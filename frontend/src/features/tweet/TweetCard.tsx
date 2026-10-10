import { useState, type ReactNode } from 'react'
import type { QuotedCommentResponse, QuotedTweetResponse, TweetResponse } from '../../api/types'
import { Avatar } from '../../components/Avatar'
import { CommentIcon, HeartIcon } from '../../components/Icons'
import { formatCount, formatFullDate, formatTimeAgo } from '../../lib/format'

/** Takes tweetResponse and turns it into a tweet card. */

//Shows the time of the post in a compact format, with a full date on hover.
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
        <span className="truncate font-semibold">@{post.username}</span>
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

// Displays a single stat (like count or reply count) with an icon and a label for screen readers.
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

// TypeScript expect tweetResponse, It combines all above, alongside conditons for quoted ones
export function TweetCard({ tweet }: { tweet: TweetResponse }) {

  /* A post can quote either a tweet or a comment, or nothing at all.*/
  const quoted = tweet.quotedTweet ?? tweet.quotedComment

  return (
    <article className="flex gap-3 px-4 py-4">
      <Avatar username={tweet.username} src={tweet.userAvatarUrl} />

      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-1.5 text-sm">
          <span className="truncate font-semibold">@{tweet.username}</span>
          <span className="shrink-0 text-muted">
            · <PostTime iso={tweet.createdAt} />
            {tweet.edited && ' · edited'}
          </span>
        </div>

        {/* The content and image of the tweet, if any. Conditional Rendering -->*/}
        {tweet.content && (
          <p className="mt-1 whitespace-pre-wrap wrap-break-word">{tweet.content}</p>
        )}
        {tweet.imageUrl && <PostImage src={tweet.imageUrl} className="mt-3 max-h-96" />}

        {quoted ? (
          <QuotedPost post={quoted} />
        ) : (
          tweet.quoteUnavailable && (
            <div className="mt-3 rounded-xl border border-line bg-frost p-3 text-sm text-muted">
              This post is unavailable.
            </div>
          )
        )}

        {/* Read-only counts for now. These become buttons when the interactions are built. */}
        <div className="mt-3 flex items-center gap-6 text-sm text-muted">
          <Stat icon={<CommentIcon />} count={tweet.commentCount} label="replies" />
          <Stat icon={<HeartIcon />} count={tweet.likeCount} label="likes" />
        </div>
      </div>
    </article>
  )
}