import { useState } from 'react'
import { getErrorMessage } from '../../api/errors'
import { Avatar } from '../../components/Avatar'
import { useAuthStore } from '../auth/authStore'
import { buildCommentRequest, MAX_COMMENT_LENGTH } from '../tweet/draft'
import { TweetForm } from '../tweet/TweetForm'
import { useCreateComment } from './commentMutations'

/*CommentComposer is a component that allows users to compose and submit new comments on a tweet. 
It displays the user's avatar and a form for entering the comment text. 
The form handles submission, validation, and error display. Upon successful submission, 
the form resets to allow for new comments.
*/

export function CommentComposer({ tweetId }: { tweetId: number }) {
  const user = useAuthStore((state) => state.user)
  const create = useCreateComment(tweetId)
  // Changing the key remounts the form, which empties it after a successful reply.
  const [formKey, setFormKey] = useState(0)

  return (
    <section className="flex gap-3 rounded-xl border border-line bg-white p-4">
      {user && <Avatar username={user.username} src={user.avatarURL} />}
      <div className="min-w-0 flex-1">
        <TweetForm
          key={formKey}
          maxLength={MAX_COMMENT_LENGTH}
          placeholder="Post your reply"
          submitLabel="Reply"
          pending={create.isPending}
          error={create.error ? getErrorMessage(create.error) : null}
          onSubmit={(draft) =>
            create.mutate(buildCommentRequest(draft), {
              onSuccess: () => setFormKey((key) => key + 1),
            })
          }
        />
      </div>
    </section>
  )
}