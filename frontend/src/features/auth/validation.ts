// Same rules as the backend's RegisterRequest, so most mistakes never reach the server.
// Username is stricter than the backend (letters, digits, underscore) because it
// appears in the URL as /@username.

export type RegisterField = 'username' | 'displayName' | 'email' | 'password'
export type RegisterValues = Record<RegisterField, string>
export type FieldErrors = Partial<Record<RegisterField, string>>

const EMAIL_PATTERN = /^[^@\s]+@[^@\s]+\.[a-zA-Z]{2,}$/
const USERNAME_PATTERN = /^[A-Za-z0-9_]+$/

export function validateRegister(values: RegisterValues): FieldErrors {
  const errors: FieldErrors = {}
  const username = values.username.trim()
  const displayName = values.displayName.trim()
  const email = values.email.trim()

  if (username.length < 3 || username.length > 30) {
    errors.username = 'Use 3 to 30 characters.'
  } else if (!USERNAME_PATTERN.test(username)) {
    errors.username = 'Use only letters, numbers and underscores.'
  }

  if (displayName.length < 1 || displayName.length > 30) {
    errors.displayName = 'Use 1 to 30 characters.'
  }

  if (!EMAIL_PATTERN.test(email)) {
    errors.email = 'Enter an email like name@example.com.'
  }

  if (values.password.length < 8 || values.password.length > 50) {
    errors.password = 'Use 8 to 50 characters.'
  }

  return errors
}
//Client-side validation that prevents invalid data from being sent to the server. 
//It checks the length and format of the username, display name, email, and password fields 
//and returns an object containing any validation errors.