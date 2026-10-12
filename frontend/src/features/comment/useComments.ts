import { useInfiniteQuery } from '@tanstack/react-query'
import { useMemo } from 'react'
import { fetchCommentsPage, fetchRepliesPage, type CommentPage } from './commentApi'

/* This file contains React Query hooks for fetching comments and replies, featuring an 
infinite scrolling helper. It includes the useComments and useReplies hooks to retrieve data 
for specific tweets or comments. 
*/

function useCommentPages(
  queryKey: readonly unknown[],
  fetchPage: (page: number) => Promise<CommentPage>,
  enabled: boolean,
) {
  const query = useInfiniteQuery({
    queryKey,
    queryFn: ({ pageParam }) => fetchPage(pageParam),
    initialPageParam: 0,
    getNextPageParam: (lastPage, _pages, lastPageParam) =>
      lastPage.hasMore ? lastPageParam + 1 : undefined,
    enabled,
  })

  const comments = useMemo(
    () => query.data?.pages.flatMap((page) => page.comments) ?? [],
    [query.data],
  )

  return { ...query, comments }
}

export type CommentPagesResult = ReturnType<typeof useCommentPages>

export function useComments(tweetId: number, enabled: boolean) {
  return useCommentPages(['comments', tweetId], (page) => fetchCommentsPage(tweetId, page), enabled)
}

export function useReplies(commentId: number, enabled: boolean) {
  return useCommentPages(['replies', commentId], (page) => fetchRepliesPage(commentId, page), enabled)
}