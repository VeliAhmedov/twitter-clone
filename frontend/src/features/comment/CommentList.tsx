import { getErrorMessage } from '../../api/errors'
import { Button } from '../../components/Button'
import { CommentCard } from './CommentCard'
import { useComments, useReplies, type CommentPagesResult } from './useComments'
import type { CommentSort } from '../../api/types'

/* 
*This file contains React Query hooks for fetching comments and replies, featuring an infinite scrolling helper. 
*It includes the useComments and useReplies hooks to retrieve data for specific tweets or comments. 
*/

function CommentPagesView({ pages, emptyText }: { pages: CommentPagesResult; emptyText: string }) {
  if (pages.status === 'pending') return <p className="text-muted">Loading replies…</p>

  if (pages.status === 'error') {
    return (
      <div className="space-y-3">
        <p className="text-danger">{getErrorMessage(pages.error, "Couldn't load replies.")}</p>
        <Button variant="quiet" onClick={() => void pages.refetch()}>
          Try again
        </Button>
      </div>
    )
  }

  if (pages.comments.length === 0) return <p className="text-muted">{emptyText}</p>

  return (
    <div className="space-y-4">
      <div className="divide-y divide-line rounded-xl border border-line bg-white">
        {pages.comments.map((comment) => (
          <CommentCard key={comment.id} comment={comment} />
        ))}
      </div>
      {pages.hasNextPage && (
        <Button
          variant="quiet"
          loading={pages.isFetchingNextPage}
          onClick={() => void pages.fetchNextPage()}
        >
          Show more replies
        </Button>
      )}
    </div>
  )
}

/** Top-level comments on a tweet. */
export function CommentList({ tweetId, sort = 'newest' }: { tweetId: number; sort?: CommentSort }) {
  const pages = useComments(tweetId, true, sort)
  return <CommentPagesView pages={pages} emptyText="No replies yet. Be the first." />
}

/** Direct replies to one comment. */
export function ReplyList({ commentId, sort = 'newest' }: { commentId: number; sort?: CommentSort }) {
  const pages = useReplies(commentId, true, sort)
  return <CommentPagesView pages={pages} emptyText="No replies yet." />
}