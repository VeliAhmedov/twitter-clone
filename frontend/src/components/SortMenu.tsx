import { useEffect, useRef, useState } from 'react'
import type { CommentSort } from '../api/types'

/*
 * This component renders a dropdown menu for sorting comments. 
 * It displays the current sort option and allows the user to select a different sort option from a list. 
 * The menu closes when the user clicks outside of it or presses the Escape key.
 * Props:
 * - value: The current sort option (of type CommentSort).
 * - onChange: A callback function that is called when the user selects a different sort option. 
 * It receives the new sort option as an argument.
 */

const LABELS: Record<CommentSort, string> = { newest: 'Newest', relevant: 'Relevant' }

/** "Relevant ▾" style dropdown. Closes on outside click and on Escape. */
export function SortMenu({ value, onChange }: { value: CommentSort; onChange: (sort: CommentSort) => void }) {
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
        aria-haspopup="menu"
        aria-expanded={open}
        onClick={() => setOpen((current) => !current)}
        className="rounded px-1 text-sm font-semibold text-muted hover:text-ink focus-visible:outline-2 focus-visible:outline-brand"
      >
        <span className="sr-only">Sort replies by </span>
        {LABELS[value]} ▾
      </button>
      {open && (
        <div
          role="menu"
          className="absolute right-0 top-full z-20 mt-1 min-w-32 rounded-lg border border-line bg-white py-1 shadow-lg"
        >
          {(Object.keys(LABELS) as CommentSort[]).map((sort) => (
            <button
              key={sort}
              type="button"
              role="menuitemradio"
              aria-checked={sort === value}
              onClick={() => {
                setOpen(false)
                onChange(sort)
              }}
              className={`block w-full px-3 py-2 text-left text-sm hover:bg-frost ${
                sort === value ? 'font-semibold text-brand' : 'text-ink'
              }`}
            >
              {LABELS[sort]}
            </button>
          ))}
        </div>
      )}
    </div>
  )
}