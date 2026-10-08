import { Link, Outlet } from 'react-router-dom'
import { Button } from '../components/Button'
import { useAuthStore } from '../features/auth/authStore'

//this is common layout for the app, including a header with a logout button and a main content area where the current route's component will be rendered.

export function AppLayout() {
  const user = useAuthStore((state) => state.user)
  const logout = useAuthStore((state) => state.logout)

  return (
    <div className="min-h-dvh">
      <header className="sticky top-0 z-10 border-b border-line bg-white/90 backdrop-blur">
        <div className="mx-auto flex max-w-2xl items-center justify-between px-4 py-3">
          <Link to="/" className="text-xl font-extrabold tracking-tighter text-brand"> 
            twittvl
          </Link>
          <div className="flex items-center gap-3"> 
            {user && <span className="text-sm text-muted">@{user.username}</span>} 
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