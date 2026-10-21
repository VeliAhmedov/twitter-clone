import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import { isAxiosError } from 'axios'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getErrorMessage } from '../../api/errors'
import { Button } from '../../components/Button'
import { CommentComposer } from '../comment/CommentComposer'
import { CommentList } from '../comment/CommentList'
import { TweetCard } from './TweetCard'
import { fetchTweet } from './tweetApi'
import type { CommentSort } from '../../api/types'
import { SortMenu } from '../../components/SortMenu'

/* This is individual tweet page, which shows a single tweet and its quotes */

function BackLink() {
  return (
    <Link to="/" className="mb-4 inline-block text-sm font-semibold text-brand hover:underline">
      ← Home
    </Link>
  )
}

export function TweetPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const tweetId = Number(id)
  const [sort, setSort] = useState<CommentSort>('newest')
  const validId = Number.isInteger(tweetId) && tweetId > 0

  const tweetQuery = useQuery({
    queryKey: ['tweet', tweetId],
    queryFn: () => fetchTweet(tweetId),
    enabled: validId,
    retry: false,
  })

  const notFound =
    !validId ||
    (tweetQuery.isError && isAxiosError(tweetQuery.error) && tweetQuery.error.response?.status === 404)

  if (notFound) {
    return (
      <>
        <BackLink />
        <p className="rounded-xl border border-line bg-white p-6 text-muted">
          This post doesn't exist. It may have been deleted.
        </p>
      </>
    )
  }

  return (
    <>
      <BackLink />
      {tweetQuery.isError ? (
        <div role="alert" className="space-y-3 rounded-xl border border-line bg-white p-6">
          <p className="text-danger">{getErrorMessage(tweetQuery.error, "Couldn't load this post.")}</p>
          <Button variant="quiet" onClick={() => void tweetQuery.refetch()}>
            Try again
          </Button>
        </div>
      ) : tweetQuery.data ? (
        <div className="space-y-4">
          <div className="rounded-xl border border-line bg-white">
            <TweetCard tweet={tweetQuery.data} detail onDeleted={() => navigate('/')} />
          </div>

           <CommentComposer tweetId={tweetId} />

          <div className="flex items-center justify-between pt-2">
            <h2 className="text-lg font-bold tracking-tight">Replies</h2>
            <div className="flex items-center gap-4">
              <SortMenu value={sort} onChange={setSort} />
              <Link
                to={`/tweet/${tweetId}/quotes`}
                className="text-sm font-semibold text-muted hover:text-ink"
              >
                View quotes ›
              </Link>
            </div>
          </div>

          <CommentList tweetId={tweetId} sort={sort} />
        </div>
      ) : (
        <p className="text-muted">Loading…</p>
      )}
    </>
  )
}