const MINUTE = 60_000
const HOUR = 60 * MINUTE
const DAY = 24 * HOUR

/** "now", "5m", "3h", then "Oct 3", and "Oct 3, 2025" for other years. */
//Converts an ISO date received from the backend into a compact label
export function formatTimeAgo(iso: string, now: number = Date.now()): string {
  const then = new Date(iso)
  if (Number.isNaN(then.getTime())) return ''

  const diff = Math.max(0, now - then.getTime())
  if (diff < MINUTE) return 'now'
  if (diff < HOUR) return `${Math.floor(diff / MINUTE)}m`
  if (diff < DAY) return `${Math.floor(diff / HOUR)}h`

  const sameYear = then.getFullYear() === new Date(now).getFullYear()
  return then.toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
    ...(sameYear ? {} : { year: 'numeric' }),
  })
}

/** return Full date and time for the hover tooltip. */
export function formatFullDate(iso: string): string {
  const date = new Date(iso)
  if (Number.isNaN(date.getTime())) return ''
  return date.toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' })
}

const compact = new Intl.NumberFormat(undefined, { notation: 'compact' })

/** 1200 -> "1.2K" , This keeps large like and reply counts compact.*/
export function formatCount(count: number): string {
  return compact.format(count)
}