import { api } from '../../api/client'
import type { UserResponse } from '../../api/types'
import type { ProfileUpdate } from './validation'

//Sends profile changes to the backend and returns the updated user data.

/**
 * PUT /api/users/{id}. We always send all three fields, so it works whether the backend
 * treats the request as a full replace or as "only the fields that are present".
 * profile image hotlink removed and replaced with actual image from local machine
 */
export async function updateProfile(
  user: UserResponse,
  values: ProfileUpdate,
  avatar: File | null,
): Promise<UserResponse> {
  const formData = new FormData()

  formData.append(
    'profile',
    new Blob(
      [
        JSON.stringify({
          displayName: values.displayName,
          bio: values.bio,
        }),
      ],
      { type: 'application/json' },
    ),
  )

  if (avatar) {
    formData.append('avatar', avatar)
  }

  const { data } = await api.put<UserResponse>(
    `/api/users/${user.id}`,
    formData,
  )

  return data
}