import { api } from '../../api/client'
import type { Page } from '../../api/types'

/*
 * This file contains API calls for notifications, including fetching notifications, getting the unread count,
 * marking a notification as read, and marking all notifications as read. It also defines the NotificationType
 * and NotificationResponse interfaces, as well as the NOTIFICATION_PAGE_SIZE constant.
 */


export type NotificationType =
  | 'TWEET_LIKE'
  | 'COMMENT_LIKE'
  | 'FOLLOW'
  | 'COMMENT'
  | 'REPLY'
  | 'QUOTE_TWEET'
  | 'QUOTE_COMMENT'
  | 'RETWEET_TWEET'
  | 'RETWEET_COMMENT'

export interface NotificationResponse {
  id: number
  senderId: number
  senderUsername: string
  senderDisplayName: string
  notificationType: NotificationType
  message: string
  tweetId: number | null
  commentId: number | null
  read: boolean
  createdAt: string
}

export const NOTIFICATION_PAGE_SIZE = 20

export async function getNotifications(page = 0): Promise<Page<NotificationResponse>> {
  const { data } = await api.get<Page<NotificationResponse>>('/api/notifications', {
    params: { page, size: NOTIFICATION_PAGE_SIZE },
  })
  return data
}

export async function getUnreadNotificationCount(): Promise<number> {
  const { data } = await api.get<number>('/api/notifications/unread-count')
  return data
}

export async function markNotificationAsRead(id: number): Promise<NotificationResponse> {
  const { data } = await api.patch<NotificationResponse>(`/api/notifications/${id}/read`)
  return data
}

export async function markAllNotificationsAsRead(): Promise<number> {
  const { data } = await api.patch<number>('/api/notifications/read-all')
  return data
}