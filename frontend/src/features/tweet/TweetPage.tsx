import { useQuery } from '@tanstack/react-query'
import { isAxiosError } from 'axios'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getErrorMessage } from '../../api/errors'
import { Button } from '../../components/Button'
import { FeedList } from '../feed/FeedList'
import { TweetCard } from './TweetCard'
import { fetchTweet } from './tweetApi'
import { useQuotes } from './useQuotes'

/* This is individual tweet page, which shows a single tweet and its quotes */

function BackLink() {
  return (
    <Link to="/" className="mb-4 inline-block text-sm font-semibold text-brand hover:underline">
      ← Home
    </Link>
  )
}

function Quotes({ tweetId, enabled }: { tweetId: number; enabled: boolean }) {
  const quotes = useQuotes(tweetId, enabled)

  return (
    <section className="mt-8">
      <h2 className="mb-3 text-lg font-bold tracking-tight">Quotes</h2>
      {quotes.status === 'pending' ? (
        <p className="text-muted">Loading quotes…</p>
      ) : quotes.status === 'error' ? (
        <div className="space-y-3">
          <p className="text-danger">{getErrorMessage(quotes.error, "Couldn't load quotes.")}</p>
          <Button variant="quiet" onClick={() => void quotes.refetch()}>
            Try again
          </Button>
        </div>
      ) : quotes.tweets.length === 0 ? (
        <p className="text-muted">No quotes yet.</p>
      ) : (
        <div className="space-y-4">
          <FeedList items={quotes.tweets.map((tweet) => ({ key: `tweet-${tweet.id}`, tweet }))} />
          {quotes.hasNextPage && (
            <Button
              variant="quiet"
              loading={quotes.isFetchingNextPage}
              onClick={() => void quotes.fetchNextPage()}
            >
              Show more
            </Button>
          )}
        </div>
      )}
    </section>
  )
}

export function TweetPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const tweetId = Number(id)
  const validId = Number.isInteger(tweetId) && tweetId > 0

  const tweetQuery = useQuery({
    queryKey: ['tweet', tweetId],
    queryFn: () => fetchTweet(tweetId),
    enabled: validId,
    retry: false,
  })

  const notFound =
    !validId || (tweetQuery.isError && isAxiosError(tweetQuery.error) && tweetQuery.error.response?.status === 404)

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
        <>
          <div className="overflow-hidden rounded-xl border border-line bg-white">
            <TweetCard tweet={tweetQuery.data} onDeleted={() => navigate('/')} />
          </div>
          <Quotes tweetId={tweetId} enabled={validId} />
        </>
      ) : (
        <p className="text-muted">Loading…</p>
      )}
    </>
  )
}