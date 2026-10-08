import axios, { isAxiosError, type InternalAxiosRequestConfig } from 'axios'
import { API_URL } from './config'
import {
  endSession,
  getAccessToken,
  readStoredSession,
  setAccessToken,
  writeStoredSession,
} from './session'
import type { AuthResponse } from './types'

const AUTH_PREFIX = '/api/auth/'

/** The instance every feature uses. Attaches the token and recovers from expired ones. */
export const api = axios.create({ baseURL: API_URL })

// No interceptors here, so a failing refresh can never trigger another refresh.
const bare = axios.create({ baseURL: API_URL })

api.interceptors.request.use((config) => {
  const token = getAccessToken()
  // Auth endpoints are public; an expired bearer token must not get in their way.
  if (token && !config.url?.startsWith(AUTH_PREFIX)) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

async function doRefresh(): Promise<AuthResponse> {
  // Read at the last moment: another tab may have rotated the token while we waited.
  const stored = readStoredSession()
  if (!stored) throw new Error('No stored session')

  try {
    const { data } = await bare.post<AuthResponse>('/api/auth/refresh', {
      refreshToken: stored.refreshToken,
    })
    setAccessToken(data.accessToken)
    writeStoredSession({ ...stored, refreshToken: data.refreshToken })
    return data
  } catch (error) {
    // The server answered and said no: the session is dead. A network error or 5xx
    // keeps the stored token so a later attempt can still work.
    const status = isAxiosError(error) ? error.response?.status : undefined
    if (status === 400 || status === 401 || status === 403) endSession()
    throw error
  }
}

// Serialises refreshes across browser tabs. Needed because the backend rotates the
// refresh token and treats a second use of the same token as theft (revokes everything).
function runExclusive<T>(task: () => Promise<T>): Promise<T> {
  if ('locks' in navigator) {
    return navigator.locks.request('twittvl-refresh', task)
  }
  return task()
}

let inFlight: Promise<AuthResponse> | null = null

/**
 * Trades the stored refresh token for a new token pair. Concurrent callers share one
 * request, because firing two refreshes with the same token trips the backend's
 * reuse detection and logs the user out everywhere.
 */
export function refreshSession(): Promise<AuthResponse> {
  inFlight ??= runExclusive(doRefresh).finally(() => {
    inFlight = null
  })
  return inFlight
}

interface RetriableRequest extends InternalAxiosRequestConfig {
  _retried?: boolean
}

api.interceptors.response.use(
  (response) => response,
  async (error: unknown) => {
    if (!isAxiosError(error) || error.response?.status !== 401 || !error.config) {
      throw error
    }

    const original = error.config as RetriableRequest
    // Wrong password on /login also returns 401: never "refresh" for auth calls,
    // and never retry the same request twice.
    if (original._retried || original.url?.startsWith(AUTH_PREFIX)) throw error
    original._retried = true

    try {
      await refreshSession()
    } catch {
      throw error // not logged in, or the session just ended: surface the original 401
    }
    return api(original) // the request interceptor attaches the new token
  },
)