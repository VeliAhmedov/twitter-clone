import { useQuery } from '@tanstack/react-query'
import { isAxiosError } from 'axios'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getErrorMessage } from '../../api/errors'
import { Button } from '../../components/Button'
import { TweetCard } from '../tweet/TweetCard'
import { fetchTweet } from '../tweet/tweetApi'
import { CommentCard } from './CommentCard'
import { ReplyComposer } from './CommentComposer'
import { ReplyList } from './CommentList'
import { useAncestors, useComment } from './useConversation'
import { useState } from 'react'
import type { CommentSort } from '../../api/types'
import { SortMenu } from '../../components/SortMenu'

/*
 * The page of one comment, like a tweet's page: the whole conversation above it
 * (the tweet, then each comment on the way down), the comment itself, a reply box,
 * and its direct replies below. Click any post in the chain to open it.
 */

function BackLink() {
  return (
    <Link to="/" className="mb-4 inline-block text-sm font-semibold text-brand hover:underline">
      ← Home
    </Link>
  )
}

export function CommentPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const commentId = Number(id)
  const [sort, setSort] = useState<CommentSort>('newest')
  const validId = Number.isInteger(commentId) && commentId > 0

  const commentQuery = useComment(commentId, validId)
  const comment = commentQuery.data
  const tweetQuery = useQuery({
    queryKey: ['tweet', comment?.tweetId],
    queryFn: () => fetchTweet(comment?.tweetId ?? 0),
    enabled: comment !== undefined,
    retry: false,
  })
  const ancestorsQuery = useAncestors(comment)

  const notFound =
    !validId ||
    (commentQuery.isError &&
      isAxiosError(commentQuery.error) &&
      commentQuery.error.response?.status === 404)

  if (notFound) {
    return (
      <>
        <BackLink />
        <p className="rounded-xl border border-line bg-white p-6 text-muted">
          This comment doesn't exist. It may have been deleted.
        </p>
      </>
    )
  }

  const failure = commentQuery.error ?? tweetQuery.error ?? ancestorsQuery.error
  if (failure) {
    return (
      <>
        <BackLink />
        <div role="alert" className="space-y-3 rounded-xl border border-line bg-white p-6">
          <p className="text-danger">{getErrorMessage(failure, "Couldn't load this conversation.")}</p>
          <Button
            variant="quiet"
            onClick={() => {
              void commentQuery.refetch()
              void tweetQuery.refetch()
              void ancestorsQuery.refetch()
            }}
          >
            Try again
          </Button>
        </div>
      </>
    )
  }

  const tweet = tweetQuery.data
  const ancestors = ancestorsQuery.data
  if (!comment || !tweet || !ancestors) {
    return (
      <>
        <BackLink />
        <p className="text-muted">Loading…</p>
      </>
    )
  }

  // After deleting this comment, go to the post above it.
  const leave = () =>
    navigate(comment.parentCommentId ? `/comment/${comment.parentCommentId}` : `/tweet/${comment.tweetId}`)

  return (
    <>
      <BackLink />
      <div className="space-y-4">
        <div className="rounded-xl border border-line bg-white">
          <TweetCard tweet={tweet} connectBelow />
          {ancestors.map((ancestor) => (
            <CommentCard key={ancestor.id} comment={ancestor} connectAbove connectBelow />
          ))}
          <CommentCard comment={comment} detail connectAbove onDeleted={leave} />
        </div>

        <ReplyComposer commentId={comment.id} />

        <div className="flex items-center justify-between pt-2">
          <h2 className="text-lg font-bold tracking-tight">Replies</h2>
          <SortMenu value={sort} onChange={setSort} />
        </div>
        <ReplyList commentId={comment.id} sort={sort} />
      </div>
    </>
  )
}