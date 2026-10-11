import { getErrorMessage } from '../../api/errors'
import { Button } from '../../components/Button'
import { FeedList } from '../feed/FeedList'
import { useQuotes } from './useQuotes'

/* This component displays a list of quotes for a specific tweet.
It uses the useQuotes hook to fetch the quotes from the backend, and handles loading, error, and empty states. 
The quotes are displayed using the FeedList component, and a "Show more" button is provided to load additional 
pages of quotes if available. 
*/

//similiar to view quotes on twitter, this component displays a list of quotes for a specific tweet.

export function QuotesList({ tweetId }: { tweetId: number }) {
  const quotes = useQuotes(tweetId, true)

  if (quotes.status === 'pending') return <p className="text-muted">Loading quotes…</p>

  if (quotes.status === 'error') {
    return (
      <div className="space-y-3">
        <p className="text-danger">{getErrorMessage(quotes.error, "Couldn't load quotes.")}</p>
        <Button variant="quiet" onClick={() => void quotes.refetch()}>
          Try again
        </Button>
      </div>
    )
  }

  if (quotes.tweets.length === 0) return <p className="text-muted">No quotes yet.</p>

  return (
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
  )
}