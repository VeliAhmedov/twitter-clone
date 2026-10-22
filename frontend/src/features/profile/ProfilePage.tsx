import { useState } from 'react'
import { Link, useParams, useSearchParams } from 'react-router-dom'
import { getErrorMessage } from '../../api/errors'
import type { UserResponse } from '../../api/types'
import { Avatar } from '../../components/Avatar'
import { Button } from '../../components/Button'
import { formatCount } from '../../lib/format'
import { useAuthStore } from '../auth/authStore'
import { PostsList } from '../posts/PostsList'
import type { PostsSource } from '../posts/postsApi'
import { FollowButton } from '../follow/FollowButton'
import { useFollowStats } from '../follow/useFollow'
import { usernameFromHandle, useProfile } from './useProfile'
import { EditProfileForm } from './EditProfileForm'


type TabId = 'posts' | 'reposts' | 'replies'

const TABS: { id: TabId; label: string; kind: 'tweets' | 'reposts' | 'replies'; empty: string }[] = [
  { id: 'posts', label: 'Posts', kind: 'tweets', empty: 'No posts yet.' },
  { id: 'reposts', label: 'Reposts', kind: 'reposts', empty: 'No reposts yet.' },
  { id: 'replies', label: 'Replies', kind: 'replies', empty: 'No replies yet.' },
]

function BackLink() {
  return (
    <Link to="/" className="mb-4 inline-block text-sm font-semibold text-brand hover:underline">
      ← Home
    </Link>
  )
}

function ProfileHeader({ user, isOwn }: { user: UserResponse; isOwn: boolean }) {
  const stats = useFollowStats(user.id)
  const joined = new Date(user.createdAt).toLocaleDateString(undefined, { month: 'long', year: 'numeric' })

  const [editing, setEditing] = useState(false)
  // ...after `const joined = ...`:
  if (editing) return <EditProfileForm user={user} onDone={() => setEditing(false)} />

  return (
    <section className="rounded-xl border border-line bg-white p-5">
      <div className="flex items-start justify-between gap-4">
        <Avatar username={user.username} src={user.avatarURL} size="xl" />
                {isOwn ? (
          <Button variant="quiet" onClick={() => setEditing(true)}>
            Edit profile
          </Button>
        ) : (
          <FollowButton userId={user.id} />
        )}
      </div>

      <h1 className="mt-3 text-xl font-bold tracking-tight wrap-break-word">
        {user.displayName || user.username}
      </h1>
      <p className="text-muted">@{user.username}</p>
      {user.bio && <p className="mt-3 whitespace-pre-wrap wrap-break-word">{user.bio}</p>}
      {!Number.isNaN(Date.parse(user.createdAt)) && (
        <p className="mt-3 text-sm text-muted">Joined {joined}</p>
      )}

      <p className="mt-3 flex gap-5 text-sm">
        <Link to={`/@${user.username}/following`} className="hover:underline">
          <span className="font-bold">{stats.data ? formatCount(stats.data.followingCount) : '–'}</span>{' '}
          <span className="text-muted">Following</span>
        </Link>
        <Link to={`/@${user.username}/followers`} className="hover:underline">
          <span className="font-bold">{stats.data ? formatCount(stats.data.followerCount) : '–'}</span>{' '}
          <span className="text-muted">Followers</span>
        </Link>
      </p>
    </section>
  )
}

export function ProfilePage() {
  const { handle } = useParams()
  const username = usernameFromHandle(handle)
  const profile = useProfile(username)
  const currentUserId = useAuthStore((state) => state.user?.id)
  const [searchParams, setSearchParams] = useSearchParams()

  const tab = TABS.find((candidate) => candidate.id === searchParams.get('tab')) ?? TABS[0]

  if (profile.notFound) {
    return (
      <>
        <BackLink />
        <p className="rounded-xl border border-line bg-white p-6 text-muted">This account doesn't exist.</p>
      </>
    )
  }

  if (profile.isError) {
    return (
      <>
        <BackLink />
        <div role="alert" className="space-y-3 rounded-xl border border-line bg-white p-6">
          <p className="text-danger">{getErrorMessage(profile.error, "Couldn't load this profile.")}</p>
          <Button variant="quiet" onClick={() => void profile.refetch()}>
            Try again
          </Button>
        </div>
      </>
    )
  }

  const user = profile.data
  if (!user) {
    return (
      <>
        <BackLink />
        <p className="text-muted">Loading…</p>
      </>
    )
  }

  const source: PostsSource = { kind: tab.kind, userId: user.id }

  return (
    <>
      <BackLink />
      <div className="space-y-4">
        <ProfileHeader user={user} isOwn={currentUserId === user.id} />

        <div role="tablist" aria-label="Profile sections" className="flex border-b border-line">
          {TABS.map((candidate) => (
            <button
              key={candidate.id}
              type="button"
              role="tab"
              aria-selected={candidate.id === tab.id}
              onClick={() => setSearchParams(candidate.id === 'posts' ? {} : { tab: candidate.id }, { replace: true })}
              className={`-mb-px border-b-2 px-4 py-2.5 text-sm font-semibold transition-colors focus-visible:outline-2 focus-visible:outline-brand ${
                candidate.id === tab.id
                  ? 'border-brand text-ink'
                  : 'border-transparent text-muted hover:text-ink'
              }`}
            >
              {candidate.label}
            </button>
          ))}
        </div>

        <PostsList
          key={tab.id}
          source={source}
          reposterName={user.displayName || user.username}
          emptyText={tab.empty}
        />
      </div>
    </>
  )
}