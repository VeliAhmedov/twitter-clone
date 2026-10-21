import { useState, type MouseEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getErrorMessage } from '../../api/errors'
import type { CommentResponse } from '../../api/types'
import { Avatar } from '../../components/Avatar'
import { ConfirmDelete } from '../../components/ConfirmDelete'
import { BookmarkIcon, CommentIcon, HeartIcon, RepostIcon } from '../../components/Icons'
import { MoreMenu, type MenuItem } from '../../components/MoreMenu'
import { formatCount } from '../../lib/format'
import { useAuthStore } from '../auth/authStore'
import { buildCommentEditRequest, buildCommentRequest, MAX_COMMENT_LENGTH } from '../tweet/draft'
import { PostImage, PostTime } from '../tweet/PostParts'
import { ToggleAction } from '../tweet/TweetActionBar'
import { TweetForm } from '../tweet/TweetForm'
import {
  useBookmarkComment,
  useCreateReply,
  useDeleteComment,
  useLikeComment,
  useRepostComment,
  useUpdateComment,
} from './commentMutations'
import { QuoteCommentForm } from './QuoteCommentForm'

/*
 * This file contains the CommentCard component, which displays a single comment with its content, author, and actions.
 * It supports viewing, replying, quoting, editing, and deleting comments, as well as liking, reposting, and bookmarking.
 * The component handles different modes and optimistically updates the UI based on user interactions.
 */

type Mode = 'view' | 'reply' | 'edit' | 'quote' | 'delete'

interface CommentCardProps {
  comment: CommentResponse
  /** True when this is the main comment on its own page. */
  detail?: boolean
  /** Thread lines to the post above / below, used on a comment's own page. */
  connectAbove?: boolean
  connectBelow?: boolean
  /** Called after this comment was deleted (its page uses it to leave the page). */
  onDeleted?: () => void
}

