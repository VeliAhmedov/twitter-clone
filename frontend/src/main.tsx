import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { QueryClientProvider } from '@tanstack/react-query'
import './index.css'
import App from './App.tsx'
import { queryClient } from './api/queryClient'
import { useAuthStore } from './features/auth/authStore'

/* this is entry point to react, it is like config file to react, provide react querry for app*/
void useAuthStore.getState().bootstrap() /*Before React starts rendering, check whether the user already has a saved login session.*/

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
    </QueryClientProvider>
  </StrictMode>,
)
/*
                    React
                      │
                    main.tsx
                      │
                    App.tsx
                      │
                    Router
              ┌───────┴────────┐
              │                │
         GuestRoute       ProtectedRoute
              │                │
       Login/Register       AppLayout
                               │
                           HomePage
                               │
                              API
                               │
                         Axios client
                               │
                    ┌──────────┴──────────┐
                    │                     │
              Access Token          Refresh Token
                 memory               localStorage
                    │                     │
                    └──────────┬──────────┘
                               │
                         Spring Boot
                               │
                 ┌─────────────┼─────────────┐
                 │             │             │
              Security       Services       Redis
                 │             │             │
                 └──────────── Database ─────┘

    Authentication
          +
JWT access/refresh tokens
          +
       Routing
          +
      Protected pages
          +
    Reusable components
          +
        Axios
          +
      React Query
          +
       Zustand
          +
       Tailwind
          +
     Spring Boot API              
 */