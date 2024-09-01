import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)

const originalFetch = window.fetch
window.fetch = async (url, options = {}) => {
  // Get the value of cookie
  const token = document.cookie.match(/access_token=([^;]*)/)?.[1]
  console.log('url: ', url)

  if (!(options.headers instanceof Headers)) {
    options.headers = new Headers(options.headers)
  }

  // Set the Authorization header with the token
  options.headers.set('Authorization', `Bearer ${token}`)

  return originalFetch(url, options)
}

app.mount('#app')
