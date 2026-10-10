import { useMutation, useQueryClient, type QueryClient } from '@tanstack/react-query'
import type { TweetRequest } from '../../api/types'
import { createTweet, deleteTweet, updateTweet } from './tweetApi'

/*handles changes and refreshes data after creating, updating, or deleting tweets. It uses React Query's 
useMutation hook (TanStack Query) to manage operations that change backend data. also called mutations  */

// Anything that shows tweets may be out of date after a change. Returning the promise keeps
// the mutation "pending" until the lists have reloaded, so buttons stay disabled that long.
function refreshTweetLists(queryClient: QueryClient) {
  return Promise.all([
    queryClient.invalidateQueries({ queryKey: ['feed'] }),
    queryClient.invalidateQueries({ queryKey: ['tweet'] }),
    queryClient.invalidateQueries({ queryKey: ['quotes'] }),
  ])
}

export function useCreateTweet() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (body: TweetRequest) => createTweet(body),
    onSuccess: () => refreshTweetLists(queryClient),
  })
}

export function useUpdateTweet() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, body }: { id: number; body: TweetRequest }) => updateTweet(id, body),
    onSuccess: () => refreshTweetLists(queryClient),
  })
}

export function useDeleteTweet() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => deleteTweet(id),
    onSuccess: (_data, id) => {
      queryClient.removeQueries({ queryKey: ['tweet', id] }) // never refetch a deleted tweet
      return refreshTweetLists(queryClient)
    },
  })
}