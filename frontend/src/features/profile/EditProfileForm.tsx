
import { useState, type SubmitEvent  } from 'react'
import { getErrorMessage } from '../../api/errors'
import type { UserResponse } from '../../api/types'
import { Avatar } from '../../components/Avatar'
import { Button } from '../../components/Button'
import { useUpdateProfile } from './useUpdateProfile'
import { MAX_BIO, MAX_DISPLAY_NAME, validateProfile } from './validation'

//Provides the form for changing the display name, bio, and profile picture URL.

const fieldClass =
  'block w-full rounded-lg border border-line bg-white px-3 py-2.5 text-base outline-none placeholder:text-muted/60 focus:border-brand focus:ring-2 focus:ring-brand/25'

const allowedTypes = ['image/jpeg', 'image/png', 'image/gif']
const maxFileSize = 5 * 1024 * 1024

export function EditProfileForm({
  user,
  onDone,
}: {
  user: UserResponse
  onDone: () => void
}) {
  const update = useUpdateProfile()
  const [displayName, setDisplayName] = useState(user.displayName)
  const [bio, setBio] = useState(user.bio ?? '')
  const [selectedFile, setSelectedFile] = useState<File | null>(null)
  const [previewURL, setPreviewURL] = useState<string | null>(null)
  const [problem, setProblem] = useState<string | null>(null)

  function selectAvatar(file: File | null) {
    setProblem(null)
    setSelectedFile(file)

    if (previewURL) URL.revokeObjectURL(previewURL)
    setPreviewURL(null)

    if (!file) return

    if (!allowedTypes.includes(file.type)) {
      setSelectedFile(null)
      setProblem('Only JPEG, PNG, and GIF images are allowed.')
      return
    }

    if (file.size > maxFileSize) {
      setSelectedFile(null)
      setProblem('Image cannot exceed 5 MB.')
      return
    }

    setPreviewURL(URL.createObjectURL(file))
  }

  function submit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault()

    const values = {
      displayName: displayName.trim(),
      bio: bio.trim(),
    }

    const message = validateProfile(values)
    setProblem(message)
    if (message) return

    const unchanged =
      values.displayName === user.displayName &&
      values.bio === (user.bio ?? '') &&
      selectedFile === null

    if (unchanged) {
      onDone()
      return
    }

    update.mutate(
      { user, values, avatar: selectedFile },
      { onSuccess: onDone },
    )
  }

  return (
    <form
      onSubmit={submit}
      className="space-y-4 rounded-xl border border-line bg-white p-5"
      noValidate
    >
      <h2 className="text-lg font-bold tracking-tight">Edit profile</h2>

      <div className="flex items-start gap-4">
        <Avatar
          username={user.username}
          src={previewURL ?? user.avatarURL}
          size="xl"
        />

        <div className="flex-1 space-y-1.5">
          <label htmlFor="avatar" className="block text-sm font-medium">
            Profile picture
          </label>

          <input
            id="avatar"
            type="file"
            accept="image/jpeg,image/png,image/gif"
            onChange={(event) => {
              selectAvatar(event.target.files?.[0] ?? null)
            }}
            className={fieldClass}
          />

          <p className="text-sm text-muted">
            JPEG, PNG, or GIF. Maximum size: 5 MB.
          </p>

          {selectedFile && (
            <p className="text-sm text-muted">
              Selected: {selectedFile.name}
            </p>
          )}
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
            maxLength={MAX_BIO}
            rows={3}
            aria-describedby="bio-count"
            className={`${fieldClass} resize-none`}
          />
        </label>

        <p id="bio-count" className="text-right text-sm text-muted">
          {bio.length}/{MAX_BIO}
        </p>
      </div>

      <p className="text-sm text-muted">
        @{user.username} · usernames can’t be changed.
      </p>

      {(problem || update.error) && (
        <p role="alert" className="text-sm text-danger">
          {problem ?? getErrorMessage(update.error)}
        </p>
      )}

      <div className="flex justify-end gap-2">
        <Button
          variant="quiet"
          onClick={onDone}
          disabled={update.isPending}
        >
          Cancel
        </Button>

        <Button type="submit" loading={update.isPending}>
          Save
        </Button>
      </div>
    </form>
  )
}
