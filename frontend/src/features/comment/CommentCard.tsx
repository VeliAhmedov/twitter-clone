import { useState } from 'react'
import { getErrorMessage } from '../../api/errors'
import type { CommentResponse } from '../../api/types'
import { Avatar } from '../../components/Avatar'
import { Button } from '../../components/Button'
import { ConfirmDelete } from '../../components/ConfirmDelete'
import { HeartIcon } from '../../components/Icons'
import { MoreMenu, type MenuItem } from '../../components/MoreMenu'
import { useAuthStore } from '../auth/authStore'
import { buildCommentEditRequest, buildCommentRequest, MAX_COMMENT_LENGTH } from '../tweet/draft'
import { PostImage, PostTime } from '../tweet/PostParts'
import { ToggleAction } from '../tweet/TweetActionBar'
import { TweetForm } from '../tweet/TweetForm'
import {
  useCreateReply,
  useDeleteComment,
  useLikeComment,
  useUpdateComment,
} from './commentMutations'
import { useReplies } from './useComments'

/*Main comment component, which displays a single comment and its actions. 
It supports viewing, replying, editing, and deleting comments, as well as showing replies 
in a nested manner. The component uses React Query hooks for data fetching and mutations.
*/

type Mode = 'view' | 'reply' | 'edit' | 'delete'

// Replies are loaded only when opened. A reply can have replies of its own, so this and
// CommentCard call each other. Indentation stops after a few levels to keep it readable.
function Replies({ commentId, depth }: { commentId: number; depth: number }) {
  const replies = useReplies(commentId, true)

  if (replies.status === 'pending') {
    return <p className="py-2 text-sm text-muted">Loading replies…</p>
  }
  if (replies.status === 'error') {
    return (
      <p className="py-2 text-sm text-danger">
        {getErrorMessage(replies.error, "Couldn't load replies.")}{' '}
        <button type="button" className="font-semibold underline" onClick={() => void replies.refetch()}>
          Try again
        </button>
      </p>
    )
  }

  return (
    <div className={depth <= 2 ? 'ml-5 border-l border-line pl-3' : ''}>
      {replies.comments.map((reply) => (
        <CommentCard key={reply.id} comment={reply} depth={depth} />
      ))}
      {replies.hasNextPage && (
        <Button
          variant="quiet"
          loading={replies.isFetchingNextPage}
          onClick={() => void replies.fetchNextPage()}
        >
          Show more replies
        </Button>
      )}
    </div>
  )
}

export function CommentCard({ comment, depth = 0 }: { comment: CommentResponse; depth?: number }) {
  const currentUserId = useAuthStore((state) => state.user?.id)
  const isOwner = currentUserId === comment.userId
  const [mode, setMode] = useState<Mode>('view')
  const [showReplies, setShowReplies] = useState(false)

  const like = useLikeComment()
  const reply = useCreateReply(comment.id)
  const update = useUpdateComment()
  const remove = useDeleteComment()
  const close = () => setMode('view')

  const menuItems: MenuItem[] = [
    { label: 'Edit', onSelect: () => setMode('edit') },
    { label: 'Delete', danger: true, onSelect: () => setMode('delete') },
  ]
  const replyLabel = comment.replyCount === 1 ? '1 reply' : `${comment.replyCount} replies`

  return (
    <div className="py-3">
      <div className="flex gap-3">
        <Avatar username={comment.username} src={comment.userAvatarUrl} size="base" />

        <div className="min-w-0 flex-1">
          <div className="flex items-center gap-1.5 text-sm">
            <span className="min-w-0 truncate font-semibold">
              {comment.displayName || comment.username}
            </span>
            <span className="min-w-0 truncate text-muted">@{comment.username}</span>
            <span className="shrink-0 text-muted">
              · <PostTime iso={comment.createdAt} />
              {comment.edited && ' · edited'}
            </span>
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
              {comment.imageUrl && <PostImage src={comment.imageUrl} className="mt-2 max-h-72" />}
            </>
          )}

          <div className="mt-2 flex items-center gap-5 text-sm text-muted">
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
            <button
              type="button"
              onClick={() => setMode(mode === 'reply' ? 'view' : 'reply')}
              className="rounded px-1 font-medium hover:text-ink focus-visible:outline-2 focus-visible:outline-brand"
            >
              Reply
            </button>
            {comment.replyCount > 0 && (
              <button
                type="button"
                aria-expanded={showReplies}
                onClick={() => setShowReplies((current) => !current)}
                className="rounded px-1 font-medium hover:text-ink focus-visible:outline-2 focus-visible:outline-brand"
              >
                {showReplies ? 'Hide replies' : `View ${replyLabel}`}
              </button>
            )}
            {isOwner && (
              <div className="ml-auto">
                <MoreMenu items={menuItems} label="Comment actions" />
              </div>
            )}
          </div>
          {like.error && (
            <p role="alert" className="mt-1 text-sm text-danger">
              {getErrorMessage(like.error)}
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
                  reply.mutate(buildCommentRequest(draft), {
                    onSuccess: () => {
                      close()
                      setShowReplies(true)
                    },
                  })
                }
              />
            </div>
          )}
          {mode === 'delete' && (
            <ConfirmDelete
              what="comment"
              pending={remove.isPending || remove.isSuccess}
              error={remove.error ? getErrorMessage(remove.error) : null}
              onCancel={close}
              onConfirm={() => remove.mutate(comment.id)}
            />
          )}

          {showReplies && <Replies commentId={comment.id} depth={depth + 1} />}
        </div>
      </div>
    </div>
  )
}