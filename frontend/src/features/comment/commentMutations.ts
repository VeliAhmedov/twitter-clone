import { useMutation, useQueryClient, type QueryClient } from '@tanstack/react-query'
import type { CommentRequest, CommentResponse } from '../../api/types'
import {
  createComment,
  createReply,
  deleteComment,
  likeComment,
  unlikeComment,
  updateComment,
} from './commentApi'
import { cancelCommentQueries, patchCommentInCaches } from './commentCache'

//Like tweetMutations, this file contains React Query mutations for comments, including creating, 
//updating, deleting, and liking/unliking comments. It also includes a helper function to refresh 
//comment lists after mutations.

// Reply counts show on the tweet and its feed card, so those reload too.
function refreshCommentLists(queryClient: QueryClient) {
  return Promise.all(
    [['comments'], ['replies'], ['comment'], ['ancestors'], ['tweet'], ['feed']].map((queryKey) =>
      queryClient.invalidateQueries({ queryKey }),
    ),
  )
}

export function useCreateComment(tweetId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (body: CommentRequest) => createComment(tweetId, body),
    onSuccess: () => refreshCommentLists(queryClient),
  })
}

export function useCreateReply(parentCommentId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (body: CommentRequest) => createReply(parentCommentId, body),
    onSuccess: () => refreshCommentLists(queryClient),
  })
}

export function useUpdateComment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, body }: { id: number; body: CommentRequest }) => updateComment(id, body),
    onSuccess: () => refreshCommentLists(queryClient),
  })
}

export function useDeleteComment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteComment(id),
    onSuccess: () => refreshCommentLists(queryClient),
  })
}

// Optimistic like, same idea as for tweets.
export function useLikeComment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ comment, enable }: { comment: CommentResponse; enable: boolean }) =>
      enable ? likeComment(comment.id) : unlikeComment(comment.id),

    onMutate: async ({ comment, enable }) => {
      await cancelCommentQueries(queryClient)
      patchCommentInCaches(queryClient, comment.id, (current) => ({
        ...current,
        likedByMe: enable,
        likeCount: Math.max(0, current.likeCount + (enable ? 1 : -1)),
      }))
      return { likedByMe: comment.likedByMe, likeCount: comment.likeCount }
    },

    onError: (_error, { comment }, previous) => {
      if (previous) patchCommentInCaches(queryClient, comment.id, (current) => ({ ...current, ...previous }))
    },

    onSuccess: (serverCount, { comment }) => {
      patchCommentInCaches(queryClient, comment.id, (current) => ({ ...current, likeCount: serverCount }))
    },
  })
}