import { useInfiniteQuery } from '@tanstack/react-query'
import { useMemo } from 'react'
import { fetchFeedPage, type FeedItem } from './feedApi'

/** A custom React hook that is a reusable function that manages React-related behavior and data without being a UI component itself. */

export function useFeed() {
    /**
     Instead of manually writing all the logic for fetching, 
     loading, retrying and remembering data, React Query manages
     */
  const query = useInfiniteQuery({
    queryKey: ['feed', 'global'],
    queryFn: ({ pageParam }) => fetchFeedPage(pageParam),
    initialPageParam: 0,
    getNextPageParam: (lastPage, _pages, lastPageParam) =>
      lastPage.hasMore ? lastPageParam + 1 : undefined,
  })

  // Pages are offset-based, so a tweet posted while scrolling can push an item onto the
  // next page and show it twice. Drop repeats.
  const items = useMemo(() => {
    const seen = new Set<string>()
    const result: FeedItem[] = []
    for (const page of query.data?.pages ?? []) {
      for (const item of page.items) {
        if (!seen.has(item.key)) {
          seen.add(item.key)
          result.push(item)
        }
      }
    }
    return result
  }, [query.data])

  return { ...query, items }
}