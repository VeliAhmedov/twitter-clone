import { useId, type ComponentProps } from 'react'

interface TextFieldProps extends Omit<ComponentProps<'input'>, 'id'> {
  label: string
  error?: string
  hint?: string
}

export function TextField({ label, error, hint, className = '', ...inputProps }: TextFieldProps) {
  const id = useId()
  const messageId = `${id}-message`
  const message = error ?? hint

  return (
    <div className="space-y-1.5">
      <label htmlFor={id} className="block text-sm font-medium">
        {label}
      </label>
      <input
        id={id}
        aria-invalid={error ? true : undefined}
        aria-describedby={message ? messageId : undefined}
        className={`block w-full rounded-lg border bg-white px-3 py-2.5 text-base outline-none placeholder:text-muted/60 focus:border-brand focus:ring-2 focus:ring-brand/25 ${
          error ? 'border-danger' : 'border-line'
        } ${className}`}
        {...inputProps}
      />
      {message && (
        <p id={messageId} className={`text-sm ${error ? 'text-danger' : 'text-muted'}`}>
          {message}
        </p>
      )}
    </div>
  )
}

//reusable text input component that can be used throughout the application. 
//It supports label, error message, and hint message. 
//The input is styled using Tailwind CSS classes and can be customized with additional class names.