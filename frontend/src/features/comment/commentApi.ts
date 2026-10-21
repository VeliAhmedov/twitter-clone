import { api } from '../../api/client'
import { hasNextPage } from '../../api/pagination'
import type { CommentRequest, CommentResponse, CommentSort, Page } from '../../api/types'

/*Like tweetApi, this file contains API calls for comments, including fetching pages of comments and 
replies, creating, updating, deleting, liking, and unliking comments. It also defines the CommentPage 
interface and a helper function to convert the API response to that format. 
*/

export const COMMENTS_PAGE_SIZE = 20

export interface CommentPage {
  comments: CommentResponse[]
  hasMore: boolean
}

function toCommentPage(data: Page<CommentResponse>, page: number): CommentPage {
  return { comments: data.content, hasMore: hasNextPage(data, page, COMMENTS_PAGE_SIZE) }
}

// Fetches a single comment by ID.
export async function fetchComment(id: number): Promise<CommentResponse> {
  const { data } = await api.get<CommentResponse>(`/api/comments/${id}`)
  return data
}

// Top-level comments on a tweet (replies are loaded separately). also supports sorting by newest or relevant.
export async function fetchCommentsPage(
  tweetId: number,
  page: number,
  sort: CommentSort = 'newest',
): Promise<CommentPage> {
  const { data } = await api.get<Page<CommentResponse>>(`/api/tweets/${tweetId}/comments`, {
    params: { page, size: COMMENTS_PAGE_SIZE, order: sort },
  })
  return toCommentPage(data, page)
}

//like fetchCommentsPage, but for replies to a specific comment. also supports sorting by newest or relevant.
export async function fetchRepliesPage(
  commentId: number,
  page: number,
  sort: CommentSort = 'newest',
): Promise<CommentPage> {
  const { data } = await api.get<Page<CommentResponse>>(`/api/comments/${commentId}/replies`, {
    params: { page, size: COMMENTS_PAGE_SIZE, order: sort },
  })
  return toCommentPage(data, page)
}

export async function createComment(tweetId: number, body: CommentRequest): Promise<CommentResponse> {
  const { data } = await api.post<CommentResponse>(`/api/tweets/${tweetId}/comments`, body) // 201
  return data
}

export async function createReply(commentId: number, body: CommentRequest): Promise<CommentResponse> {
  const { data } = await api.post<CommentResponse>(`/api/comments/${commentId}/replies`, body) // 201
  return data
}

export async function updateComment(id: number, body: CommentRequest): Promise<CommentResponse> {
  const { data } = await api.patch<CommentResponse>(`/api/comments/${id}`, body)
  return data
}

export async function deleteComment(id: number): Promise<void> {
  await api.delete(`/api/comments/${id}`) // 204
}

export async function likeComment(id: number): Promise<number> {
  const { data } = await api.post<number>(`/api/comments/${id}/likes`)
  return data
}

export async function unlikeComment(id: number): Promise<number> {
  const { data } = await api.delete<number>(`/api/comments/${id}/likes`)
  return data
}
// Repost and bookmark a comment. DELETE repost answers 204 (no body), so the count may be missing.
export async function repostComment(id: number): Promise<number> {
  const { data } = await api.post<number>(`/api/comments/${id}/retweet`) // 201
  return data
}

export async function unrepostComment(id: number): Promise<number | undefined> {
  const { data } = await api.delete<unknown>(`/api/comments/${id}/retweet`)
  return typeof data === 'number' ? data : undefined
}

export async function bookmarkComment(id: number): Promise<undefined> {
  await api.post(`/api/bookmarks/comment/${id}`) // 201
}

export async function unbookmarkComment(id: number): Promise<undefined> {
  await api.delete(`/api/bookmarks/comment/${id}`) // 204
}