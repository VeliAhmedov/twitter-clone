import { api } from '../../api/client'
import { hasNextPage } from '../../api/pagination'
import type {
  BookmarkResponse,
  CommentResponse,
  Page,
  RetweetResponse,
  TweetResponse,
} from '../../api/types'

/*
 * This module provides functions and types for fetching and managing posts (tweets, comments, reposts, and bookmarks) in a paginated manner.
 * It defines the structure of a post item, the source of posts, and the response format for paginated posts.
 * The `fetchPostsPage` function retrieves a page of posts based on the specified source and page number.
 * The `postsKey` function generates a unique key for caching or identifying a specific list of posts.
*/

export const POSTS_PAGE_SIZE = 20

/** Which list: a profile tab (needs the user's id) or the logged-in user's bookmarks. */
export type PostsSource =
  | { kind: 'tweets' | 'reposts' | 'replies'; userId: number }
  | { kind: 'bookmarks' }

/** One row of a profile tab or of the bookmarks page: a tweet or a comment. */
export interface PostItem {
  key: string
  tweet: TweetResponse | null
  comment: CommentResponse | null
  /** True when the row is there because the profile owner reposted it. */
  reposted: boolean
}

export interface PostsPage {
  items: PostItem[]
  hasMore: boolean
}

export function postsKey(source: PostsSource) {
  return source.kind === 'bookmarks'
    ? (['posts', 'bookmarks'] as const)
    : (['posts', source.kind, source.userId] as const)
}

const params = (page: number) => ({ params: { page, size: POSTS_PAGE_SIZE } })

export async function fetchPostsPage(source: PostsSource, page: number): Promise<PostsPage> {
  switch (source.kind) {
    case 'tweets': {
      const { data } = await api.get<Page<TweetResponse>>(`/api/users/${source.userId}/tweets`, params(page))
      return {
        items: data.content.map((tweet) => ({
          key: `tweet-${tweet.id}`,
          tweet,
          comment: null,
          reposted: false,
        })),
        hasMore: hasNextPage(data, page, POSTS_PAGE_SIZE),
      }
    }
    case 'replies': {
      const { data } = await api.get<Page<CommentResponse>>(`/api/users/${source.userId}/comments`, params(page))
      return {
        items: data.content.map((comment) => ({
          key: `comment-${comment.id}`,
          tweet: null,
          comment,
          reposted: false,
        })),
        hasMore: hasNextPage(data, page, POSTS_PAGE_SIZE),
      }
    }
    case 'reposts': {
      const { data } = await api.get<Page<RetweetResponse>>(`/api/users/${source.userId}/retweets`, params(page))
      return {
        items: data.content
          .filter((repost) => repost.tweet || repost.comment)
          .map((repost) => ({
            key: `repost-${repost.id}`,
            tweet: repost.tweet,
            comment: repost.comment,
            reposted: true,
          })),
        hasMore: hasNextPage(data, page, POSTS_PAGE_SIZE),
      }
    }
    case 'bookmarks': {
      const { data } = await api.get<Page<BookmarkResponse>>('/api/bookmarks', params(page))
      return {
        items: data.content
          .filter((bookmark) => bookmark.tweet || bookmark.comment)
          .map((bookmark) => ({
            key: `bookmark-${bookmark.id}`,
            tweet: bookmark.tweet,
            comment: bookmark.comment,
            reposted: false,
          })),
        hasMore: hasNextPage(data, page, POSTS_PAGE_SIZE),
      }
    }
  }
}