import type { ReactNode } from 'react'
import { getErrorMessage } from '../../api/errors'
import type { TweetResponse } from '../../api/types'
import { BookmarkIcon, CommentIcon, HeartIcon, RepostIcon } from '../../components/Icons'
import { formatCount } from '../../lib/format'
import { useBookmarkTweet, useLikeTweet, useRepostTweet } from './interactionMutations'

/* This component displays the action bar for a tweet, including buttons for replying, reposting, liking, and bookmarking.
It uses the ToggleAction component for each action button, which shows the current state (active/inactive) and count of interactions. 
The TweetActionBar component also handles the logic for performing these actions using React Query mutations, 
and displays any error messages that occur during these operations. */

interface ToggleActionProps {
  label: string
  active: boolean
  activeClass: string
  count?: number
  pending?: boolean
  onClick: () => void
  children: ReactNode
}

/** A pressed/unpressed button with an icon and an optional count (like, repost, bookmark). */
export function ToggleAction({
  label,
  active,
  activeClass,
  count,
  pending = false,
  onClick,
  children,
}: ToggleActionProps) {
  return (
    <button
      type="button"
      aria-pressed={active}
      onClick={onClick}
      disabled={pending}
      className={`inline-flex items-center gap-1.5 rounded px-1 text-sm transition-colors focus-visible:outline-2 focus-visible:outline-brand disabled:opacity-60 ${
        active ? activeClass : 'text-muted hover:text-ink'
      }`}
    >
      <span className="sr-only">{label}</span>
      {children}
      {count !== undefined && count > 0 && <span aria-hidden="true">{formatCount(count)}</span>}
    </button>
  )
}

interface TweetActionBarProps {
  tweet: TweetResponse
  /** True on the tweet's own page, where the reply box is already on screen. */
  detail?: boolean
  onReply: () => void
}

export function TweetActionBar({ tweet, detail = false, onReply }: TweetActionBarProps) {
  const like = useLikeTweet()
  const repost = useRepostTweet()
  const bookmark = useBookmarkTweet()
  const error = like.error ?? repost.error ?? bookmark.error

    const replyContent = (label: string) => (
    <>
      <span className="sr-only">{label}</span>
      <CommentIcon />
      {tweet.commentCount > 0 && <span aria-hidden="true">{formatCount(tweet.commentCount)}</span>}
    </>
  )

  return (
    <div className="mt-3">
      <div className="flex items-center gap-5 text-sm text-muted">
                {detail ? (
          <span className="inline-flex items-center gap-1.5 px-1">{replyContent('Replies')}</span>
        ) : (
          <button
            type="button"
            onClick={onReply}
            className="inline-flex items-center gap-1.5 rounded px-1 hover:text-ink focus-visible:outline-2 focus-visible:outline-brand"
          >
            {replyContent('Reply')}
          </button>
        )}

        <ToggleAction
          label="Repost"
          active={tweet.retweetedByMe}
          activeClass="text-repost"
          count={tweet.retweetCount}
          pending={repost.isPending}
          onClick={() => repost.mutate({ tweet, enable: !tweet.retweetedByMe })}
        >
          <RepostIcon strokeWidth={tweet.retweetedByMe ? 2.6 : 1.8} />
        </ToggleAction>

        <ToggleAction
          label="Like"
          active={tweet.likedByMe}
          activeClass="text-like"
          count={tweet.likeCount}
          pending={like.isPending}
          onClick={() => like.mutate({ tweet, enable: !tweet.likedByMe })}
        >
          <HeartIcon filled={tweet.likedByMe} />
        </ToggleAction>

        <ToggleAction
          label="Bookmark"
          active={tweet.bookmarkedByMe}
          activeClass="text-brand"
          pending={bookmark.isPending}
          onClick={() => bookmark.mutate({ tweet, enable: !tweet.bookmarkedByMe })}
        >
          <BookmarkIcon filled={tweet.bookmarkedByMe} />
        </ToggleAction>
      </div>

      {error && (
        <p role="alert" className="mt-2 text-sm text-danger">
          {getErrorMessage(error)}
        </p>
      )}
    </div>
  )
}