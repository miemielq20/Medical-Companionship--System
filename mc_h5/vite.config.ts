import vue from '@vitejs/plugin-vue';
import Components from 'unplugin-vue-components/vite';
import { VantResolver } from 'unplugin-vue-components/resolvers';
import { fileURLToPath, URL } from 'node:url'

export default {

  plugins: [
    vue(),
    Components({
      resolvers: [VantResolver()],
    }),
  ],
   server: {
       port: 5050,
       historyApiFallback: true,
        proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },
    
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      },
    },
};