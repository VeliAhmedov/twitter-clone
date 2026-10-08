import { RouterProvider } from 'react-router-dom'
import { router } from './routes/router'

/*Start the application's routing system*/

export default function App() {
  return <RouterProvider router={router} />
}
/*for example:
/login        → LoginPage
/register     → RegisterPage
/             → HomePage
anything else → NotFoundPage
*/