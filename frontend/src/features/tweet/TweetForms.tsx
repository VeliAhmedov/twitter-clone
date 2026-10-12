import { getErrorMessage } from '../../api/errors'
import type { TweetResponse } from '../../api/types'
import { ConfirmDelete } from '../../components/ConfirmDelete'
import { buildCommentRequest, buildCreateRequest, buildEditRequest, MAX_COMMENT_LENGTH } from './draft'
import { TweetForm } from './TweetForm'
import { useCreateTweet, useDeleteTweet, useUpdateTweet } from './tweetMutations'
import { useCreateComment } from '../comment/commentMutations'

/* TweetForm is shared form and this is specialized forms for editing, quoting, and deleting tweets. 
It uses the shared TweetForm component and the mutation hooks to handle the backend operations. */

/** Quick reply box that opens under a card, so you don't have to open the tweet page. */
export function ReplyToTweetForm({ tweet, onDone }: { tweet: TweetResponse; onDone: () => void }) {
  const create = useCreateComment(tweet.id)

  return (
    <TweetForm
      autoFocus
      maxLength={MAX_COMMENT_LENGTH}
      placeholder={`Reply to @${tweet.username}`}
      submitLabel="Reply"
      pending={create.isPending}
      error={create.error ? getErrorMessage(create.error) : null}
      onCancel={onDone}
      onSubmit={(draft) => create.mutate(buildCommentRequest(draft), { onSuccess: onDone })}
    />
  )
}

export function EditTweetForm({ tweet, onDone }: { tweet: TweetResponse; onDone: () => void }) {
  const update = useUpdateTweet()

  return (
    <TweetForm
      autoFocus
      initial={{ content: tweet.content ?? '', image: tweet.imageUrl ?? '' }}
      placeholder="Edit your post"
      submitLabel="Save"
      pending={update.isPending}
      error={update.error ? getErrorMessage(update.error) : null}
      onCancel={onDone}
      onSubmit={(draft) => {
        const body = buildEditRequest(tweet, draft)
        if (!body) {
          onDone() // nothing changed, so don't mark the post as edited
          return
        }
        update.mutate({ id: tweet.id, body }, { onSuccess: onDone })
      }}
    />
  )
}
export function QuoteTweetForm({ tweet, onDone }: { tweet: TweetResponse; onDone: () => void }) {
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
        create.mutate(buildCreateRequest(draft, tweet.id), { onSuccess: onDone })
      }
    />
  )
}

export function DeleteTweetConfirm({
  tweet,
  onCancel,
  onDeleted,
}: {
  tweet: TweetResponse
  onCancel: () => void
  onDeleted?: () => void
}) {
  const remove = useDeleteTweet()

  return (
    <ConfirmDelete
      what="post"
      // Stay disabled after success too: the card disappears once the list reloads.
      pending={remove.isPending || remove.isSuccess}
      error={remove.error ? getErrorMessage(remove.error) : null}
      onCancel={onCancel}
      onConfirm={() => remove.mutate(tweet.id, { onSuccess: () => onDeleted?.() })}
    />
  )
}
