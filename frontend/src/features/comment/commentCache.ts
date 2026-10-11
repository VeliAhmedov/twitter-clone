import type { InfiniteData, QueryClient } from '@tanstack/react-query'
import type { CommentResponse } from '../../api/types'
import type { CommentPage } from './commentApi'

//Main purpose to mamage comemnt caches, including patching a comment in all caches and canceling comment queries.

/** Changes one comment in every cached comment or reply list. */
export function patchCommentInCaches(
  queryClient: QueryClient,
  commentId: number,
  patch: (comment: CommentResponse) => CommentResponse,
): void {
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

export function cancelCommentQueries(queryClient: QueryClient) {
  return Promise.all([
    queryClient.cancelQueries({ queryKey: ['comments'] }),
    queryClient.cancelQueries({ queryKey: ['replies'] }),
  ])
}