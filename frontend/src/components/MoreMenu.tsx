import { useEffect, useRef, useState } from 'react'
import { MoreIcon } from './Icons'

/* this component is a reusable dropdown menu that displays a list of actions when the "..." button is clicked.
It uses the useState hook to manage the open/closed state of the menu, and the useRef hook to reference the root element of the component.
The useEffect hook is used to add event listeners for closing the menu when clicking outside or pressing the Escape key. */

export interface MenuItem {
  label: string
  onSelect: () => void
  danger?: boolean
}

/** The "..." button with a small dropdown. Closes on outside click and on Escape. */
export function MoreMenu({ items, label = 'More actions' }: { items: MenuItem[]; label?: string }) {
  const [open, setOpen] = useState(false)
  const rootRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (!open) return

    const closeOnOutsideClick = (event: PointerEvent) => {
      if (!rootRef.current?.contains(event.target as Node)) setOpen(false)
    }
    const closeOnEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape') setOpen(false)
    }
    document.addEventListener('pointerdown', closeOnOutsideClick)
    document.addEventListener('keydown', closeOnEscape)
    return () => {
      document.removeEventListener('pointerdown', closeOnOutsideClick)
      document.removeEventListener('keydown', closeOnEscape)
    }
  }, [open])

  return (
    <div ref={rootRef} className="relative">
      <button
        type="button"
        aria-label={label}
        aria-haspopup="menu"
        aria-expanded={open}
        onClick={() => setOpen((current) => !current)}
        className="rounded-full p-1.5 text-muted hover:bg-frost hover:text-ink focus-visible:outline-2 focus-visible:outline-brand"
      >
        <MoreIcon />
      </button>
      {open && (
        <div
          role="menu"
          className="absolute right-0 top-full z-20 mt-1 min-w-36 rounded-lg border border-line bg-white py-1 shadow-lg"
        >
          {items.map((item) => (
            <button
              key={item.label}
              type="button"
              role="menuitem"
              onClick={() => {
                setOpen(false)
                item.onSelect()
              }}
              className={`block w-full px-3 py-2 text-left text-sm hover:bg-frost ${
                item.danger ? 'text-danger' : 'text-ink'
              }`}
            >
              {item.label}
            </button>
          ))}
        </div>
      )}
    </div>
  )
}