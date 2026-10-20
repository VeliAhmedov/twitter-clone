import { PostsList } from '../posts/PostsList'

// This page shows the logged-in user's bookmarks, which are tweets and comments they have saved for later.

export function BookmarksPage() {
  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold tracking-tight">Bookmarks</h1>
      <PostsList
        source={{ kind: 'bookmarks' }}
        emptyText="No bookmarks yet. Tap the bookmark icon on a post to save it here."
      />
    </div>
  )
}