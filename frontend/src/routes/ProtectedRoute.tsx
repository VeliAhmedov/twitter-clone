import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { FullPageSpinner } from '../components/Spinner'
import { useAuthStore } from '../features/auth/authStore'

/** Wrap any routes that need a logged-in user. Remembers where the visitor was headed. */
//Main functionality is protect pages that require login
export function ProtectedRoute() {
  const status = useAuthStore((state) => state.status)
  const location = useLocation()

  if (status === 'loading') return <FullPageSpinner /> //if loading, show spinner
  if (status === 'anonymous') {
    return <Navigate to="/login" replace state={{ from: location }} /> //if anon redirect to login
  }
  return <Outlet /> //otherwise, The user is authenticated, so show the protected page
}