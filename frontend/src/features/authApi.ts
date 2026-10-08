import { api } from '../../api/client'
import type { AuthResponse, LoginRequest, RegisterRequest, UserResponse } from '../../api/types'

export async function loginRequest(body: LoginRequest): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>('/api/auth/login', body)
  return data
}

// Register returns the new user (201) but no tokens, so the caller logs in afterwards.
export async function registerRequest(body: RegisterRequest): Promise<UserResponse> {
  const { data } = await api.post<UserResponse>('/api/auth/register', body)
  return data
}

export async function logoutRequest(refreshToken: string): Promise<void> {
  await api.post('/api/auth/logout', { refreshToken })
}

// The backend has no "current user" endpoint, so we resolve the profile by username.
export async function fetchUserByUsername(username: string): Promise<UserResponse> {
  const { data } = await api.get<UserResponse>(`/api/users/${encodeURIComponent(username)}`)
  return data
}