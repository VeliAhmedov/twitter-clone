import { useState, type FormEvent } from 'react'
import { getErrorMessage } from '../../api/errors'
import type { UserResponse } from '../../api/types'
import { Avatar } from '../../components/Avatar'
import { Button } from '../../components/Button'
import { useUpdateProfile } from './useUpdateProfile'
import { isHttpUrl, MAX_BIO, MAX_DISPLAY_NAME, validateProfile } from './validation'

//Provides the form for changing the display name, bio, and profile picture URL.

const fieldClass =
  'block w-full rounded-lg border border-line bg-white px-3 py-2.5 text-base outline-none placeholder:text-muted/60 focus:border-brand focus:ring-2 focus:ring-brand/25'

/** Edit display name, bio and profile picture (a link: the backend stores a URL, not a file). */
export function EditProfileForm({ user, onDone }: { user: UserResponse; onDone: () => void }) {
  const update = useUpdateProfile()
  const [displayName, setDisplayName] = useState(user.displayName)
  const [bio, setBio] = useState(user.bio ?? '')
  const [avatarURL, setAvatarURL] = useState(user.avatarURL ?? '')
  const [problem, setProblem] = useState<string | null>(null)

  function submit(event: FormEvent) {
    event.preventDefault()
    const values = { displayName: displayName.trim(), bio: bio.trim(), avatarURL: avatarURL.trim() }

    const message = validateProfile(values)
    setProblem(message)
    if (message) return

    const unchanged =
      values.displayName === user.displayName &&
      values.bio === (user.bio ?? '') &&
      values.avatarURL === (user.avatarURL ?? '')
    if (unchanged) {
      onDone()
      return
    }

    update.mutate({ user, values }, { onSuccess: onDone })
  }

  const preview = avatarURL.trim()

  return (
    <form onSubmit={submit} className="space-y-4 rounded-xl border border-line bg-white p-5" noValidate>
      <h2 className="text-lg font-bold tracking-tight">Edit profile</h2>

      <div className="flex items-start gap-4">
        {/* key restarts the avatar's "failed to load" memory whenever the link changes */}
        <Avatar key={preview} username={user.username} src={isHttpUrl(preview) ? preview : null} size="xl" />
        <div className="flex-1 space-y-1.5">
          <label className="block space-y-1.5">
            <span className="text-sm font-medium">Profile image link</span>
            <input
              type="url"
              value={avatarURL}
              onChange={(event) => setAvatarURL(event.target.value)}
              placeholder="https://…"
              aria-describedby="avatar-hint"
              className={fieldClass}
            />
          </label>
          <p id="avatar-hint" className="text-sm text-muted">
            Paste a link to an image. Leave empty to remove it.
          </p>
        </div>
      </div>

      <label className="block space-y-1.5">
        <span className="text-sm font-medium">Display name</span>
        <input
          value={displayName}
          onChange={(event) => setDisplayName(event.target.value)}
          maxLength={MAX_DISPLAY_NAME}
          className={fieldClass}
        />
      </label>

      <div className="space-y-1.5">
        <label className="block space-y-1.5">
          <span className="text-sm font-medium">Bio</span>
          <textarea
            value={bio}
            onChange={(event) => setBio(event.target.value)}
            rows={3}
            aria-describedby="bio-count"
            className={`${fieldClass} resize-none`}
          />
        </label>
        <p id="bio-count" className="text-right text-sm text-muted">
          {bio.length}/{MAX_BIO}
        </p>
      </div>

      <p className="text-sm text-muted">@{user.username} · usernames can’t be changed.</p>

      {(problem || update.error) && (
        <p role="alert" className="text-sm text-danger">
          {problem ?? getErrorMessage(update.error)}
        </p>
      )}

      <div className="flex justify-end gap-2">
        <Button variant="quiet" onClick={onDone} disabled={update.isPending}>
          Cancel
        </Button>
        <Button type="submit" loading={update.isPending}>
          Save
        </Button>
      </div>
    </form>
  )
}