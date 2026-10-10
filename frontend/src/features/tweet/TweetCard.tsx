import { useState, type ReactNode } from 'react'
import { Link } from 'react-router-dom'
import type { QuotedCommentResponse, QuotedTweetResponse, TweetResponse } from '../../api/types'
import { Avatar } from '../../components/Avatar'
import { CommentIcon, HeartIcon } from '../../components/Icons'
import { formatCount, formatFullDate, formatTimeAgo } from '../../lib/format'
import { useAuthStore } from '../auth/authStore'
import { DeleteTweetConfirm, EditTweetForm, QuoteTweetForm } from './TweetForms'

/*Takes tweetResponse and turn to tweet card, which shows the tweet's author, content, image, quoted post (if any), 
and action buttons for quoting, editing, or deleting the tweet. It also handles the state for editing, quoting, and 
deleting modes, and uses the appropriate forms for those actions. */

type Mode = 'view' | 'edit' | 'quote' | 'delete'

function PostTime({ iso }: { iso: string }) {
  return (
    <time dateTime={iso} title={formatFullDate(iso)}>
      {formatTimeAgo(iso)}
    </time>
  )
}

// Image URLs are typed in by users, so some will be broken: hide those instead of showing a broken icon.
function PostImage({ src, className = '' }: { src: string; className?: string }) {
  const [failed, setFailed] = useState(false)
  if (failed) return null
  return (
    <img
      src={src}
      alt="Attached to post"
      loading="lazy"
      referrerPolicy="no-referrer"
      onError={() => setFailed(true)}
      className={`w-full rounded-xl border border-line object-cover ${className}`}
    />
  )
}

// Displays an embedded tweet or comment, including its author and content.
function QuotedPost({ post }: { post: QuotedTweetResponse | QuotedCommentResponse }) {
  return (
    <div className="mt-3 rounded-xl border border-line p-3">
      <div className="flex items-center gap-2 text-sm">
        <Avatar username={post.username} src={post.userAvatarUrl} size="sm" />
        <span className="min-w-0 truncate font-semibold">{post.userDisplayName || post.username}</span>
        <span className="min-w-0 truncate text-muted">@{post.username}</span>
        <span className="shrink-0 text-muted">
          · <PostTime iso={post.createdAt} />
        </span>
      </div>
      {post.content && (
        <p className="mt-1 line-clamp-4 whitespace-pre-wrap wrap-break-word text-sm">{post.content}</p>
      )}
      {post.imageUrl && <PostImage src={post.imageUrl} className="mt-2 max-h-48" />}
    </div>
  )
}

function Stat({ icon, count, label }: { icon: ReactNode; count: number; label: string }) {
  return (
    <span className="inline-flex items-center gap-1.5">
      {icon}
      <span aria-hidden="true">{count > 0 ? formatCount(count) : ''}</span>
      <span className="sr-only">
        {count} {label}
      </span>
    </span>
  )
}

function ActionButton({ onClick, children }: { onClick: () => void; children: ReactNode }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className="rounded px-1 text-sm font-medium text-muted hover:text-ink focus-visible:outline-2 focus-visible:outline-brand"
    >
      {children}
    </button>
  )
}

interface TweetCardProps {
  tweet: TweetResponse
  /** Called after this tweet was deleted (the tweet page uses it to leave the page). */
  onDeleted?: () => void
}

export function TweetCard({ tweet, onDeleted }: TweetCardProps) {
  const currentUserId = useAuthStore((state) => state.user?.id)
  const isOwner = currentUserId === tweet.userId
  const [mode, setMode] = useState<Mode>('view')
  const quoted = tweet.quotedTweet ?? tweet.quotedComment

  const toggle = (next: Mode) => setMode((current) => (current === next ? 'view' : next))
  const close = () => setMode('view')

  return (
    <article className="flex gap-3 px-4 py-4">
      <Avatar username={tweet.username} src={tweet.userAvatarUrl} />

      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-1.5 text-sm">
          <span className="min-w-0 truncate font-semibold">
            {tweet.userDisplayName || tweet.username}
          </span>
          <span className="min-w-0 truncate text-muted">@{tweet.username}</span>
          <span className="shrink-0 text-muted">
            ·{' '}
            <Link to={`/tweet/${tweet.id}`} className="hover:underline">
              <PostTime iso={tweet.createdAt} />
            </Link>
            {tweet.edited && ' · edited'}
          </span>
        </div>

        {mode === 'edit' ? (
          <div className="mt-2">
            <EditTweetForm tweet={tweet} onDone={close} />
          </div>
        ) : (
          <>
            {tweet.content && (
              <p className="mt-1 whitespace-pre-wrap wrap-break-word">{tweet.content}</p>
            )}
            {tweet.imageUrl && <PostImage src={tweet.imageUrl} className="mt-3 max-h-96" />}
          </>
        )}

        {quoted ? (
          <QuotedPost post={quoted} />
        ) : (
          tweet.quoteUnavailable && (
            <div className="mt-3 rounded-xl border border-line bg-frost p-3 text-sm text-muted">
              This post is unavailable.
            </div>
          )
        )}

        <div className="mt-3 flex items-center gap-6 text-sm text-muted">
          <Stat icon={<CommentIcon />} count={tweet.commentCount} label="replies" />
          <Stat icon={<HeartIcon />} count={tweet.likeCount} label="likes" />
          <div className="ml-auto flex items-center gap-3">
            <ActionButton onClick={() => toggle('quote')}>Quote</ActionButton>
            {isOwner && (
              <>
                <ActionButton onClick={() => toggle('edit')}>Edit</ActionButton>
                <ActionButton onClick={() => toggle('delete')}>Delete</ActionButton>
              </>
            )}
          </div>
        </div>

        {mode === 'quote' && (
          <div className="mt-3">
            <QuoteTweetForm tweet={tweet} onDone={close} />
          </div>
        )}
        {mode === 'delete' && (
          <DeleteTweetConfirm tweet={tweet} onCancel={close} onDeleted={onDeleted} />
        )}
      </div>
    </article>
  )
}