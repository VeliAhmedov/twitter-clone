import type { ComponentProps } from 'react'
import { Spinner } from './spinner'

interface ButtonProps extends ComponentProps<'button'> {
  variant?: 'primary' | 'quiet'
  loading?: boolean
}

const variants = {
  primary: 'bg-brand text-white hover:bg-brand-dark',
  quiet: 'border border-line bg-white text-ink hover:bg-frost',
}

export function Button({
  variant = 'primary',
  loading = false,
  disabled,
  type = 'button',
  className = '',
  children,
  ...rest
}: ButtonProps) {
  return (
    <button
      type={type}
      disabled={disabled || loading}
      className={`inline-flex items-center justify-center gap-2 rounded-lg px-4 py-2.5 text-sm font-semibold transition-colors focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand disabled:cursor-not-allowed disabled:opacity-60 ${variants[variant]} ${className}`}
      {...rest}
    >
      {loading && <Spinner />}
      {children}
    </button>
  )
}

//reusuble button component that can be used throughout the application. It supports different variants, loading state, and disabled state. The button is styled using Tailwind CSS classes and can be customized with additional class names.