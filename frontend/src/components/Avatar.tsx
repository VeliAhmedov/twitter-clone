import { useState } from 'react'

/** Reusable UI component that displays a user's avatar. */

//there props needed
interface AvatarProps {
  username: string
  src?: string | null
  size?: 'sm' | 'md'
}

const sizes = { sm: 'size-6 text-xs', md: 'size-11 text-base' }

/** Profile picture, or the first letter of the username when there is none (or it fails to load). */
export function Avatar({ username, src, size = 'md' }: AvatarProps) {
  const [failed, setFailed] = useState(false)
  const shape = `shrink-0 rounded-full ${sizes[size]}`

  if (src && !failed) {
    return (
      <img
        src={src}
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