const apiUrl: string | undefined = import.meta.env.VITE_API_URL

if (!apiUrl) {
  throw new Error(
    'VITE_API_URL is not set. Add it to frontend/.env.development and restart `npm run dev`.',
  )
}

// No trailing slash, so `${API_URL}/api/...` never produces a double slash.
export const API_URL: string = apiUrl.replace(/\/+$/, '')