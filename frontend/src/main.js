import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

let token = null

const accessToken = document.cookie.match(/access_token=([^;]*)/)
if (accessToken) {
    token = accessToken[1]
    window.fetch = ((originalFetch) => {
        return (...args) => {
        const headers = new Headers(args[1] && args[1].headers ? args[1].headers : {})
        headers.set('Authorization', `Bearer ${token}`)
        return originalFetch(...args, { headers })
        }
  })(window.fetch)
}

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
