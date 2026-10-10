import { ComposeBox } from '../tweet/ComposeBox'
import { Feed } from './Feed'

export function HomePage() {
  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold tracking-tight">Home</h1>
      <ComposeBox />
      <Feed />
    </div>
  )
}