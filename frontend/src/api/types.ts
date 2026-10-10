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
  userAvatarUrl: string | null
  likeCount: number
  commentCount: number
  edited: boolean
  quoteUnavailable: boolean
  createdAt: string
}