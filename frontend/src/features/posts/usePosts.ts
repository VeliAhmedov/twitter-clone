import { useInfiniteQuery } from '@tanstack/react-query'
import { useMemo } from 'react'
import { fetchPostsPage, postsKey, type PostItem, type PostsSource } from './postsApi'

export function usePosts(source: PostsSource) {
  const query = useInfiniteQuery({
    queryKey: postsKey(source),
    queryFn: ({ pageParam }) => fetchPostsPage(source, pageParam),
    initialPageParam: 0,
    getNextPageParam: (lastPage, _pages, lastPageParam) =>
      lastPage.hasMore ? lastPageParam + 1 : undefined,
    // Bookmarks change from other pages, so always look again when the page opens.
    refetchOnMount: source.kind === 'bookmarks' ? 'always' : true,
  })

  // Offset paging can show an item twice when the list changes while scrolling. Drop repeats.
  const items = useMemo(() => {
    const seen = new Set<string>()
    const result: PostItem[] = []
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