import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
        server: {
            host: '127.0.0.1',
            port: 5173,
            proxy: {
                '/api': {
                    target: 'http://100.120.86.63:19010',
                    changeOrigin: true,
                    secure: false,
                },
            },
        },
})
