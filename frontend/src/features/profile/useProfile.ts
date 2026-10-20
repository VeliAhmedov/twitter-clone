import { useQuery } from '@tanstack/react-query'
import { isAxiosError } from 'axios'
import { fetchUserByUsername } from '../auth/authApi'

/*
 * This module provides utility functions and a React hook for managing user profile data in a frontend application.
 * It includes a function to extract a username from a handle string and a hook to fetch user profile data based on the username.
 * The `useProfile` hook uses React Query to manage the fetching and caching of user profile data, handling loading, error, and not-found states.
 */

/** "/@bob" arrives as the route parameter "@bob". Returns "bob", or null when it isn't a handle. */
export function usernameFromHandle(handle: string | undefined): string | null {
  if (!handle || !handle.startsWith('@') || handle.length < 2) return null
  return handle.slice(1)
}

export function useProfile(username: string | null) {
  const query = useQuery({
    queryKey: ['profile', username],
    queryFn: () => fetchUserByUsername(username ?? ''),
    enabled: username !== null,
    retry: false,
  })
  const notFound =
    username === null ||
    (query.isError && isAxiosError(query.error) && query.error.response?.status === 404)
  return { ...query, notFound }
}