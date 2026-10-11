import { getErrorMessage } from '../../api/errors'
import { Button } from '../../components/Button'
import { CommentCard } from './CommentCard'
import { useComments } from './useComments'

/*CommentList is a component that displays a list of comments for a specific tweet. 
It uses the useComments hook to fetch comments and handles loading, error, and empty states. 
It also supports infinite scrolling by allowing users to load more comments if available.
*/

export function CommentList({ tweetId }: { tweetId: number }) {
  const comments = useComments(tweetId, true)

  if (comments.status === 'pending') return <p className="text-muted">Loading replies…</p>

  if (comments.status === 'error') {
    return (
      <div className="space-y-3">
        <p className="text-danger">{getErrorMessage(comments.error, "Couldn't load replies.")}</p>
        <Button variant="quiet" onClick={() => void comments.refetch()}>
          Try again
        </Button>
      </div>
    )
  }

  if (comments.comments.length === 0) {
    return <p className="text-muted">No replies yet. Be the first.</p>
  }

  return (
    <div className="space-y-4">
      <div className="divide-y divide-line rounded-xl border border-line bg-white px-4">
        {comments.comments.map((comment) => (
          <CommentCard key={comment.id} comment={comment} />
        ))}
      </div>
      {comments.hasNextPage && (
        <Button
          variant="quiet"
          loading={comments.isFetchingNextPage}
          onClick={() => void comments.fetchNextPage()}
        >
          Show more replies
        </Button>
      )}
    </div>
  )
}