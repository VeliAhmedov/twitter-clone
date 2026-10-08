import { QueryClient } from '@tanstack/react-query'

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
})
//for fetching data in React and its freshness, caching, and updating the UI when the data changes. 
//It provides a set of hooks and utilities to manage server state in React applications.