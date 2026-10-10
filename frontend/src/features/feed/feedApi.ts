import { api } from '../../api/client'
import { hasNextPage } from '../../api/pagination'
import type { Page, TweetResponse } from '../../api/types'

//20 tweeys per page
export const FEED_PAGE_SIZE = 20

/** What the feed list renders. */
export interface FeedItem {
  key: string
  tweet: TweetResponse
}

export interface FeedPage {
  items: FeedItem[]
  hasMore: boolean
}

// Old endpoint: every tweet, newest first. When feed v2 is applied (FeedItemResponse),
// this mapping is the only place that has to change.
export async function fetchFeedPage(page: number): Promise<FeedPage> {
  const { data } = await api.get<Page<TweetResponse>>('/api/tweets/feed', {
    params: { page, size: FEED_PAGE_SIZE },
  })
  return {
    items: data.content.map((tweet) => ({ key: `tweet-${tweet.id}`, tweet })),
    hasMore: hasNextPage(data, page, FEED_PAGE_SIZE),
  }
}