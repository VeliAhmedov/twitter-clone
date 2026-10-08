import { Link } from 'react-router-dom'

export function NotFoundPage() {
  return (
    <div className="grid min-h-dvh place-items-center px-6 text-center">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Page not found</h1>
        <p className="mt-2 text-muted">That address doesn't lead anywhere.</p>
        <Link to="/" className="mt-6 inline-block font-semibold text-brand hover:underline"> //link to home page
          Back to home
        </Link>
      </div>
    </div>
  )
}
//when a user navigates to a route that doesn't exist, this page will be displayed. 
//It informs the user that the page was not found and provides a link to return to the home page. 
//The layout is centered and responsive.