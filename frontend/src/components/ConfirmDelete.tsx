import { Button } from './Button'

/* This component is a confirmation dialog for deleting a post or comment. 
It displays a warning message, an optional error message, and two buttons: Cancel and Delete. 
The Delete button is disabled while the deletion is pending. 
The onConfirm and onCancel callbacks are called when the respective buttons are clicked. */

interface ConfirmDeleteProps {
  what: string // "post", "comment"
  pending: boolean
  error: string | null
  onConfirm: () => void
  onCancel: () => void
}

export function ConfirmDelete({ what, pending, error, onConfirm, onCancel }: ConfirmDeleteProps) {
  return (
    <div className="mt-3 space-y-2 rounded-xl border border-danger/30 bg-danger/10 p-3 text-sm">
      <p className="text-danger">Delete this {what}? This can't be undone.</p>
      {error && (
        <p role="alert" className="text-danger">
          {error}
        </p>
      )}
      <div className="flex gap-2">
        <Button variant="quiet" onClick={onCancel} disabled={pending}>
          Cancel
        </Button>
        <Button variant="danger" loading={pending} onClick={onConfirm}>
          Delete
        </Button>
      </div>
    </div>
  )
}