import { useState } from 'react'
import { getErrorMessage } from '../../api/errors'
import { Avatar } from '../../components/Avatar'
import { useAuthStore } from '../auth/authStore'
import { buildCreateRequest } from './draft'
import { TweetForm } from './TweetForm'
import { useCreateTweet } from './tweetMutations'

// ComposeBox is the area for create/post a new tweet. It shows the user's avatar and a TweetForm.

export function ComposeBox() {
  const user = useAuthStore((state) => state.user)
  const create = useCreateTweet()
  // Changing the key remounts the form, which empties it after a successful post.
  const [formKey, setFormKey] = useState(0)

  return (
    <section className="flex gap-3 rounded-xl border border-line bg-white p-4">
      {user && <Avatar username={user.username} src={user.avatarURL} />}
      <div className="min-w-0 flex-1">
        <TweetForm
          key={formKey}
          placeholder="What's on your mind?"
          submitLabel="Post"
          pending={create.isPending}
          error={create.error ? getErrorMessage(create.error) : null}
          onSubmit={(draft) =>
            create.mutate(buildCreateRequest(draft), {
              onSuccess: () => setFormKey((key) => key + 1),
            })
          }
        />
      </div>
    </section>
  )
}