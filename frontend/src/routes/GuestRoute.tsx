import { Navigate, Outlet, useLocation, type Location } from 'react-router-dom'
import { FullPageSpinner } from '../components/Spinner'
import { useAuthStore } from '../features/auth/authStore'

/** For login and register: logged-in users are sent on to where they were headed (or home). */
//oposite of protected, for people who aren't logged in.
export function GuestRoute() {
  const status = useAuthStore((state) => state.status)
  const location = useLocation()

  if (status === 'loading') return <FullPageSpinner />
  if (status === 'authenticated') {
    const from = (location.state as { from?: Location } | null)?.from
    const target = from ? from.pathname + from.search + from.hash : '/'
    return <Navigate to={target} replace /> //if authenticated, redirect to the page they were trying to access, or home if none.
  }
  return <Outlet />
}