export function Spinner({ label = 'Loading' }: { label?: string }) {
  return (
    <span role="status" className="inline-flex items-center">
      <span className="size-5 animate-spin rounded-full border-2 border-current border-t-transparent motion-reduce:animate-none" />
      <span className="sr-only">{label}</span>
    </span>
  )
}

export function FullPageSpinner() {
  return (
    <div className="grid min-h-dvh place-items-center text-brand">
      <Spinner />
    </div>
  )
}
//on time proccessing cases, to show a loading spinner while waiting for data to be fetched or processed. It provides a visual indication to the user that something is happening in the background and they should wait.