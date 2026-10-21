import type { InfiniteData, QueryClient } from '@tanstack/react-query'
import type { CommentResponse } from '../../api/types'
import type { CommentPage } from './commentApi'
import { patchCommentInPosts } from '../posts/postsCache'

/** This class manages comment caches, including patching a comment in all caches and canceling comment queries. */

/** Changes one comment everywhere it is cached: lists, its own page, and thread chains. */
export function patchCommentInCaches(
  queryClient: QueryClient,
  commentId: number,
  patch: (comment: CommentResponse) => CommentResponse,
): void {
  queryClient.setQueryData<CommentResponse>(['comment', commentId], (old) => (old ? patch(old) : old))
  patchCommentInPosts(queryClient, commentId, patch)
  queryClient.setQueriesData<CommentResponse[]>({ queryKey: ['ancestors'] }, (old) =>
    old?.map((comment) => (comment.id === commentId ? patch(comment) : comment)),
  )

  for (const queryKey of [['comments'], ['replies']]) {
    queryClient.setQueriesData<InfiniteData<CommentPage>>(
      { queryKey },
      (old) =>
        old && {
          ...old,
          pages: old.pages.map((page) => ({
            ...page,
            comments: page.comments.map((comment) =>
              comment.id === commentId ? patch(comment) : comment,
            ),
          })),
        },
    )
  }
}
// Stops in-flight reloads from overwriting an optimistic change.
export function cancelCommentQueries(queryClient: QueryClient) {
  return Promise.all([
    queryClient.cancelQueries({ queryKey: ['comments'] }),
    queryClient.cancelQueries({ queryKey: ['replies'] }),
    queryClient.cancelQueries({ queryKey: ['comment'] }),
    queryClient.cancelQueries({ queryKey: ['ancestors'] }),
    queryClient.cancelQueries({ queryKey: ['posts'] }),
  ])
}