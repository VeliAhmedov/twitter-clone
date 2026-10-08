import { useState, type SubmitEvent } from 'react'
import { Link } from 'react-router-dom'
import { getErrorMessage } from '../../api/errors'
import { Button } from '../../components/Button'
import { TextField } from '../../components/TextField'
import { AuthShell } from './AuthShell'
import { useAuthStore } from './authStore'

//Actually implements the login page. 
//It uses the AuthShell layout and includes a form with username and password fields, 
//as well as a submit button. It handles form submission, displays error messages, 
//and manages loading state during the login process.

export function LoginPage() {
  const login = useAuthStore((state) => state.login)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault()
    if (submitting) return
    setError(null)
    setSubmitting(true)
    try {
      await login(username.trim(), password)
      // GuestRoute sees the new status and redirects; nothing to do here.
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthShell
      title="Log in"
      footer={
        <>
          New here?{' '}
          <Link to="/register" className="font-semibold text-brand hover:underline">
            Create an account
          </Link>
        </>
      }
    >
      <form onSubmit={handleSubmit} className="space-y-5">
        {error && (
          <p
            role="alert"
            className="rounded-lg border border-danger/30 bg-danger/10 px-3 py-2 text-sm text-danger"
          >
            {error}
          </p>
        )}
        <TextField
          label="Username"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          autoComplete="username"
          autoCapitalize="none"
          spellCheck={false}
          required
          autoFocus
        />
        <TextField
          label="Password"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          autoComplete="current-password"
          required
        />
        <Button type="submit" loading={submitting} className="w-full">
          Log in
        </Button>
      </form>
    </AuthShell>
  )
}