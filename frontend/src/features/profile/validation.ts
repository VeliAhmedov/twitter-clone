export const MAX_DISPLAY_NAME = 30
export const MAX_BIO = 150

// Validates profile-editing fields before submitting.
// The backend also validates these fields.

export interface ProfileUpdate {
  displayName: string
  bio: string
}

/** First problem as a sentence, or null when the values can be saved. */
export function validateProfile(values: ProfileUpdate): string | null {
  if (!values.displayName) {
    return 'Display name can’t be empty.'
  }

  if (values.displayName.length > MAX_DISPLAY_NAME) {
    return `Display name can be at most ${MAX_DISPLAY_NAME} characters.`
  }

  if (values.bio.length > MAX_BIO) {
    return `Bio can be at most ${MAX_BIO} characters.`
  }

  return null
}
