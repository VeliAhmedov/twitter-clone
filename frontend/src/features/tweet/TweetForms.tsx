import { getErrorMessage } from '../../api/errors'
import type { TweetResponse } from '../../api/types'
import { Button } from '../../components/Button'
import { buildCreateRequest, buildEditRequest } from './draft'
import { TweetForm } from './TweetForm'
import { useCreateTweet, useDeleteTweet, useUpdateTweet } from './tweetMutations'

/* TweetForm is shared form and this is specialized forms for editing, quoting, and deleting tweets. 
It uses the shared TweetForm component and the mutation hooks to handle the backend operations. */

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
  // Stay disabled after success too: the card disappears once the list reloads.
  const busy = remove.isPending || remove.isSuccess

  return (
    <div className="mt-3 space-y-2 rounded-xl border border-danger/30 bg-danger/10 p-3 text-sm">
      <p className="text-danger">Delete this post? This can't be undone.</p>
      {remove.error && (
        <p role="alert" className="text-danger">
          {getErrorMessage(remove.error)}
        </p>
      )}
      <div className="flex gap-2">
        <Button variant="quiet" onClick={onCancel} disabled={busy}>
          Cancel
        </Button>
        <Button
          variant="danger"
          loading={busy}
          onClick={() => remove.mutate(tweet.id, { onSuccess: () => onDeleted?.() })}
        >
          Delete
        </Button>
      </div>
    </div>
  )
}