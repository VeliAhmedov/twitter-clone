import { useMutation, useQueryClient } from '@tanstack/react-query'
import { updateProfile } from './profileApi'
import type { UserResponse } from '../../api/types'
import type { ProfileUpdate } from './validation'

//Handles profile updates and refreshes the relevant frontend state and cached data.

export function useUpdateProfile() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({
      user,
      values,
      avatar,
    }: {
      user: UserResponse
      values: ProfileUpdate
      avatar: File | null
    }) => updateProfile(user, values, avatar),

    onSuccess: (updatedUser) => {
      queryClient.invalidateQueries({ queryKey: ['profile'] })
      queryClient.invalidateQueries({ queryKey: ['users'] })
      queryClient.setQueryData(
        ['profile', updatedUser.username],
        updatedUser,
      )
    },
  })
}