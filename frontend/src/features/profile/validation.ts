export const MAX_DISPLAY_NAME = 30
export const MAX_BIO = 150

//validates profile-editing fields before submitting them.
//though backend part does its part, frontend also shows when there is problem to inform user

export interface ProfileUpdate {
  displayName: string
  bio: string
  /** Empty string removes the picture. */
  avatarURL: string
}

export function isHttpUrl(value: string): boolean {
  try {
    const url = new URL(value)
    return url.protocol === 'http:' || url.protocol === 'https:'
  } catch {
    return false
  }
}

/** First problem as a sentence, or null when the values can be saved. */
export function validateProfile(values: ProfileUpdate): string | null {
  if (!values.displayName) return 'Display name can’t be empty.'
  if (values.displayName.length > MAX_DISPLAY_NAME) return `Display name can be at most ${MAX_DISPLAY_NAME} characters.`
  if (values.bio.length > MAX_BIO) return `Bio can be at most ${MAX_BIO} characters.`
  if (values.avatarURL && !isHttpUrl(values.avatarURL)) return 'The image link must start with http:// or https://.'
  return null
}