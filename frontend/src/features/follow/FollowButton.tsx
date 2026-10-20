import { getErrorMessage } from '../../api/errors'
import { Button } from '../../components/Button'
import { useFollowStatus, useToggleFollow } from './useFollow'

/** Follow / Following button for someone else's profile. */
export function FollowButton({ userId }: { userId: number }) {
  const status = useFollowStatus(userId, true)
  const toggle = useToggleFollow()
  const following = status.data

  if (status.isError) return <p className="text-sm text-muted">Follow is unavailable right now.</p>
  if (following === undefined) return <Button variant="quiet" disabled>Follow</Button>

  return (
    <div className="flex flex-col items-end gap-1">
      <Button
        variant={following ? 'quiet' : 'primary'}
        aria-pressed={following}
        loading={toggle.isPending}
        onClick={() => toggle.mutate({ userId, enable: !following })}
      >
        {following ? 'Following' : 'Follow'}
      </Button>
      {toggle.error && (
        <p role="alert" className="text-sm text-danger">
          {getErrorMessage(toggle.error)}
        </p>
      )}
    </div>
  )
}