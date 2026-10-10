import { useCallback, useEffect, useRef } from 'react'
import { getErrorMessage } from '../../api/errors'
import { Button } from '../../components/Button'
import { FeedList } from './FeedList'
import { useFeed } from './useFeed'

//Handle full feed experence, including loading, errors, and paging. Uses FeedList to render the actual tweets.

// Calls onLoadMore when the returned element scrolls near the viewport.
// Re-subscribing whenever `enabled` flips makes it fire again if the element is still visible
// after a page loads (a short page on a tall screen).
function useLoadMoreTrigger(onLoadMore: () => void, enabled: boolean) {
  const ref = useRef<HTMLDivElement>(null)

  useEffect(() => {
    const node = ref.current
    if (!node || !enabled) return

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries.some((entry) => entry.isIntersecting)) onLoadMore()
      },
      { rootMargin: '600px' },
    )
    observer.observe(node)
    return () => observer.disconnect()
  }, [onLoadMore, enabled])

  return ref
}

function FeedSkeleton() {
  return (
    <div
      role="status"
      aria-label="Loading feed"
      className="divide-y divide-line overflow-hidden rounded-xl border border-line bg-white"
    >
      {[0, 1, 2].map((index) => (
        <div key={index} className="flex animate-pulse gap-3 px-4 py-4 motion-reduce:animate-none">
          <div className="size-11 shrink-0 rounded-full bg-line" />
          <div className="flex-1 space-y-2 pt-1">
            <div className="h-3 w-1/3 rounded bg-line" />
            <div className="h-3 w-full rounded bg-line" />
            <div className="h-3 w-2/3 rounded bg-line" />
          </div>
        </div>
      ))}
    </div>
  )
}

export function Feed() {
  const {
    items,
    status,
    error,
    refetch,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isFetchNextPageError,
  } = useFeed()

  const loadMore = useCallback(() => {
    void fetchNextPage()
  }, [fetchNextPage])

  // Hook code responsible for automatic loading while scrolling.
  // Stops auto-loading after a failed page, so a dead backend is not hammered in a loop.
  const sentinelRef = useLoadMoreTrigger(
    loadMore,
    hasNextPage && !isFetchingNextPage && !isFetchNextPageError,
  )

  if (status === 'pending') return <FeedSkeleton />

  if (status === 'error') {
    return (
      <div role="alert" className="space-y-3 rounded-xl border border-line bg-white p-6">
        <p className="text-danger">{getErrorMessage(error, "Couldn't load the feed.")}</p>
        <Button variant="quiet" onClick={() => void refetch()}>
          Try again
        </Button>
      </div>
    )
  }

  if (items.length === 0) {
    return (
      <p className="rounded-xl border border-line bg-white p-6 text-center text-muted">
        No posts yet.
      </p>
    )
  }

  return (
    <>
      <FeedList items={items} />
      <div ref={sentinelRef} className="py-6 text-center text-sm text-muted">
        {isFetchNextPageError ? (
          <Button variant="quiet" onClick={loadMore}>
            Couldn't load more. Try again
          </Button>
        ) : isFetchingNextPage ? (
          'Loading more…'
        ) : hasNextPage ? null : (
          "You're all caught up."
        )}
      </div>
    </>
  )
}
/**
 * Fedd handles below situations:
 * 
 * Initial loading	                FeedSkeleton, a placeholder resembling tweet cards
 * Initial request fails	        An error message and “Try again” button
 * No tweets returned	            “No posts yet.”
 * More tweets are loading	        “Loading more…”
 * Loading the next page fails     	A button to retry loading more
 * All pages are loaded	            “You're all caught up.”
 * 
 * conditional rendering: the component chooses which part of the UI to display based on the current state.
 */