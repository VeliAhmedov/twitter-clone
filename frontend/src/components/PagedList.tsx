import { useCallback, type ReactNode } from 'react'
import { getErrorMessage } from '../api/errors'
import { useLoadMoreTrigger } from '../lib/useLoadMoreTrigger'
import { Button } from './Button'

/*
 *This component wraps a paged list with loading, error, empty state and infinite scroll functionality.
 *It takes a `query` object that contains the status of the paged query, error information, and methods for fetching more data.
 *It also takes `isEmpty`, `emptyText`, and `errorText` props to customize the display for empty and error states.
*/

interface PagedQuery {
  status: 'pending' | 'error' | 'success'
  error: unknown
  refetch: () => unknown
  hasNextPage: boolean
  isFetchingNextPage: boolean
  isFetchNextPageError: boolean
  fetchNextPage: () => unknown
}

interface PagedListProps {
  query: PagedQuery
  isEmpty: boolean
  emptyText: string
  errorText: string
  /** The rendered items. Shown only when there is something to show. */
  children: ReactNode
}

/** Loading, error, empty state and infinite scroll around any paged list. */
export function PagedList({ query, isEmpty, emptyText, errorText, children }: PagedListProps) {
  const { fetchNextPage, hasNextPage, isFetchingNextPage, isFetchNextPageError } = query

  const loadMore = useCallback(() => {
    void fetchNextPage()
  }, [fetchNextPage])

  // Stops auto-loading after a failed page, so a dead backend is not hammered in a loop.
  const sentinelRef = useLoadMoreTrigger(loadMore, hasNextPage && !isFetchingNextPage && !isFetchNextPageError)

  if (query.status === 'pending') {
    return <p className="rounded-xl border border-line bg-white p-6 text-muted">Loading…</p>
  }

  if (query.status === 'error') {
    return (
      <div role="alert" className="space-y-3 rounded-xl border border-line bg-white p-6">
        <p className="text-danger">{getErrorMessage(query.error, errorText)}</p>
        <Button variant="quiet" onClick={() => void query.refetch()}>
          Try again
        </Button>
      </div>
    )
  }

  if (isEmpty) {
    return <p className="rounded-xl border border-line bg-white p-6 text-center text-muted">{emptyText}</p>
  }

  return (
    <>
      {children}
      <div ref={sentinelRef} className="py-6 text-center text-sm text-muted">
        {isFetchNextPageError ? (
          <Button variant="quiet" onClick={loadMore}>
            Couldn't load more. Try again
          </Button>
        ) : isFetchingNextPage ? (
          'Loading more…'
        ) : hasNextPage ? null : (
          "That's everything."
        )}
      </div>
    </>
  )
}