import type { InfiniteData, QueryClient } from '@tanstack/react-query'
import type { CommentResponse, TweetResponse } from '../../api/types'
import type { PostsPage } from './postsApi'

// Profile tabs and the bookmarks page all live under the ['posts'] query key.
function mapPosts(
  queryClient: QueryClient,
  mapItem: (item: PostsPage['items'][number]) => PostsPage['items'][number],
) {
  queryClient.setQueriesData<InfiniteData<PostsPage>>(
    { queryKey: ['posts'] },
    (old) =>
      old && {
        ...old,
        pages: old.pages.map((page) => ({ ...page, items: page.items.map(mapItem) })),
      },
  )
}

export function patchTweetInPosts(
  queryClient: QueryClient,
  tweetId: number,
  patch: (tweet: TweetResponse) => TweetResponse,
) {
  mapPosts(queryClient, (item) =>
    item.tweet?.id === tweetId ? { ...item, tweet: patch(item.tweet) } : item,
  )
}

export function patchCommentInPosts(
  queryClient: QueryClient,
  commentId: number,
  patch: (comment: CommentResponse) => CommentResponse,
) {
  mapPosts(queryClient, (item) =>
    item.comment?.id === commentId ? { ...item, comment: patch(item.comment) } : item,
  )
}