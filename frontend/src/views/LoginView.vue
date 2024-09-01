<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

import hideIcon from '/src/assets/ico/bx-hide.svg'
import showIcon from '/src/assets/ico/bx-show.svg'

const showPassword = ref(false)
const username = ref('')
const password = ref('')
const router = useRouter()
const BASE_URL = import.meta.env.VITE_BASE_URL

const isLoginButtonDisabled = computed(() => !username.value || !password.value)

const toggleShowPassword = () => {
  showPassword.value = !showPassword.value
}

async function login() {
  if (!isLoginButtonDisabled.value) {
    const response = await fetch(BASE_URL + '/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ userName: username.value, password: password.value })
    })

    if (response.ok) {
      const { access_token } = await response.json()
      document.cookie = `access_token=${access_token}; path=/; SameSite=None; Secure;`
      router.push({ name: 'tasks' })
    } else if ([400, 401].includes(response.status)) {
      showToast('Username or Password is incorrect.')
    } else {
      showToast('There is a problem. Please try again later.')
    }
  }
}

const showToast = (message) => {
  const existingToast = document.querySelector('.toast')
  if (existingToast) {
    existingToast.parentElement.removeChild(existingToast)
  }

  const toastDiv = document.createElement('div')
  toastDiv.className = 'itbkk-message toast toast-bottom toast-right z-50 m-10'
  const alertDiv = document.createElement('div')
  alertDiv.className = 'alert alert-error'
  alertDiv.innerHTML = `<span>${message}</span>`
  alertDiv.style.color = 'white'
  alertDiv.style.textAlign = 'center'
  alertDiv.style.display = 'flex'
  alertDiv.style.padding = '20px'
  alertDiv.style.margin = '24px'
  alertDiv.style.fontSize = '18px'

  toastDiv.appendChild(alertDiv)
  document.body.appendChild(toastDiv)

  setTimeout(() => {
    if (toastDiv.parentElement) {
      toastDiv.parentElement.removeChild(toastDiv)
    }
  }, 2000)
}
</script>

<template>
  <div
    class="bg-[url('/src/assets/login-bg.jpeg')] bg-cover bg-center bg-no-repeat fixed inset-0 flex items-center justify-center"
  >
    <div class="login-container">
      <h1 class="text-2xl font-extrabold text-slate-900">Welcome To ITB-KK</h1>
      <div class="login-form">
        <form @submit.prevent="login">
          <div class="input-group">
            <input
              class="itbkk-username"
              type="text"
              id="username"
              placeholder="Username"
              v-model="username"
              maxlength="50"
            />
            <img src="/src/assets/ico/bxs-user.svg" alt="User Icon" class="input-icon white" />
          </div>
          <br />
          <div class="input-group relative">
            <input
              :type="showPassword ? 'text' : 'password'"
              class="itbkk-password"
              type="password"
              id="password"
              placeholder="Password"
              v-model="password"
              maxlength="14"
            />
            <button @click="toggleShowPassword" type="button" class="absolute right-9">
              <img :src="showPassword ? hideIcon : showIcon" alt="Toggle Password Visibility" />
            </button>
            <img src="/src/assets/ico/bxs-lock.svg" alt="User Icon" class="input-icon" />
          </div>
          <br />
          <button
            type="submit"
            class="itbkk-button-signin py-3 w-48 rounded-3xl text-slate-100 transition delay-75"
            :class="[
              isLoginButtonDisabled
                ? 'disabled bg-slate-400 hover:bg-slate-400'
                : 'bg-slate-800 hover:bg-slate-700'
            ]"
            :disabled="isLoginButtonDisabled"
          >
            Login
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  text-align: center;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(1.25);
  width: 500px;
  padding: 60px;
  border: 1px solid black;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  background: rgb(255, 255, 255, 0.6);
}

.login-form {
  margin-top: 30px;
}

.input-group {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  color: black;
}

.input-icon {
  width: 20px;
  height: 20px;
  margin-left: 10px;
}

.input-group input {
  flex: 1;
  width: 80%;
  padding: 10px;
  border: 1px solid black;
  background-color: white;
  border-radius: 5px;
}
</style>
