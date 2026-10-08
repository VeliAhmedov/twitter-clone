import { isAxiosError } from 'axios'
import { create } from 'zustand'
import { refreshSession } from '../../api/client'
import { queryClient } from '../../api/queryClient'
import {
  SESSION_STORAGE_KEY,
  endSession,
  readStoredSession,
  setAccessToken,
  setSessionEndedHandler,
  writeStoredSession,
} from '../../api/session'
import type { RegisterRequest, Role, UserResponse } from '../../api/types'
import { fetchUserByUsername, loginRequest, logoutRequest, registerRequest } from './authApi'

type AuthStatus = 'loading' | 'authenticated' | 'anonymous'

interface AuthState {
  status: AuthStatus
  user: UserResponse | null
  role: Role | null
  /** Restores the session from the stored refresh token. Safe to call more than once. */
  bootstrap: () => Promise<void>
  login: (username: string, password: string) => Promise<void>
  /** Creates the account, then logs in. */
  register: (values: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
}

const anonymous = { status: 'anonymous', user: null, role: null } as const

let bootstrapPromise: Promise<void> | null = null

async function restoreSession(): Promise<void> {
  const stored = readStoredSession()
  if (!stored) {
    useAuthStore.setState(anonymous)
    return
  }
  try {
    const auth = await refreshSession()
    const user = await fetchUserByUsername(stored.username)
    useAuthStore.setState({ status: 'authenticated', user, role: auth.role })
  } catch (error) {
    // A refresh the server rejected already ended the session. A network error keeps
    // the stored token so the next reload can try again. A deleted account ends it here.
    if (isAxiosError(error) && error.response?.status === 404) endSession()
    useAuthStore.setState(anonymous)
  }
}

export const useAuthStore = create<AuthState>()((set, get) => ({
  status: 'loading',
  user: null,
  role: null,

  bootstrap: () => {
    bootstrapPromise ??= restoreSession()
    return bootstrapPromise
  },

  login: async (username, password) => {
    const auth = await loginRequest({ username, password })
    setAccessToken(auth.accessToken)
    writeStoredSession({ refreshToken: auth.refreshToken, username })
    try {
      const user = await fetchUserByUsername(username)
      set({ status: 'authenticated', user, role: auth.role })
    } catch (error) {
      endSession()
      throw error
    }
  },

  register: async (values) => {
    await registerRequest(values)
    await get().login(values.username, values.password)
  },

  logout: async () => {
    const stored = readStoredSession()
    endSession() // update the UI immediately, then tell the server
    if (stored) {
      try {
        await logoutRequest(stored.refreshToken)
      } catch {
        // server unreachable: the refresh token still expires on its own
      }
    }
  },
}))

// A dead session (failed refresh, logout, other tab) resets the UI and drops cached data.
setSessionEndedHandler(() => {
  queryClient.clear()
  useAuthStore.setState(anonymous)
})

// Logging out in one tab logs out the others.
window.addEventListener('storage', (event) => {
  if (event.key !== SESSION_STORAGE_KEY && event.key !== null) return
  if (readStoredSession() === null && useAuthStore.getState().status === 'authenticated') {
    endSession()
  }
})

//This is your frontend authentication state manager, using Zustand.
/*
Register
   ↓
Account created
   ↓
Automatically login
   ↓
Authenticated
*/