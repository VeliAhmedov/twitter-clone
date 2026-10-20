import { useInfiniteQuery, useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useMemo } from 'react'
import type { FollowStats } from '../../api/types'
import { useAuthStore } from '../auth/authStore'
import {
  fetchFollowStats,
  fetchFollowStatus,
  fetchFollowUsersPage,
  followUser,
  unfollowUser,
  type FollowListKind,
} from './followApi'

/*
    * This module provides React hooks for managing follow-related data in a frontend application.
    * It includes hooks for fetching follow statistics, follow status, and lists of followers or following users.
    * The `useToggleFollow` hook implements optimistic updates for following and unfollowing users, allowing the UI to reflect changes immediately while the server request is processed.
    * The hooks are designed to work with the backend API functions defined in the `followApi` module, which handle the actual data retrieval and updates.
*/

export function useFollowStats(userId: number | undefined) {
  return useQuery({
    queryKey: ['follow-stats', userId],
    queryFn: () => fetchFollowStats(userId ?? 0),
    enabled: userId !== undefined,
  })
}

export function useFollowStatus(userId: number | undefined, enabled: boolean) {
  return useQuery({
    queryKey: ['follow-status', userId],
    queryFn: () => fetchFollowStatus(userId ?? 0),
    enabled: enabled && userId !== undefined,
    retry: false,
  })
}

// Optimistic follow/unfollow: the button and the follower count change at once and are
// put back if the request fails.
export function useToggleFollow() {
  const queryClient = useQueryClient()
  const currentUserId = useAuthStore((state) => state.user?.id)

  return useMutation({
    mutationFn: ({ userId, enable }: { userId: number; enable: boolean }) =>
      enable ? followUser(userId) : unfollowUser(userId),

    onMutate: async ({ userId, enable }) => {
      await Promise.all([
        queryClient.cancelQueries({ queryKey: ['follow-status', userId] }),
        queryClient.cancelQueries({ queryKey: ['follow-stats', userId] }),
      ])
      const status = queryClient.getQueryData<boolean>(['follow-status', userId])
      const stats = queryClient.getQueryData<FollowStats>(['follow-stats', userId])

      queryClient.setQueryData<boolean>(['follow-status', userId], enable)
      queryClient.setQueryData<FollowStats>(
        ['follow-stats', userId],
        (old) => old && { ...old, followerCount: Math.max(0, old.followerCount + (enable ? 1 : -1)) },
      )
      return { status, stats }
    },

    onError: (_error, { userId }, previous) => {
      if (!previous) return
      queryClient.setQueryData(['follow-status', userId], previous.status)
      queryClient.setQueryData(['follow-stats', userId], previous.stats)
    },

    // Prefer the server's own follower total over our +1/-1 guess when it sends one.
    onSuccess: (serverCount, { userId }) => {
      if (typeof serverCount === 'number') {
        queryClient.setQueryData<FollowStats>(
          ['follow-stats', userId],
          (old) => old && { ...old, followerCount: serverCount },
        )
      }
    },

    onSettled: (_data, _error, { userId }) => {
      void queryClient.invalidateQueries({ queryKey: ['follow-list'] })
      // The logged-in user's own "following" number changed too.
      if (currentUserId !== undefined && currentUserId !== userId) {
        void queryClient.invalidateQueries({ queryKey: ['follow-stats', currentUserId] })
      }
    },
  })
}

export function useFollowList(userId: number | undefined, kind: FollowListKind) {
  const query = useInfiniteQuery({
    queryKey: ['follow-list', userId, kind],
    queryFn: ({ pageParam }) => fetchFollowUsersPage(userId ?? 0, kind, pageParam),
    initialPageParam: 0,
    getNextPageParam: (lastPage, _pages, lastPageParam) =>
      lastPage.hasMore ? lastPageParam + 1 : undefined,
    enabled: userId !== undefined,
  })

  const users = useMemo(() => query.data?.pages.flatMap((page) => page.users) ?? [], [query.data])
  return { ...query, users }
}