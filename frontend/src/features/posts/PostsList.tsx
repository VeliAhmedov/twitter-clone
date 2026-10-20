import { PagedList } from '../../components/PagedList'
import { RepostIcon } from '../../components/Icons'
import { CommentCard } from '../comment/CommentCard'
import { TweetCard } from '../tweet/TweetCard'
import type { PostItem, PostsSource } from './postsApi'
import { usePosts } from './usePosts'

interface PostsListProps {
  source: PostsSource
  /** Name shown in "X reposted" on the Reposts tab. */
  reposterName?: string
  emptyText: string
}

function PostRow({ item, reposterName }: { item: PostItem; reposterName?: string }) {
  const card = item.tweet ? (
    <TweetCard tweet={item.tweet} />
  ) : item.comment ? (
    <CommentCard comment={item.comment} />
  ) : null

  if (!item.reposted) return card

  return (
    <div>
      <p className="flex items-center gap-1.5 px-4 pt-3 text-xs font-semibold text-muted">
        <RepostIcon width={14} height={14} />
        {reposterName ? `${reposterName} reposted` : 'Reposted'}
      </p>
      {card}
    </div>
  )
}

/** A profile tab or the bookmarks page: cards for tweets and comments, with paging. */
export function PostsList({ source, reposterName, emptyText }: PostsListProps) {
  const posts = usePosts(source)

  return (
    <PagedList
      query={posts}
      isEmpty={posts.items.length === 0}
      emptyText={emptyText}
      errorText="Couldn't load posts."
    >
      <div className="divide-y divide-line rounded-xl border border-line bg-white">
        {posts.items.map((item) => (
          <PostRow key={item.key} item={item} reposterName={reposterName} />
        ))}
      </div>
    </PagedList>
  )
}