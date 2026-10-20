import type { CommentRequest, TweetRequest } from '../../api/types'

/** To validate and work with tweet drafts before they are sent to the backend */

export const MAX_TWEET_LENGTH = 280
export const MAX_COMMENT_LENGTH = 150 // the backend limits comments to 150

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
export function validateDraft(draft: TweetDraft, maxLength: number = MAX_TWEET_LENGTH): string | null {
  const content = draft.content.trim()
  const image = draft.image.trim()

  if (!content && !image) return 'Write something or add an image link.'
  if (draft.content.length > maxLength) {
    return `Keep it to ${maxLength} characters or fewer.`
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

/** Body for creating a comment or reply. The backend names the image field `url`. */
export function buildCommentRequest(draft: TweetDraft): CommentRequest {
  const content = draft.content.trim()
  const image = draft.image.trim()
  return { content: content || undefined, imageUrl: image || undefined }
}

type Original = { content: string | null; imageUrl: string | null }

// Only the fields that changed, because the backend marks something "edited" whenever a
// sent field differs. An empty string clears a field (the backend ignores null).
function changedFields(original: Original, draft: TweetDraft): { content?: string; image?: string } | null {
  const changes: { content?: string; image?: string } = {}
  const content = draft.content.trim()
  const image = draft.image.trim()

  if (content !== (original.content ?? '')) changes.content = content
  if (image !== (original.imageUrl ?? '')) changes.image = image

  return Object.keys(changes).length > 0 ? changes : null
}

/** Body for PATCH /api/tweets/{id}, or null when nothing changed. */
export function buildEditRequest(original: Original, draft: TweetDraft): TweetRequest | null {
  return changedFields(original, draft)
}

/** Body for PATCH /api/comments/{id}, or null when nothing changed. */
export function buildCommentEditRequest(original: Original, draft: TweetDraft): CommentRequest | null {
  const changes = changedFields(original, draft)
  if (!changes) return null
  return { content: changes.content, imageUrl: changes.image }
}

/** Body for quoting a comment (POST /api/tweets with quotedCommentId). */
export function buildQuoteCommentRequest(draft: TweetDraft, quotedCommentId: number): TweetRequest {
  const content = draft.content.trim()
  const image = draft.image.trim()
  return { content: content || undefined, image: image || undefined, quotedCommentId }
}