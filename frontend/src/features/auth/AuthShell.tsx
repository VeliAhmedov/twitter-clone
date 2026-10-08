import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'

interface AuthShellProps {
  title: string
  children: ReactNode
  footer: ReactNode
}

export function AuthShell({ title, children, footer }: AuthShellProps) {
  return (
    <div className="min-h-dvh lg:grid lg:grid-cols-[5fr_6fr]">
      <aside className="flex items-center justify-between overflow-hidden bg-brand px-6 py-4 text-white lg:flex-col lg:items-stretch lg:px-12 lg:py-12">
        <p className="hidden max-w-xs text-xl font-medium leading-snug text-balance lg:block">
          Post what's on your mind. See who replies.
        </p>
        <Link
          to="/"
          className="text-3xl font-extrabold tracking-tighter focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-white lg:text-[clamp(4rem,8.5vw,9rem)] lg:leading-none"
        >
          twittvl
        </Link>
      </aside>

      <main className="flex items-center justify-center px-6 py-10 lg:px-16">
        <div className="w-full max-w-sm">
          <h1 className="mb-8 text-3xl font-bold tracking-tight">{title}</h1>
          {children}
          <p className="mt-8 text-sm text-muted">{footer}</p>
        </div>
      </main>
    </div>
  )
}
//Complete reuse layout for login and register pages. 
//It includes a sidebar with a brief description and the application name, 
//and a main section that displays the title, form content, and footer. 
//The layout is responsive and adjusts for larger screens.