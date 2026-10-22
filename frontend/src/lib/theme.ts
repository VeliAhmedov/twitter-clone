import { useSyncExternalStore } from 'react'

/*
*small utility that manages night mode for your frontend. 
*It lets the rest of your React app check whether dark mode is active and switch between light and dark mode.
*/

const STORAGE_KEY = 'theme'
const listeners = new Set<() => void>()

function subscribe(listener: () => void) {
  listeners.add(listener)
  return () => {
    listeners.delete(listener)
  }
}

const isDark = () => document.documentElement.classList.contains('dark')

/** Switches night mode on or off and remembers the choice. index.html applies it before first paint. */
export function setDarkMode(dark: boolean) {
  document.documentElement.classList.toggle('dark', dark)
  try {
    localStorage.setItem(STORAGE_KEY, dark ? 'dark' : 'light')
  } catch {
    // Storage can be blocked (private mode). The switch still works until reload.
  }
  listeners.forEach((listener) => listener())
}

/** [isNight, toggle]. Every component using it stays in sync. */
export function useDarkMode() {
  const dark = useSyncExternalStore(subscribe, isDark, () => false)
  return [dark, () => setDarkMode(!dark)] as const
}