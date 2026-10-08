import { useState } from 'react'
import { isAxiosError } from 'axios'
import { api } from '../../api/client'
import { Button } from '../../components/Button'
import { useAuthStore } from '../auth/authStore'

// Placeholder. will replace with the real feed after feed fix in backend. will not return feed currently
export function HomePage() {
  const user = useAuthStore((state) => state.user)
  const role = useAuthStore((state) => state.role)
  const [result, setResult] = useState<string | null>(null)
  const [checking, setChecking] = useState(false)

  // Calls an endpoint that needs a valid token. Handy for testing the refresh flow.
  async function checkAuth() {
    setChecking(true)
    try {
      const response = await api.get('/api/bookmarks') //test whether the user is authenticated by calling an endpoint that requires authentication
      setResult(`Authenticated request worked (HTTP ${response.status}).`)
    } catch (error) {
      const status = isAxiosError(error) ? (error.response?.status ?? 'no response') : 'unknown'
      setResult(`Authenticated request failed (${status}).`)
    } finally {
      setChecking(false)
    }
  }

  return (
    <section className="space-y-4 rounded-xl border border-line bg-white p-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Hi, {user?.displayName}</h1>
        <p className="text-muted">
          Signed in as @{user?.username} ({role}). The feed goes here next.
        </p>
      </div>
      <div className="flex items-center gap-3">
        <Button variant="quiet" loading={checking} onClick={() => void checkAuth()}>
          Check login
        </Button>
        {result && <p className="text-sm text-muted">{result}</p>}
      </div>
    </section>
  )
}