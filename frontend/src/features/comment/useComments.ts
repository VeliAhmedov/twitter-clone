import { useInfiniteQuery } from '@tanstack/react-query'
import { useMemo } from 'react'
import { fetchCommentsPage, fetchRepliesPage, type CommentPage } from './commentApi'
import type { CommentSort } from '../../api/types'

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

// The useComments and useReplies hooks utilize the useCommentPages helper to fetch comments 
// for a specific tweet or replies for a specific comment, respectively. 
// They provide an easy way to manage infinite scrolling and data fetching in the UI.
export function useComments(tweetId: number, enabled: boolean, sort: CommentSort = 'newest') {
  return useCommentPages(
    ['comments', tweetId, sort],
    (page) => fetchCommentsPage(tweetId, page, sort),
    enabled,
  )
}

export function useReplies(commentId: number, enabled: boolean, sort: CommentSort = 'newest') {
  return useCommentPages(
    ['replies', commentId, sort],
    (page) => fetchRepliesPage(commentId, page, sort),
    enabled,
  )
}