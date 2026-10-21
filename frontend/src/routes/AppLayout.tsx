import { Link, Outlet } from 'react-router-dom'
import { Button } from '../components/Button'
import { useAuthStore } from '../features/auth/authStore'
import { BellIcon } from '../components/Icons'
import { useUnreadNotificationCount } from '../features/notification/useNotifications'

//this is common layout for the app, including a header with a logout button and a main content area where the current route's component will be rendered.

export function AppLayout() {
  const user = useAuthStore((state) => state.user)
  const logout = useAuthStore((state) => state.logout)
  const { data: unreadCount = 0 } = useUnreadNotificationCount()

  return (
    <div className="min-h-dvh">
      <header className="sticky top-0 z-10 border-b border-line bg-white/90 backdrop-blur">
        <div className="mx-auto flex max-w-2xl items-center justify-between px-4 py-3">
          <Link to="/" className="text-xl font-extrabold tracking-tighter text-brand"> 
            twittvl
          </Link>
          <div className="flex items-center gap-3"> 
            <Link to="/notifications" aria-label={`Notifications${unreadCount > 0 ? `, ${unreadCount} unread` : ''}`}
               className="relative flex items-center gap-1 text-sm font-semibold text-muted hover:text-ink">
            <BellIcon />
                <span>Notifications</span>

                {unreadCount > 0 && (
                <span className="flex min-w-5 items-center justify-center rounded-full bg-brand px-1.5 py-0.5 text-xs font-bold text-white">
                 {unreadCount > 99 ? '99+' : unreadCount}
                  </span>
                   )}
            </Link>
            
            <Link to="/bookmarks" className="text-sm font-semibold text-muted hover:text-ink">
              Bookmarks
            </Link>
            {user && (
              <Link to={`/@${user.username}`} className="text-sm text-muted hover:text-ink">
                @{user.username}
              </Link>
            )}
            <Button variant="quiet" onClick={() => void logout()}> 
              Log out
            </Button>
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-2xl px-4 py-6">
        <Outlet />
      </main>
    </div>
  )
}