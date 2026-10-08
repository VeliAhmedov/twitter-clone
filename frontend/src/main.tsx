import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { QueryClientProvider } from '@tanstack/react-query'
import './index.css'
import App from './App.tsx'
import { queryClient } from './api/queryClient'
import { useAuthStore } from './features/auth/authStore'

// Restore the login before the first render. Called outside React on purpose, so
// StrictMode's double-mount cannot fire two refreshes.
void useAuthStore.getState().bootstrap()

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
*/