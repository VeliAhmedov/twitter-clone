import { useInfiniteQuery } from '@tanstack/react-query'
import { useMemo } from 'react'
import { fetchQuotesPage } from './tweetApi'

/*loads quotes page by page, using React Query's useInfiniteQuery hook */

//This custom hook fetches the tweets that quote a particular tweet.
export function useQuotes(tweetId: number, enabled: boolean) {
  const query = useInfiniteQuery({
    queryKey: ['quotes', tweetId],
    queryFn: ({ pageParam }) => fetchQuotesPage(tweetId, pageParam),
    initialPageParam: 0,
    getNextPageParam: (lastPage, _pages, lastPageParam) =>
      lastPage.hasMore ? lastPageParam + 1 : undefined,
    enabled,
  })

  const tweets = useMemo(() => query.data?.pages.flatMap((page) => page.tweets) ?? [], [query.data])

  return { ...query, tweets }
}