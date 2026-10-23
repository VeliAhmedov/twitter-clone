import { useState } from 'react'
import { API_URL } from '../api/config'

/** Reusable UI component that displays a user's avatar. */

//there props needed, exclude size, because it has default value and part of component, so it is optional
interface AvatarProps {
  username: string
  src?: string | null
  size?: 'sm' | 'base' | 'md' | 'xl'
}
//size of avatar, sm=small, base=medium, md=large
const sizes = { sm: 'size-6 text-xs', base: 'size-9 text-sm', md: 'size-11 text-base', xl: 'size-20 text-3xl' }

/** Profile picture, or the first letter of the username when there is none (or it fails to load). */
export function Avatar({ username, src, size = 'md' }: AvatarProps) {
  const [failed, setFailed] = useState(false)
  const shape = `shrink-0 rounded-full ${sizes[size]}`
  const imageSrc = src?.startsWith('/') ? `${API_URL}${src}` : src

  if (imageSrc && !failed) {
    return (
      <img
        src={imageSrc}
        alt=""
        loading="lazy"
        referrerPolicy="no-referrer"
        onError={() => setFailed(true)}
        className={`${shape} object-cover`}
      />
    )
  }

  return (
    <span
      aria-hidden="true"
      className={`${shape} grid place-items-center bg-brand/10 font-bold uppercase text-brand`}
    >
      {username.charAt(0)}
    </span>
  )
}