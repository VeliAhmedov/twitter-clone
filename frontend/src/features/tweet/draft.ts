import type { TweetRequest } from '../../api/types'

/** To validate and work with tweet drafts before they are sent to the backend */

export const MAX_TWEET_LENGTH = 280

export interface TweetDraft {
  content: string
  image: string
}

function isHttpUrl(value: string): boolean {
  try {
    const url = new URL(value)
    return url.protocol === 'http:' || url.protocol === 'https:'
  } catch {
    return false
  }
}

/** Returns a message for the first problem, or null when the draft can be sent. check validation of tweet */
export function validateDraft(draft: TweetDraft): string | null {
  const content = draft.content.trim()
  const image = draft.image.trim()

  if (!content && !image) return 'Write something or add an image link.'
  if (draft.content.length > MAX_TWEET_LENGTH) {
    return `Keep it to ${MAX_TWEET_LENGTH} characters or fewer.`
  }
  if (image && !isHttpUrl(image)) return 'The image link must start with http:// or https://.'
  return null
}

/** Body for POST /api/tweets. Blank fields are left out. convert format to requestBody */
export function buildCreateRequest(draft: TweetDraft, quotedTweetId?: number): TweetRequest {
  const content = draft.content.trim()
  const image = draft.image.trim()
  return {
    content: content || undefined,
    image: image || undefined,
    quotedTweetId,
  }
}

/**
 * Body for PATCH /api/tweets/{id}: only the fields that changed, because the backend
 * marks a tweet "edited" whenever a sent field differs. An empty string clears a field
 * (the backend ignores null, so null cannot be used to remove an image).
 * Returns null when nothing changed. 
 */
export function buildEditRequest(
  original: { content: string | null; imageUrl: string | null },
  draft: TweetDraft,
): TweetRequest | null {
  const request: TweetRequest = {}
  const content = draft.content.trim()
  const image = draft.image.trim()

  if (content !== (original.content ?? '')) request.content = content
  if (image !== (original.imageUrl ?? '')) request.image = image

  return Object.keys(request).length > 0 ? request : null
}