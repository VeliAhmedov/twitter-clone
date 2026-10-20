import { getErrorMessage } from '../../api/errors'
import type { CommentResponse } from '../../api/types'
import { buildQuoteCommentRequest } from '../tweet/draft'
import { TweetForm } from '../tweet/TweetForm'
import { useCreateTweet } from '../tweet/tweetMutations'

/*
 * This module provides a React component for quoting a comment in a new tweet. 
 * The `QuoteCommentForm` component allows users to create a new post that embeds an existing comment, 
 * with the option to add their own text (up to 280 characters). 
 */

/** Quote a comment: creates a new post (up to 280 characters) that embeds the comment. */
export function QuoteCommentForm({ comment, onDone }: { comment: CommentResponse; onDone: () => void }) {
  const create = useCreateTweet()

  return (
    <TweetForm
      autoFocus
      placeholder="Add a comment"
      submitLabel="Quote"
      pending={create.isPending}
      error={create.error ? getErrorMessage(create.error) : null}
      onCancel={onDone}
      onSubmit={(draft) =>
        create.mutate(buildQuoteCommentRequest(draft, comment.id), { onSuccess: onDone })
      }
    />
  )
}