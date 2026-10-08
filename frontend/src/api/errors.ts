import { isAxiosError } from 'axios'

/** Turns anything thrown by an API call into one sentence that is safe to show a user. */
export function getErrorMessage(
  error: unknown,
  fallback = 'Something went wrong. Try again.',
): string {
  if (!isAxiosError(error)) return fallback

  if (!error.response) {
    return "Can't reach the server. Check your connection and try again."
  }

  const { status, data } = error.response
  if (status === 429) return 'Too many attempts. Wait a minute, then try again.'

  // The backend's error body is expected to carry a human-readable `message`.
  if (typeof data === 'object' && data !== null && 'message' in data) {
    const { message } = data as { message: unknown }
    if (typeof message === 'string' && message.trim()) return message
  }

  if (status >= 500) return 'The server had a problem. Try again in a moment.'
  return fallback
}