import react from '@vitejs/plugin-react'       //lets vite work with react
import tailwindcss from '@tailwindcss/vite'    //lets vite work with tailwindcss
import { defineConfig } from 'vite'            //vite itself

// https://vite.dev/config/
export default defineConfig({                  //vite configuration "When Vite runs, use the React plugin and Tailwind plugin"
  plugins: [react(), tailwindcss()],
})
