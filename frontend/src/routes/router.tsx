import { createBrowserRouter } from 'react-router-dom'
import { LoginPage } from '../features/auth/LoginPage'
import { RegisterPage } from '../features/auth/RegisterPage'
import { HomePage } from '../features/feed/HomePage'
import { AppLayout } from './AppLayout'
import { GuestRoute } from './GuestRoute'
import { NotFoundPage } from './NotFoundPage'
import { ProtectedRoute } from './ProtectedRoute'
import { TweetPage } from '../features/tweet/TweetPage'
import { QuotesPage } from '../features/tweet/QuotesPage'
import { CommentPage } from '../features/comment/CommentPage'
import { BookmarksPage } from '../features/bookmark/BookmarksPage'
import { FollowListPage } from '../features/follow/FollowListPage'
import { ProfilePage } from '../features/profile/ProfilePage'

//router define URL paths and their corresponding components. 

export const router = createBrowserRouter([
  {
    element: <GuestRoute />,
    children: [
      { path: '/login', element: <LoginPage /> },        //this route is for the login page, which is only accessible to unauthenticated users.
      { path: '/register', element: <RegisterPage /> },  //this route is for the register page, which is also only accessible to unauthenticated users.
    ],
  },
  {
    element: <ProtectedRoute />, //this means it require authentication to access the routes defined in children.
    children: [
      {
        element: <AppLayout />,
        children: [
          { path: '/', element: <HomePage /> },
          { path: '/tweet/:id', element: <TweetPage /> },
          { path: '/tweet/:id/quotes', element: <QuotesPage /> },
          { path: '/comment/:id', element: <CommentPage /> },
          { path: '/comment/:id', element: <CommentPage /> },
          { path: '/bookmarks', element: <BookmarksPage /> },
          // React Router can't match "/@:username", so the handle is a normal parameter
          // and the page checks that it starts with "@".
          { path: '/:handle', element: <ProfilePage /> },
          { path: '/:handle/:list', element: <FollowListPage /> },
        ], 
      },
    ],
  },
  { path: '*', element: <NotFoundPage /> },
])

//It includes routes for login, register, home page, and a catch-all for not found pages. 
//The GuestRoute and ProtectedRoute components are used to protect certain routes based on authentication status.