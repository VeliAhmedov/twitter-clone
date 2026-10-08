import { useState, type ChangeEvent, type SubmitEvent } from 'react'
import { Link } from 'react-router-dom'
import { getErrorMessage } from '../../api/errors'
import { Button } from '../../components/Button'
import { TextField } from '../../components/TextField'
import { AuthShell } from './AuthShell'
import { useAuthStore } from './authStore'
import { validateRegister, type FieldErrors, type RegisterField, type RegisterValues } from './validation'

export function RegisterPage() {
  const register = useAuthStore((state) => state.register)
  const [values, setValues] = useState<RegisterValues>({
    username: '',
    displayName: '',
    email: '',
    password: '',
  })
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const update = (field: RegisterField) => (event: ChangeEvent<HTMLInputElement>) => {
    setValues((current) => ({ ...current, [field]: event.target.value }))
    setFieldErrors((current) => ({ ...current, [field]: undefined }))
  }

  async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault()
    if (submitting) return

    const errors = validateRegister(values)
    setFieldErrors(errors)
    if (Object.keys(errors).length > 0) return

    setError(null)
    setSubmitting(true)
    try {
      await register({
        username: values.username.trim(),
        displayName: values.displayName.trim(),
        email: values.email.trim(),
        password: values.password,
      })
      // GuestRoute sees the new status and redirects; nothing to do here.
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthShell
      title="Create your account"
      footer={
        <>
          Already have an account?{' '}
          <Link to="/login" className="font-semibold text-brand hover:underline">
            Log in
          </Link>
        </>
      }
    >
      <form onSubmit={handleSubmit} noValidate className="space-y-5">
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
          value={values.username}
          onChange={update('username')}
          error={fieldErrors.username}
          hint="Letters, numbers and underscores. This is your @handle."
          autoComplete="username"
          autoCapitalize="none"
          spellCheck={false}
          autoFocus
        />
        <TextField
          label="Display name"
          value={values.displayName}
          onChange={update('displayName')}
          error={fieldErrors.displayName}
          autoComplete="name"
        />
        <TextField
          label="Email"
          type="email"
          value={values.email}
          onChange={update('email')}
          error={fieldErrors.email}
          autoComplete="email"
        />
        <TextField
          label="Password"
          type="password"
          value={values.password}
          onChange={update('password')}
          error={fieldErrors.password}
          hint="At least 8 characters."
          autoComplete="new-password"
        />
        <Button type="submit" loading={submitting} className="w-full">
          Create account
        </Button>
      </form>
    </AuthShell>
  )
}