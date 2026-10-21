import { useMutation, useQueryClient, type QueryClient } from '@tanstack/react-query'
import type { CommentRequest, CommentResponse } from '../../api/types'
import {
  bookmarkComment,
  createComment,
  createReply,
  deleteComment,
  likeComment,
  repostComment,
  unbookmarkComment,
  unlikeComment,
  unrepostComment,
  updateComment,
} from './commentApi'
import { cancelCommentQueries, patchCommentInCaches } from './commentCache'

/* 
 *This file contains React Query mutations for creating, updating, deleting, and toggling comments.
 * It includes hooks for creating comments and replies, updating and deleting comments, and toggling likes, reposts, and bookmarks.
 * The mutations are designed to optimistically update the UI and handle errors gracefully.
 */

// Reply counts show on the tweet and its feed card, so those reload too.
function refreshCommentLists(queryClient: QueryClient) {
  return Promise.all(
    [['comments'], ['replies'], ['comment'], ['ancestors'], ['tweet'], ['feed'], ['posts']].map((queryKey) =>
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

type Flag = 'likedByMe' | 'retweetedByMe' | 'bookmarkedByMe'
type CountField = 'likeCount' | 'retweetCount'

interface ToggleConfig {
  flag: Flag
  count?: CountField
  on: (id: number) => Promise<number | undefined>
  off: (id: number) => Promise<number | undefined>
}

function setField(comment: CommentResponse, field: Flag | CountField, value: boolean | number): CommentResponse {
  return Object.assign({}, comment, { [field]: value })
}

// Optimistic toggle, same idea as for tweets: change the button at once, undo on failure.
function useToggleComment(config: ToggleConfig) {
  const queryClient = useQueryClient()
  const { flag, count } = config

  return useMutation({
    mutationFn: ({ comment, enable }: { comment: CommentResponse; enable: boolean }) =>
      enable ? config.on(comment.id) : config.off(comment.id),

    onMutate: async ({ comment, enable }) => {
      await cancelCommentQueries(queryClient)
      patchCommentInCaches(queryClient, comment.id, (current) => {
        const next = setField(current, flag, enable)
        return count
          ? setField(next, count, Math.max(0, current[count] + (enable ? 1 : -1)))
          : next
      })
      return { flag: comment[flag], count: count ? comment[count] : undefined }
    },

    onError: (_error, { comment }, previous) => {
      if (!previous) return
      patchCommentInCaches(queryClient, comment.id, (current) => {
        const restored = setField(current, flag, previous.flag)
        return count && previous.count !== undefined
          ? setField(restored, count, previous.count)
          : restored
      })
    },

    // Prefer the server's own total over our +1/-1 guess when it sends one.
    onSuccess: (serverCount, { comment }) => {
      if (count && typeof serverCount === 'number') {
        patchCommentInCaches(queryClient, comment.id, (current) => setField(current, count, serverCount))
      }
    },
  })
}

const likeConfig: ToggleConfig = {
  flag: 'likedByMe',
  count: 'likeCount',
  on: likeComment,
  off: unlikeComment,
}
const repostConfig: ToggleConfig = {
  flag: 'retweetedByMe',
  count: 'retweetCount',
  on: repostComment,
  off: unrepostComment,
}
const bookmarkConfig: ToggleConfig = {
  flag: 'bookmarkedByMe',
  on: bookmarkComment,
  off: unbookmarkComment,
}

export const useLikeComment = () => useToggleComment(likeConfig)
export const useRepostComment = () => useToggleComment(repostConfig)
export const useBookmarkComment = () => useToggleComment(bookmarkConfig)