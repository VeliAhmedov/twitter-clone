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