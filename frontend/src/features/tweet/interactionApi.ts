import { api } from '../../api/client'

//backend's automatic like, repost, bookmark count handling more smoothly with React Query's caching mechanism.
//more so on the side of CREATE and GET operations.

/** These functions call the backend to like, repost, or bookmark a tweet. 
They return the new total count of likes or reposts, or undefined if the backend doesn't provide a count. 
*/

// Like and repost return the new total as a plain number.
export async function likeTweet(id: number): Promise<number> {
  const { data } = await api.post<number>(`/api/tweets/${id}/likes`)
  return data
}

export async function unlikeTweet(id: number): Promise<number> {
  const { data } = await api.delete<number>(`/api/tweets/${id}/likes`)
  return data
}

export async function repostTweet(id: number): Promise<number> {
  const { data } = await api.post<number>(`/api/tweets/${id}/retweet`) // 201
  return data
}

// The backend answers 204 here, which has no body, so there may be no count to read.
export async function unrepostTweet(id: number): Promise<number | undefined> {
  const { data } = await api.delete<unknown>(`/api/tweets/${id}/retweet`)
  return typeof data === 'number' ? data : undefined
}

export async function bookmarkTweet(id: number): Promise<undefined> {
  await api.post(`/api/bookmarks/tweet/${id}`) // 201
}

export async function unbookmarkTweet(id: number): Promise<undefined> {
  await api.delete(`/api/bookmarks/tweet/${id}`) // 204
}