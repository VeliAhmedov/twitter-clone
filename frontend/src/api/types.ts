// Mirrors of the backend DTOs. Add new ones here as each feature is built.

export type Role = 'USER' | 'ADMIN'

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  role: Role
}

export interface UserResponse {
  id: number
  email: string
  bio: string | null
  username: string
  displayName: string
  avatarURL: string | null
  createdAt: string // ISO-8601 instant
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  displayName: string
  email: string
  bio?: string
}

// Spring's Page, as the backend serializes it. Depending on the Spring Data version the
// "is there more?" info is either `last` or nested under `page`, so both are optional.
export interface Page<T> {
  content: T[]
  last?: boolean
  page?: { number: number; totalPages: number }
}

export interface QuotedTweetResponse {
  id: number
  content: string | null
  imageUrl: string | null
  userId: number
  username: string
  userDisplayName: string
  userAvatarUrl: string | null
  createdAt: string
}

export interface QuotedCommentResponse extends QuotedTweetResponse {
  tweetId: number
}

export interface TweetResponse {
  id: number
  content: string | null
  imageUrl: string | null
  quotedTweet: QuotedTweetResponse | null
  quotedComment: QuotedCommentResponse | null
  userId: number
  username: string
  userDisplayName: string
  userAvatarUrl: string | null
  likeCount: number
  commentCount: number
  retweetCount: number
  likedByMe: boolean
  retweetedByMe: boolean
  bookmarkedByMe: boolean
  edited: boolean
  quoteUnavailable: boolean
  createdAt: string
}

//for posting tweet
export interface TweetRequest {
  content?: string
  image?: string
  quotedTweetId?: number
  quotedCommentId?: number
}

export interface CommentResponse {
  id: number
  tweetId: number
  userId: number
  parentCommentId: number | null
  username: string
  displayName: string
  userAvatarUrl: string | null
  content: string | null
  imageUrl: string | null
  likeCount: number
  replyCount: number
  retweetCount: number
  likedByMe: boolean
  retweetedByMe: boolean
  bookmarkedByMe: boolean
  edited: boolean
  createdAt: string
}

// Note: the backend calls the image field `url` for comments (and `image` for tweets).
export interface CommentRequest {
  content?: string
  imageUrl?: string
}

export type CommentSort = 'newest' | 'relevant'

export interface FollowStats {
  followerCount: number
  followingCount: number
}

export interface FollowUser {
  id: number
  username: string
  displayName: string
  avatarURL: string | null
}

// One entry of GET /api/users/{id}/retweets: either a tweet or a comment was reposted.
export interface RetweetResponse {
  id: number
  repostedAt: string
  tweet: TweetResponse | null
  comment: CommentResponse | null
}

// One entry of GET /api/bookmarks: a tweet or a comment.
export interface BookmarkResponse {
  id: number
  createdAt: string
  tweet: TweetResponse | null
  comment: CommentResponse | null
}