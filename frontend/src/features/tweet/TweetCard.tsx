import { useState, type MouseEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import type { QuotedCommentResponse, QuotedTweetResponse, TweetResponse } from '../../api/types'
import { Avatar } from '../../components/Avatar'
import { MoreMenu, type MenuItem } from '../../components/MoreMenu'
import { useAuthStore } from '../auth/authStore'
import { PostImage, PostTime } from './PostParts'
import { TweetActionBar } from './TweetActionBar'
import { DeleteTweetConfirm, EditTweetForm, QuoteTweetForm, ReplyToTweetForm } from './TweetForms'

/* This component displays a tweet or comment, including its author, content, and action buttons.
It handles different modes for replying, editing, quoting, and deleting the tweet. 
The component uses the TweetActionBar for the action buttons and the specialized forms for each mode. */

type Mode = 'view' | 'reply' | 'edit' | 'quote' | 'delete'

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

interface TweetCardProps {
  tweet: TweetResponse
  /** Called after this tweet was deleted (the tweet page uses it to leave the page). */
  onDeleted?: () => void
  /** True when this card is the main tweet on its own page. */
  detail?: boolean
}

export function TweetCard({ tweet, onDeleted, detail = false }: TweetCardProps) {
  const currentUserId = useAuthStore((state) => state.user?.id)
  const isOwner = currentUserId === tweet.userId
  const navigate = useNavigate()
  const [mode, setMode] = useState<Mode>('view')
  const quoted = tweet.quotedTweet ?? tweet.quotedComment
  const close = () => setMode('view')

  // Clicking the card itself opens the tweet, but not when the click was on a button,
  // link or form, while something is being edited, or while the user selects text.
  function openTweet(event: MouseEvent<HTMLElement>) {
    if (detail || mode !== 'view') return
    const target = event.target as HTMLElement
    if (target.closest('a, button, input, textarea, form')) return
    if (window.getSelection()?.toString()) return
    navigate(`/tweet/${tweet.id}`)
  }

  const menuItems: MenuItem[] = [
    { label: 'Quote', onSelect: () => setMode('quote') },
    ...(isOwner
      ? [
          { label: 'Edit', onSelect: () => setMode('edit') },
          { label: 'Delete', danger: true, onSelect: () => setMode('delete') },
        ]
      : []),
  ]

  return (
    <article
      onClick={openTweet}
      className={`flex gap-3 px-4 py-4 ${detail ? '' : 'cursor-pointer hover:bg-frost/60'}`}
    >
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
          <div className="-my-1.5 ml-auto shrink-0">
            <MoreMenu items={menuItems} />
          </div>
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

        <TweetActionBar
          tweet={tweet}
          detail={detail}
          onReply={() => setMode(mode === 'reply' ? 'view' : 'reply')}
        />

        {mode === 'reply' && (
          <div className="mt-3">
            <ReplyToTweetForm tweet={tweet} onDone={close} />
          </div>
        )}
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