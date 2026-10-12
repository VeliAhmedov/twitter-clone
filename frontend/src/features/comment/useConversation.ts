import { useQuery } from '@tanstack/react-query'
import type { CommentResponse } from '../../api/types'
import { fetchComment } from './commentApi'

/* 
*This file contains React Query hooks for fetching a single comment and its ancestors. 
*It includes the useComment and useAncestors hooks to retrieve data for specific comments.
 */

const MAX_ANCESTORS = 25 // safety limit, in case the data is ever circular

// Walks up from a comment's parent to the top-level comment, one request per step.
async function fetchAncestors(parentId: number | null): Promise<CommentResponse[]> {
  const chain: CommentResponse[] = []
  let nextId = parentId
  while (nextId !== null && chain.length < MAX_ANCESTORS) {
    const parent = await fetchComment(nextId)
    chain.unshift(parent) // so the top-most comment ends up first
    nextId = parent.parentCommentId
  }
  return chain
}

export function useComment(commentId: number, enabled: boolean) {
  return useQuery({
    queryKey: ['comment', commentId],
    queryFn: () => fetchComment(commentId),
    enabled,
    retry: false,
  })
}

/** The comments above this one, top-most first. The tweet itself is not included. */
export function useAncestors(comment: CommentResponse | undefined) {
  return useQuery({
    queryKey: ['ancestors', comment?.id],
    queryFn: () => fetchAncestors(comment?.parentCommentId ?? null),
    enabled: comment !== undefined,
  })
}