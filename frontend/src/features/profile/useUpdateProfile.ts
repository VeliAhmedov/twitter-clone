import { useMutation, useQueryClient } from '@tanstack/react-query'
import type { UserResponse } from '../../api/types'
import { useAuthStore } from '../auth/authStore'
import { updateProfile } from './profileApi'
import type { ProfileUpdate } from './validation'

//Handles profile updates and refreshes the relevant frontend state and cached data.

export function useUpdateProfile() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ user, values }: { user: UserResponse; values: ProfileUpdate }) =>
      updateProfile(user, values),

    onSuccess: (updated) => {
      queryClient.setQueryData(['profile', updated.username], updated)

      // The header, the composer and the menu read the logged-in user from here.
      if (useAuthStore.getState().user?.id === updated.id) useAuthStore.setState({ user: updated })

      // The name and picture are repeated on every card, so reload whatever is open.
      void queryClient.invalidateQueries({ predicate: (query) => query.queryKey[0] !== 'profile' })
    },
  })
}