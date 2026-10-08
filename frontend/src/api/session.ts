// Where the login session lives.
//
//   access token  -> module memory only (gone on reload, never in localStorage)
//   refresh token -> localStorage, because the backend returns it in the JSON body
//
// This file has no imports from the rest of the app on purpose, so both the axios
// client and the auth store can use it without creating an import cycle.

export const SESSION_STORAGE_KEY = 'twittvl.session'

export interface StoredSession {
  refreshToken: string
  username: string
}

let accessToken: string | null = null
let sessionEndedHandler: (() => void) | null = null

export function getAccessToken(): string | null {
  return accessToken
}

export function setAccessToken(token: string | null): void {
  accessToken = token
}

function isStoredSession(value: unknown): value is StoredSession {
  if (typeof value !== 'object' || value === null) return false
  const candidate = value as Partial<StoredSession>
  return typeof candidate.refreshToken === 'string' && typeof candidate.username === 'string'
}

export function readStoredSession(): StoredSession | null {
  try {
    const raw = localStorage.getItem(SESSION_STORAGE_KEY)
    if (!raw) return null
    const parsed: unknown = JSON.parse(raw)
    return isStoredSession(parsed) ? parsed : null
  } catch {
    // storage unavailable or the value is corrupted: treat as "no session"
    return null
  }
}

export function writeStoredSession(session: StoredSession): void {
  try {
    localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(session))
  } catch {
    // storage full or blocked: the user stays logged in until reload
  }
}

function clearStoredSession(): void {
  try {
    localStorage.removeItem(SESSION_STORAGE_KEY)
  } catch {
    // nothing to clear
  }
}

/** The auth store registers a callback here so the UI reacts when a session dies. */
export function setSessionEndedHandler(handler: () => void): void {
  sessionEndedHandler = handler
}

/** Forget every token and tell the UI. Used by logout, failed refresh and cross-tab logout. */
export function endSession(): void {
  accessToken = null
  clearStoredSession()
  sessionEndedHandler?.()
}