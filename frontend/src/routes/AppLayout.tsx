import type { ReactNode } from 'react'
import { Link, NavLink, Outlet } from 'react-router-dom'
import { Avatar } from '../components/Avatar'
import {
  BellIcon,
  BookmarkIcon,
  HomeIcon,
  LogoutIcon,
  MoonIcon,
  SunIcon,
  UserIcon,
} from '../components/Icons'
import { useAuthStore } from '../features/auth/authStore'
import { WhoToFollow } from '../features/follow/WhoToFollow'
import { useUnreadNotificationCount } from '../features/notification/useNotifications'
import { useDarkMode } from '../lib/theme'

//this is common layout for the app, including a header with a logout button and a main content area where the current route's component will be rendered.
/*
 * App shell. Wide screens: left menu, feed in the middle, "Who to follow" on the right.
 * Medium screens: icon-only left menu. Phones: slim top bar and a bottom tab bar.
 */

const ICON = { width: 22, height: 22 }

const itemClass =
  'flex w-full items-center justify-center gap-3 rounded-full px-3 py-2.5 text-[15px] transition-colors lg:justify-start'

function Badge({ count }: { count: number }) {
  if (count <= 0) return null
  return (
    <span
      aria-hidden="true"
      className="absolute -right-2 -top-1.5 min-w-4 rounded-full bg-brand px-1 text-center text-[10px] font-bold leading-4 text-white"
    >
      {count > 99 ? '99+' : count}
    </span>
  )
}

interface NavProps {
  to: string
  label: string
  icon: ReactNode
  end?: boolean
  badge?: number
}

function SideLink({ to, label, icon, end, badge = 0 }: NavProps) {
  return (
    <NavLink
      to={to}
      end={end}
      aria-label={badge > 0 ? `${label}, ${badge} unread` : label}
      className={({ isActive }) =>
        `${itemClass} ${isActive ? 'bg-brand/10 font-bold text-brand' : 'text-muted hover:bg-white hover:text-ink'}`
      }
    >
      <span className="relative">
        {icon}
        <Badge count={badge} />
      </span>
      <span className="hidden lg:inline">{label}</span>
    </NavLink>
  )
}

function TabLink({ to, label, icon, end, badge = 0 }: NavProps) {
  return (
    <NavLink
      to={to}
      end={end}
      aria-label={badge > 0 ? `${label}, ${badge} unread` : label}
      className={({ isActive }) =>
        `relative flex flex-1 justify-center py-3 transition-colors ${isActive ? 'text-brand' : 'text-muted hover:text-ink'}`
      }
    >
      <span className="relative">
        {icon}
        <Badge count={badge} />
      </span>
    </NavLink>
  )
}

export function AppLayout() {
  const user = useAuthStore((state) => state.user)
  const logout = useAuthStore((state) => state.logout)
  const { data: unread = 0 } = useUnreadNotificationCount()
  const [dark, toggleDark] = useDarkMode()

  const profilePath = user ? `/@${user.username}` : '/'
  const themeLabel = dark ? 'Light mode' : 'Night mode'
  const themeIcon = dark ? <SunIcon {...ICON} /> : <MoonIcon {...ICON} />

  return (
    <div className="min-h-dvh">
      {/* Phones: top bar */}
      <header className="sticky top-0 z-10 flex items-center justify-between border-b border-line bg-white/90 px-4 py-3 backdrop-blur md:hidden">
        <Link to="/" className="text-xl font-extrabold tracking-tighter text-brand">
          twittvl
        </Link>
        <div className="flex items-center gap-1 text-muted">
          <button
            type="button"
            onClick={toggleDark}
            aria-label={themeLabel}
            className="rounded-full p-2 hover:bg-frost hover:text-ink"
          >
            {themeIcon}
          </button>
          <button
            type="button"
            onClick={() => void logout()}
            aria-label="Log out"
            className="rounded-full p-2 hover:bg-frost hover:text-ink"
          >
            <LogoutIcon {...ICON} />
          </button>
        </div>
      </header>

      <div className="mx-auto flex max-w-6xl justify-center gap-6 md:px-4">
        {/* Medium and wide screens: left menu */}
        <aside className="sticky top-0 hidden h-dvh w-16 shrink-0 flex-col py-5 md:flex lg:w-56">
          <Link
            to="/"
            className="mb-5 px-3 text-center text-xl font-extrabold tracking-tighter text-brand lg:text-left"
          >
            <span className="lg:hidden">t</span>
            <span className="hidden lg:inline">twittvl</span>
          </Link>

          <nav aria-label="Main" className="flex flex-col gap-1">
            <SideLink to="/" label="Home" end icon={<HomeIcon {...ICON} />} />
            <SideLink to="/notifications" label="Notifications" badge={unread} icon={<BellIcon {...ICON} />} />
            <SideLink to="/bookmarks" label="Bookmarks" icon={<BookmarkIcon {...ICON} />} />
            <SideLink to={profilePath} label="Profile" icon={<UserIcon {...ICON} />} />
          </nav>

          <div className="mt-auto space-y-1">
            <button
              type="button"
              onClick={toggleDark}
              aria-label={themeLabel}
              className={`${itemClass} text-muted hover:bg-white hover:text-ink`}
            >
              {themeIcon}
              <span className="hidden lg:inline">{themeLabel}</span>
            </button>
            <button
              type="button"
              onClick={() => void logout()}
              aria-label="Log out"
              className={`${itemClass} text-muted hover:bg-white hover:text-ink`}
            >
              <LogoutIcon {...ICON} />
              <span className="hidden lg:inline">Log out</span>
            </button>

            {user && (
              <Link
                to={profilePath}
                className="mt-2 flex items-center justify-center gap-3 rounded-full p-2 hover:bg-white lg:justify-start"
              >
                <Avatar username={user.username} src={user.avatarURL} />
                <span className="hidden min-w-0 lg:block">
                  <span className="block truncate text-sm font-semibold">{user.displayName || user.username}</span>
                  <span className="block truncate text-sm text-muted">@{user.username}</span>
                </span>
              </Link>
            )}
          </div>
        </aside>

        <main className="min-w-0 max-w-2xl flex-1 px-4 py-6 pb-24 md:px-0 md:pb-6">
          <Outlet />
        </main>

        {/* Wide screens: right column */}
        <aside className="hidden w-72 shrink-0 py-6 xl:block">
          <div className="sticky top-6">
            <WhoToFollow />
          </div>
        </aside>
      </div>

      {/* Phones: bottom tab bar */}
      <nav
        aria-label="Main"
        className="fixed inset-x-0 bottom-0 z-10 flex border-t border-line bg-white/95 backdrop-blur md:hidden"
      >
        <TabLink to="/" label="Home" end icon={<HomeIcon {...ICON} />} />
        <TabLink to="/notifications" label="Notifications" badge={unread} icon={<BellIcon {...ICON} />} />
        <TabLink to="/bookmarks" label="Bookmarks" icon={<BookmarkIcon {...ICON} />} />
        <TabLink to={profilePath} label="Profile" icon={<UserIcon {...ICON} />} />
      </nav>
    </div>
  )
}