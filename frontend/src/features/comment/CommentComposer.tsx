import { useState } from 'react'
import { getErrorMessage } from '../../api/errors'
import type { CommentRequest } from '../../api/types'
import { Avatar } from '../../components/Avatar'
import { useAuthStore } from '../auth/authStore'
import { buildCommentRequest, MAX_COMMENT_LENGTH } from '../tweet/draft'
import { TweetForm } from '../tweet/TweetForm'
import { useCreateComment, useCreateReply } from './commentMutations'

/* 
*This file contains the CommentComposer and ReplyComposer components, 
*which provide a text box for users to write and submit comments or replies.
*It uses the TweetForm component for the input and the mutation hooks to handle the backend operations. 
*/

interface ComposerMutation {
  isPending: boolean
  error: unknown
  mutate: (body: CommentRequest, options: { onSuccess: () => void }) => void
}

function ComposerShell({ placeholder, mutation }: { placeholder: string; mutation: ComposerMutation }) {
  const user = useAuthStore((state) => state.user)
  // Changing the key remounts the form, which empties it after a successful reply.
  const [formKey, setFormKey] = useState(0)

  return (
    <section className="flex gap-3 rounded-xl border border-line bg-white p-4">
      {user && <Avatar username={user.username} src={user.avatarURL} />}
      <div className="min-w-0 flex-1">
        <TweetForm
          key={formKey}
          maxLength={MAX_COMMENT_LENGTH}
          placeholder={placeholder}
          submitLabel="Reply"
          pending={mutation.isPending}
          error={mutation.error ? getErrorMessage(mutation.error) : null}
          onSubmit={(draft) =>
            mutation.mutate(buildCommentRequest(draft), {
              onSuccess: () => setFormKey((key) => key + 1),
            })
          }
        />
      </div>
    </section>
  )
}

/** Reply box under a tweet. */
export function CommentComposer({ tweetId }: { tweetId: number }) {
  const create = useCreateComment(tweetId)
  return <ComposerShell placeholder="Post your reply" mutation={create} />
}

/** Reply box under a comment's own page. */
export function ReplyComposer({ commentId }: { commentId: number }) {
  const create = useCreateReply(commentId)
  return <ComposerShell placeholder="Post your reply" mutation={create} />
}