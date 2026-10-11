import { useMutation, useQueryClient } from '@tanstack/react-query'
import type { TweetResponse } from '../../api/types'
import {
  bookmarkTweet,
  likeTweet,
  repostTweet,
  unbookmarkTweet,
  unlikeTweet,
  unrepostTweet,
} from './interactionApi'
import { cancelTweetQueries, patchTweetInCaches } from './tweetCache'

/* while interactionApi.ts handles the backend calls for liking, reposting, and bookmarking tweets, 
this file provides React Query hooks that manage the optimistic UI updates and caching for these interactions.
The hooks use the useMutation hook from React Query to perform the backend operations and update the local cache 
immediately for a responsive user experience. If the backend operation fails, the changes are reverted to maintain consistency. 
*/

type Flag = 'likedByMe' | 'retweetedByMe' | 'bookmarkedByMe'
type CountField = 'likeCount' | 'retweetCount'

interface ToggleConfig {
  flag: Flag
  count?: CountField
  on: (id: number) => Promise<number | undefined>
  off: (id: number) => Promise<number | undefined>
}

function setField(tweet: TweetResponse, field: Flag | CountField, value: boolean | number): TweetResponse {
  return Object.assign({}, tweet, { [field]: value })
}

// Optimistic toggle: the button changes at once, the request runs in the background,
// and the change is undone if the request fails.
function useToggleTweet(config: ToggleConfig) {
  const queryClient = useQueryClient()
  const { flag, count } = config

  return useMutation({
    mutationFn: ({ tweet, enable }: { tweet: TweetResponse; enable: boolean }) =>
      enable ? config.on(tweet.id) : config.off(tweet.id),

    onMutate: async ({ tweet, enable }) => {
      await cancelTweetQueries(queryClient)
      patchTweetInCaches(queryClient, tweet.id, (current) => {
        const next = setField(current, flag, enable)
        return count
          ? setField(next, count, Math.max(0, (current[count] ?? 0) + (enable ? 1 : -1)))
          : next
      })
      return { flag: tweet[flag], count: count ? tweet[count] : undefined }
    },

    onError: (_error, { tweet }, previous) => {
      if (!previous) return
      patchTweetInCaches(queryClient, tweet.id, (current) => {
        const restored = setField(current, flag, previous.flag)
        return count && previous.count !== undefined
          ? setField(restored, count, previous.count)
          : restored
      })
    },

    // Prefer the server's own total over our +1/-1 guess when it sends one.
    onSuccess: (serverCount, { tweet }) => {
      if (count && typeof serverCount === 'number') {
        patchTweetInCaches(queryClient, tweet.id, (current) => setField(current, count, serverCount))
      }
    },
  })
}

const likeConfig: ToggleConfig = {
  flag: 'likedByMe',
  count: 'likeCount',
  on: likeTweet,
  off: unlikeTweet,
}
const repostConfig: ToggleConfig = {
  flag: 'retweetedByMe',
  count: 'retweetCount',
  on: repostTweet,
  off: unrepostTweet,
}
const bookmarkConfig: ToggleConfig = {
  flag: 'bookmarkedByMe',
  on: bookmarkTweet,
  off: unbookmarkTweet,
}

export const useLikeTweet = () => useToggleTweet(likeConfig)
export const useRepostTweet = () => useToggleTweet(repostConfig)
export const useBookmarkTweet = () => useToggleTweet(bookmarkConfig)