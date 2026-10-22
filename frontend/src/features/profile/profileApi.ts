import { api } from '../../api/client'
import type { UserResponse } from '../../api/types'
import type { ProfileUpdate } from './validation'

//Sends profile changes to the backend and returns the updated user data.

/**
 * PUT /api/users/{id}. We always send all three fields, so it works whether the backend
 * treats the request as a full replace or as "only the fields that are present".
 */
export async function updateProfile(user: UserResponse, values: ProfileUpdate): Promise<UserResponse> {
  const { data } = await api.put<Partial<UserResponse> | ''>(`/api/users/${user.id}`, values)
  return {
    ...user,
    displayName: values.displayName,
    bio: values.bio || null,
    avatarURL: values.avatarURL || null,
    ...(data && typeof data === 'object' ? data : {}), // the server's answer wins when it sends one
  }
}