import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import svgLoaderImport from 'vite-plugin-svg-loader'
const svgLoader = svgLoaderImport.default

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    svgLoader()
  ],
  server: {
    host: '0.0.0.0', // so it works in Docker
    watch: {
      usePolling: true, // fixes file change detection in bind mounts
      interval: 100,    // lower = faster reload, higher = less CPU usage
    }
  }
})