// Replies are not shown inside the card. Click a comment to open its own page, where the
// conversation above it and the replies below it are shown, like on Twitter.
export function CommentCard({
  comment,
  detail = false,
  connectAbove = false,
  connectBelow = false,
  onDeleted,
}: CommentCardProps) {
  const currentUserId = useAuthStore((state) => state.user?.id)
  const isOwner = currentUserId === comment.userId
  const navigate = useNavigate()
  const [mode, setMode] = useState<Mode>('view')

  const like = useLikeComment()
  const repost = useRepostComment()
  const bookmark = useBookmarkComment()
  const actionError = like.error ?? repost.error ?? bookmark.error
  const reply = useCreateReply(comment.id)
  const update = useUpdateComment()
  const remove = useDeleteComment()
  const close = () => setMode('view')

  function openComment(event: MouseEvent<HTMLElement>) {
    if (detail || mode !== 'view') return
    const target = event.target as HTMLElement
    if (target.closest('a, button, input, textarea, form')) return
    if (window.getSelection()?.toString()) return
    navigate(`/comment/${comment.id}`)
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

  const replyContent = (label: string) => (
    <>
      <span className="sr-only">{label}</span>
      <CommentIcon />
      {comment.replyCount > 0 && <span aria-hidden="true">{formatCount(comment.replyCount)}</span>}
    </>
  )

  return (
    <article
      onClick={openComment}
      className={`relative flex gap-3 px-4 py-4 first:rounded-t-xl last:rounded-b-xl ${
        detail ? '' : 'cursor-pointer hover:bg-frost/60'
      }`}
    >
      {connectAbove && (
        <span aria-hidden="true" className="absolute left-9.25 top-0 h-3 w-0.5 bg-line" />
      )}
      {connectBelow && (
        <span aria-hidden="true" className="absolute bottom-0 left-9.25 top-16 w-0.5 bg-line" />
      )}

      <Link to={`/@${comment.username}`} aria-label={`@${comment.username}'s profile`} className="shrink-0 self-start">
        <Avatar username={comment.username} src={comment.userAvatarUrl} />
      </Link>

      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-1.5 text-sm">
          <Link to={`/@${comment.username}`} className="min-w-0 truncate font-semibold hover:underline">
            {comment.displayName || comment.username}
          </Link>
          <span className="min-w-0 truncate text-muted">@{comment.username}</span>
          <span className="shrink-0 text-muted">
            ·{' '}
            <Link to={`/comment/${comment.id}`} className="hover:underline">
              <PostTime iso={comment.createdAt} />
            </Link>
            {comment.edited && ' · edited'}
          </span>
          <div className="-my-1.5 ml-auto shrink-0">
            <MoreMenu items={menuItems} label="Comment actions" />
          </div>
        </div>

        {mode === 'edit' ? (
          <div className="mt-2">
            <TweetForm
              autoFocus
              initial={{ content: comment.content ?? '', image: comment.imageUrl ?? '' }}
              maxLength={MAX_COMMENT_LENGTH}
              placeholder="Edit your comment"
              submitLabel="Save"
              pending={update.isPending}
              error={update.error ? getErrorMessage(update.error) : null}
              onCancel={close}
              onSubmit={(draft) => {
                const body = buildCommentEditRequest(comment, draft)
                if (!body) {
                  close()
                  return
                }
                update.mutate({ id: comment.id, body }, { onSuccess: close })
              }}
            />
          </div>
        ) : (
          <>
            {comment.content && (
              <p className="mt-1 whitespace-pre-wrap wrap-break-word">{comment.content}</p>
            )}
            {comment.imageUrl && <PostImage src={comment.imageUrl} className="mt-3 max-h-96" />}
          </>
        )}

        <div className="mt-3 flex items-center gap-5 text-sm text-muted">
          {detail ? (
            <span className="inline-flex items-center gap-1.5 px-1">{replyContent('Replies')}</span>
          ) : (
            <button
              type="button"
              onClick={() => setMode(mode === 'reply' ? 'view' : 'reply')}
              className="inline-flex items-center gap-1.5 rounded px-1 hover:text-ink focus-visible:outline-2 focus-visible:outline-brand"
            >
              {replyContent('Reply')}
            </button>
          )}
          <ToggleAction
            label="Repost"
            active={comment.retweetedByMe}
            activeClass="text-repost"
            count={comment.retweetCount}
            pending={repost.isPending}
            onClick={() => repost.mutate({ comment, enable: !comment.retweetedByMe })}
          >
            <RepostIcon strokeWidth={comment.retweetedByMe ? 2.6 : 1.8} />
          </ToggleAction>
          <ToggleAction
            label="Like"
            active={comment.likedByMe}
            activeClass="text-like"
            count={comment.likeCount}
            pending={like.isPending}
            onClick={() => like.mutate({ comment, enable: !comment.likedByMe })}
          >
            <HeartIcon filled={comment.likedByMe} />
          </ToggleAction>
          <ToggleAction
            label="Bookmark"
            active={comment.bookmarkedByMe}
            activeClass="text-brand"
            pending={bookmark.isPending}
            onClick={() => bookmark.mutate({ comment, enable: !comment.bookmarkedByMe })}
          >
            <BookmarkIcon filled={comment.bookmarkedByMe} />
          </ToggleAction>
        </div>
        {actionError && (
          <p role="alert" className="mt-2 text-sm text-danger">
            {getErrorMessage(actionError)}
          </p>
        )}

        {mode === 'reply' && (
          <div className="mt-3">
            <TweetForm
              autoFocus
              maxLength={MAX_COMMENT_LENGTH}
              placeholder={`Reply to @${comment.username}`}
              submitLabel="Reply"
              pending={reply.isPending}
              error={reply.error ? getErrorMessage(reply.error) : null}
              onCancel={close}
              onSubmit={(draft) =>
                reply.mutate(buildCommentRequest(draft), { onSuccess: close })
              }
            />
          </div>
        )}
        {mode === 'quote' && (
          <div className="mt-3">
            <QuoteCommentForm comment={comment} onDone={close} />
          </div>
        )}
        {mode === 'delete' && (
          <ConfirmDelete
            what="comment"
            pending={remove.isPending || remove.isSuccess}
            error={remove.error ? getErrorMessage(remove.error) : null}
            onCancel={close}
            onConfirm={() => remove.mutate(comment.id, { onSuccess: () => onDeleted?.() })}
          />
        )}
      </div>
    </article>
  )
}