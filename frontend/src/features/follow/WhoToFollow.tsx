import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { Avatar } from '../../components/Avatar'
import { useAuthStore } from '../auth/authStore'
import { fetchUserSuggestions } from './followApi'
import { FollowButton } from './FollowButton'

//Displays suggested users and lets the current user follow them.

/** Small card for the right column on wide screens. Hidden when there is nobody to suggest. */
export function WhoToFollow() {
  const currentUserId = useAuthStore((state) => state.user?.id)
  const query = useQuery({
    queryKey: ['who-to-follow'],
    queryFn: fetchUserSuggestions,
    staleTime: 5 * 60_000,
    retry: false,
  })

  const people = (query.data ?? []).filter((user) => user.id !== currentUserId).slice(0, 4)
  if (people.length === 0) return null

  return (
    <section aria-labelledby="who-to-follow" className="rounded-xl border border-line bg-white">
      <h2 id="who-to-follow" className="px-4 pt-4 text-lg font-bold tracking-tight">
        Who to follow
      </h2>
      <ul className="divide-y divide-line">
        {people.map((user) => (
          <li key={user.id} className="flex items-center gap-3 px-4 py-3">
            <Link to={`/@${user.username}`} className="flex min-w-0 flex-1 items-center gap-3">
              <Avatar username={user.username} src={user.avatarURL} />
              <span className="min-w-0">
                <span className="block truncate font-semibold hover:underline">
                  {user.displayName || user.username}
                </span>
                <span className="block truncate text-sm text-muted">@{user.username}</span>
              </span>
            </Link>
            <FollowButton userId={user.id} />
          </li>
        ))}
      </ul>
    </section>
  )
}