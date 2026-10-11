import { useState } from 'react'
import { formatFullDate, formatTimeAgo } from '../../lib/format'

/*This file contains two components: PostTime and PostImage.
PostTime: displays a timestamp in a human-readable format, showing how long ago the post was made, and provides the full date as a tooltip.
PostImage: displays an image attached to a post, handling cases where the image fails to load by hiding it instead of showing a broken icon. 
*/

export function PostTime({ iso }: { iso: string }) {
  return (
    <time dateTime={iso} title={formatFullDate(iso)}>
      {formatTimeAgo(iso)}
    </time>
  )
}

// Image URLs are typed in by users, so some will be broken: hide those instead of showing a broken icon.
export function PostImage({ src, className = '' }: { src: string; className?: string }) {
  const [failed, setFailed] = useState(false)
  if (failed) return null
  return (
    <img
      src={src}
      alt="Attached to post"
      loading="lazy"
      referrerPolicy="no-referrer"
      onError={() => setFailed(true)}
      className={`w-full rounded-xl border border-line object-cover ${className}`}
    />
  )
}