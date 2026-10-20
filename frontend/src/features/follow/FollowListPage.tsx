import { Link, useParams } from 'react-router-dom'
import { Avatar } from '../../components/Avatar'
import { PagedList } from '../../components/PagedList'
import { useFollowList } from './useFollow'
import { usernameFromHandle, useProfile } from './useProfile'

export function FollowListPage() {
  const { handle, list } = useParams()
  const username = usernameFromHandle(handle)
  const kind = list === 'followers' || list === 'following' ? list : null
  const profile = useProfile(kind ? username : null)
  const follows = useFollowList(profile.data?.id, kind ?? 'followers')

  const back = (
    <Link
      to={username ? `/@${username}` : '/'}
      className="mb-4 inline-block text-sm font-semibold text-brand hover:underline"
    >
      ← {username ? `@${username}` : 'Home'}
    </Link>
  )

  if (!kind || profile.notFound) {
    return (
      <>
        {back}
        <p className="rounded-xl border border-line bg-white p-6 text-muted">This page doesn't exist.</p>
      </>
    )
  }

  return (
    <>
      {back}
      <h1 className="mb-4 text-2xl font-bold tracking-tight">
        {kind === 'followers' ? 'Followers' : 'Following'}
      </h1>
      {profile.isError ? (
        <p role="alert" className="text-danger">
          Couldn't load this profile.
        </p>
      ) : (
        <PagedList
          query={profile.data ? follows : { ...follows, status: 'pending' }}
          isEmpty={follows.users.length === 0}
          emptyText={kind === 'followers' ? 'No followers yet.' : 'Not following anyone yet.'}
          errorText="Couldn't load this list."
        >
          <div className="divide-y divide-line rounded-xl border border-line bg-white">
            {follows.users.map((user) => (
              <Link
                key={user.id}
                to={`/@${user.username}`}
                className="flex items-center gap-3 px-4 py-3 first:rounded-t-xl last:rounded-b-xl hover:bg-frost/60"
              >
                <Avatar username={user.username} src={user.avatarURL} />
                <span className="min-w-0">
                  <span className="block truncate font-semibold">{user.displayName || user.username}</span>
                  <span className="block truncate text-sm text-muted">@{user.username}</span>
                </span>
              </Link>
            ))}
          </div>
        </PagedList>
      )}
    </>
  )
}