import { createApp } from 'vue'
import api from './api'
import router from './router'
import './style.css'
import 'vant/lib/index.css'
import App from './App.vue'

const app = createApp(App)
app.config.globalProperties.$api = api

app.use(router)
app.mount('#app')