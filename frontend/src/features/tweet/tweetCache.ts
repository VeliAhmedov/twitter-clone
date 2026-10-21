import type { InfiniteData, QueryClient } from '@tanstack/react-query'
import type { TweetResponse } from '../../api/types'
import type { FeedPage } from '../feed/feedApi'
import { patchTweetInPosts } from '../posts/postsCache'

type QuotesData = InfiniteData<{ tweets: TweetResponse[]; hasMore: boolean }>

//very important part that utilizes React Query's caching mechanism alongside backend 
//Redis all components that display a tweet (feed, tweet page, quotes list) will use the same cached data for that tweet.

/**
 * Changes one tweet everywhere it is cached (feed, tweet page, quotes list), so a like
 * shows up instantly in every place the tweet appears without reloading anything.
 */
export function patchTweetInCaches(
  queryClient: QueryClient,
  tweetId: number,
  patch: (tweet: TweetResponse) => TweetResponse,
): void {
  queryClient.setQueryData<TweetResponse>(['tweet', tweetId], (old) => (old ? patch(old) : old))
  patchTweetInPosts(queryClient, tweetId, patch)

  queryClient.setQueriesData<InfiniteData<FeedPage>>(
    { queryKey: ['feed'] },
    (old) =>
      old && {
        ...old,
        pages: old.pages.map((page) => ({
          ...page,
          items: page.items.map((item) =>
            item.tweet.id === tweetId ? { ...item, tweet: patch(item.tweet) } : item,
          ),
        })),
      },
  )

  queryClient.setQueriesData<QuotesData>(
    { queryKey: ['quotes'] },
    (old) =>
      old && {
        ...old,
        pages: old.pages.map((page) => ({
          ...page,
          tweets: page.tweets.map((tweet) => (tweet.id === tweetId ? patch(tweet) : tweet)),
        })),
      },
  )
}

/** Stops in-flight reloads from overwriting an optimistic change. */
export function cancelTweetQueries(queryClient: QueryClient) {
  return Promise.all([
    queryClient.cancelQueries({ queryKey: ['feed'] }),
    queryClient.cancelQueries({ queryKey: ['tweet'] }),
    queryClient.cancelQueries({ queryKey: ['quotes'] }),
    queryClient.cancelQueries({ queryKey: ['posts'] }),
  ])
}