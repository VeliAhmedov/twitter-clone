import { api } from '../../api/client'
import { hasNextPage } from '../../api/pagination'
import type { Page, TweetRequest, TweetResponse } from '../../api/types'

/** via shared api Axios, API calls for tweets, including creating, fetching, updating, deleting, and fetching quote pages endpoints. */

export const QUOTES_PAGE_SIZE = 10

export async function createTweet(body: TweetRequest): Promise<TweetResponse> {
  const { data } = await api.post<TweetResponse>('/api/tweets', body) // 201
  return data
}

export async function fetchTweet(id: number): Promise<TweetResponse> {
  const { data } = await api.get<TweetResponse>(`/api/tweets/${id}`)
  return data
}

export async function updateTweet(id: number, body: TweetRequest): Promise<TweetResponse> {
  const { data } = await api.patch<TweetResponse>(`/api/tweets/${id}`, body)
  return data
}

export async function deleteTweet(id: number): Promise<void> {
  await api.delete(`/api/tweets/${id}`) // 204
}

//get page of tweets that quote a given tweet, with pagination support. 
// It returns the tweets and a boolean indicating if there are more pages.
export async function fetchQuotesPage(
  tweetId: number,
  page: number,
): Promise<{ tweets: TweetResponse[]; hasMore: boolean }> {
  const { data } = await api.get<Page<TweetResponse>>(`/api/tweets/${tweetId}/quotes`, {
    params: { page, size: QUOTES_PAGE_SIZE },
  })
  return { tweets: data.content, hasMore: hasNextPage(data, page, QUOTES_PAGE_SIZE) }
}