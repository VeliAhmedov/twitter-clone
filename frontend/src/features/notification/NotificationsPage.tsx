import { Link } from 'react-router-dom'
import { PagedList } from '../../components/PagedList'
import { Button } from '../../components/Button'
import { useMarkAllNotificationsAsRead, useMarkNotificationAsRead, useNotifications, useUnreadNotificationCount} from './useNotifications'
import type { NotificationResponse } from './notificationApi'

/*
 * This file contains the NotificationsPage component, which displays a list of notifications for the user.
 * It allows users to mark individual notifications as read or mark all notifications as read.
 * The component uses React Query hooks to fetch and manage notification data.
 */

function getNotificationTarget(notification: NotificationResponse): string {
  const { notificationType, senderUsername, tweetId, commentId } = notification

  if (notificationType === 'FOLLOW') {
    return `/@${senderUsername}`
  }

  if (
    commentId != null &&
    ['COMMENT_LIKE', 'REPLY', 'QUOTE_COMMENT', 'RETWEET_COMMENT'].includes(notificationType)
  ) {
    return `/comment/${commentId}`
  }

  if (tweetId != null) {
    return `/tweet/${tweetId}`
  }

  if (commentId != null) {
    return `/comment/${commentId}`
  }

  return `/@${senderUsername}`
}

export function NotificationsPage() {
  const query = useNotifications()
  const markRead = useMarkNotificationAsRead()
  const markAllRead = useMarkAllNotificationsAsRead()

  const notifications = query.data?.pages.flatMap((page) => page.content) ?? []
  const { data: unreadCount = 0 } = useUnreadNotificationCount()

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-bold">Notifications</h1>

        <Button
          variant="quiet"
          disabled={unreadCount === 0 || markAllRead.isPending}
          onClick={() => markAllRead.mutate()}
        >
          Mark all as read
        </Button>
      </div>

      <PagedList
        query={query}
        isEmpty={notifications.length === 0}
        emptyText="No notifications yet."
        errorText="Couldn't load notifications."
      >
        <div className="divide-y divide-line overflow-hidden rounded-xl border border-line bg-white">
          {notifications.map((notification) => (
            <Link
              key={notification.id}
              to={getNotificationTarget(notification)}
              onClick={() => {
                if (!notification.read) markRead.mutate(notification.id)
              }}
              className={`flex gap-3 p-4 transition hover:bg-gray-50 ${
                notification.read ? '' : 'bg-blue-50'
              }`}
            >
              <div className="flex size-10 shrink-0 items-center justify-center rounded-full bg-gray-100 font-bold text-brand">
                {(notification.senderDisplayName || notification.senderUsername)
                  .charAt(0)
                  .toUpperCase()}
              </div>

              <div className="min-w-0 flex-1">
                <p className="text-sm text-ink">
                  <span className="font-semibold">
                    {notification.senderDisplayName || notification.senderUsername}
                  </span>{' '}
                  {notification.message}
                </p>

                <p className="mt-1 text-xs text-muted">
                  {new Date(notification.createdAt).toLocaleString()}
                </p>
              </div>

              {!notification.read && (
                <span
                  className="mt-2 size-2 shrink-0 rounded-full bg-brand"
                  aria-label="Unread"
                />
              )}
            </Link>
          ))}
        </div>
      </PagedList>
    </section>
  )
}