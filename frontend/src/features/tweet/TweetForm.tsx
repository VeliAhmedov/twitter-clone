import { useState, type SubmitEvent } from 'react'
import { Button } from '../../components/Button'
import { TextField } from '../../components/TextField'
import { MAX_TWEET_LENGTH, validateDraft, type TweetDraft } from './draft'

//shared form for posts, editing, and quoting, CREATE TYPES. 

/*It creates the text box, optional image URL field, validation messages, character counter and submit/cancel buttons.*/

interface TweetFormProps {
  initial?: TweetDraft
  placeholder: string
  submitLabel: string
  pending: boolean
  /** Error from the server, shown under the text box. */
  error: string | null
  onSubmit: (draft: TweetDraft) => void
  onCancel?: () => void
  autoFocus?: boolean
}

/** Text box + optional image link. Used for new posts, quotes and edits. */
export function TweetForm({
  initial,
  placeholder,
  submitLabel,
  pending,
  error,
  onSubmit,
  onCancel,
  autoFocus = false,
}: TweetFormProps) {
  const [content, setContent] = useState(initial?.content ?? '')
  const [image, setImage] = useState(initial?.image ?? '')
  const [showImage, setShowImage] = useState(Boolean(initial?.image))
  const [validationError, setValidationError] = useState<string | null>(null)

  const remaining = MAX_TWEET_LENGTH - content.length
  const message = validationError ?? error

  function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault()
    if (pending) return

    const problem = validateDraft({ content, image })
    setValidationError(problem)
    if (problem) return

    onSubmit({ content, image })
  }

  return (
    <form onSubmit={handleSubmit} noValidate className="space-y-3">
      <textarea
        aria-label="Post text"
        value={content}
        onChange={(event) => setContent(event.target.value)}
        placeholder={placeholder}
        rows={3}
        autoFocus={autoFocus}
        className="block w-full resize-none rounded-lg border border-line bg-white px-3 py-2.5 text-base outline-none placeholder:text-muted/60 focus:border-brand focus:ring-2 focus:ring-brand/25"
      />

      {showImage && (
        <TextField
          label="Image link"
          type="url"
          value={image}
          onChange={(event) => setImage(event.target.value)}
          placeholder="https://example.com/photo.jpg"
          autoComplete="off"
        />
      )}

      {message && (
        <p role="alert" className="text-sm text-danger">
          {message}
        </p>
      )}

      <div className="flex items-center gap-3">
        {!showImage && (
          <button
            type="button"
            onClick={() => setShowImage(true)}
            className="text-sm font-medium text-brand hover:underline"
          >
            Add image link
          </button>
        )}
        <div className="ml-auto flex items-center gap-3">
          {remaining <= 40 && (
            <span className={`text-sm tabular-nums ${remaining < 0 ? 'text-danger' : 'text-muted'}`}>
              {remaining}
            </span>
          )}
          {onCancel && (
            <Button variant="quiet" onClick={onCancel}>
              Cancel
            </Button>
          )}
          <Button type="submit" loading={pending} disabled={remaining < 0}>
            {submitLabel}
          </Button>
        </div>
      </div>
    </form>
  )
}