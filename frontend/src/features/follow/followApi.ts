import { api } from '../../api/client'
import { hasNextPage } from '../../api/pagination'
import type { FollowStats, FollowUser, Page, UserResponse } from '../../api/types'

// This module provides functions and types for fetching and managing follow-related data, 
// such as follow statistics, follow status, and lists of followers or following users. 
// It defines the structure of follow statistics and user information, as well as the response format for paginated follow lists. 
// The functions in this module interact with the backend API to retrieve and update follow-related data.

export const FOLLOW_PAGE_SIZE = 20

export type FollowListKind = 'followers' | 'following'

export async function fetchFollowStats(userId: number): Promise<FollowStats> {
  const { data } = await api.get<FollowStats>(`/api/users/${userId}/follow-stats`)
  return data
}

/** Does the logged-in user follow this user? (needs GET /api/users/{id}/follow-status) */
export async function fetchFollowStatus(userId: number): Promise<boolean> {
  const { data } = await api.get<boolean>(`/api/users/${userId}/follow-status`)
  return data
}

// Both calls answer with the followed user's new follower count.
export async function followUser(userId: number): Promise<number | undefined> {
  const { data } = await api.post<unknown>(`/api/users/${userId}/follow`)
  return typeof data === 'number' ? data : undefined
}

export async function unfollowUser(userId: number): Promise<number | undefined> {
  const { data } = await api.delete<unknown>(`/api/users/${userId}/follow`)
  return typeof data === 'number' ? data : undefined
}

// Fetches a page of users who follow or are followed by the specified user, based on the kind of list requested (followers or following).
export async function fetchFollowUsersPage(
  userId: number,
  kind: FollowListKind,
  page: number,
): Promise<{ users: FollowUser[]; hasMore: boolean }> {
  const { data } = await api.get<Page<FollowUser>>(`/api/users/${userId}/${kind}`, {
    params: { page, size: FOLLOW_PAGE_SIZE },
  })
  return { users: data.content, hasMore: hasNextPage(data, page, FOLLOW_PAGE_SIZE) }
}

/** A few users for the "Who to follow" card (GET /api/users is public and paged). */
export async function fetchUserSuggestions(): Promise<UserResponse[]> {
  const { data } = await api.get<Page<UserResponse>>('/api/users', { params: { page: 0, size: 8 } })
  return data.content
}