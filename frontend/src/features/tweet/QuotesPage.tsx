import { Link, useParams } from 'react-router-dom'
import { QuotesList } from './QuotesList'

/* This component displays a page showing all quotes for a specific tweet.
It uses the QuotesList component to fetch and display the quotes, and provides a link to go back to the original tweet. 
The tweet ID is extracted from the URL parameters, and the component handles cases where the ID is invalid. 
*/

export function QuotesPage() {
  const { id } = useParams()
  const tweetId = Number(id)
  const validId = Number.isInteger(tweetId) && tweetId > 0

  return (
    <>
      <Link
        to={validId ? `/tweet/${tweetId}` : '/'}
        className="mb-4 inline-block text-sm font-semibold text-brand hover:underline"
      >
        ← Back to post
      </Link>
      <h1 className="mb-4 text-2xl font-bold tracking-tight">Quotes</h1>
      {validId ? (
        <QuotesList tweetId={tweetId} />
      ) : (
        <p className="text-muted">This post doesn't exist.</p>
      )}
    </>
  )
}