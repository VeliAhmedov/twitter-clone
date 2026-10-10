import { TweetCard } from '../tweet/TweetCard'
import type { FeedItem } from './feedApi'

/** One card per item. Knows nothing about loading or paging. Render list of tweets. */
export function FeedList({ items }: { items: FeedItem[] }) {
  return (
    <div className="divide-y divide-line overflow-hidden rounded-xl border border-line bg-white">
      {items.map((item) => (
        <TweetCard key={item.key} tweet={item.tweet} />
      ))}
    </div>
  )
}