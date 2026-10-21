import { useInfiniteQuery, useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { hasNextPage } from '../../api/pagination'
import {
  getNotifications,
  getUnreadNotificationCount,
  markAllNotificationsAsRead,
  markNotificationAsRead,
  NOTIFICATION_PAGE_SIZE,
} from './notificationApi'

/*
 * This file contains React Query hooks for notifications, including fetching notifications, getting the unread count,
 * marking a notification as read, and marking all notifications as read. It also defines the notificationKeys object
 * for query keys used in the hooks.
 */

export const notificationKeys = {
  all: ['notifications'] as const,
  unreadCount: ['notification-unread-count'] as const,
}

export function useNotifications() {
  return useInfiniteQuery({
    queryKey: notificationKeys.all,
    queryFn: ({ pageParam }) => getNotifications(pageParam),
    initialPageParam: 0,
    getNextPageParam: (lastPage, allPages) =>
      hasNextPage(lastPage, allPages.length - 1, NOTIFICATION_PAGE_SIZE)
        ? allPages.length
        : undefined,
  })
}

export function useUnreadNotificationCount() {
  return useQuery({
    queryKey: notificationKeys.unreadCount,
    queryFn: getUnreadNotificationCount,
    refetchInterval: 30_000,
  })
}

export function useMarkNotificationAsRead() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: markNotificationAsRead,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: notificationKeys.all })
      void queryClient.invalidateQueries({ queryKey: notificationKeys.unreadCount })
    },
  })
}

export function useMarkAllNotificationsAsRead() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: markAllNotificationsAsRead,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: notificationKeys.all })
      void queryClient.invalidateQueries({ queryKey: notificationKeys.unreadCount })
    },
  })
}