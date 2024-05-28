<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const name = ref('')
const description = ref('')
const router = useRouter()
const BASE_URL = import.meta.env.VITE_BASE_URL

const saveStatus = async () => {
  const statusData = {
    name: name.value.trim(),
    description: description.value.trim()
  }
  try {
    const response = await fetch(BASE_URL + `/v2/statuses`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(statusData)
    })
    if (response.status === 201) {
      router.push('/statuslist')
      // Alert
      showAlert('The status has been added', 'rgb(34 197 94)')
    } else {
      console.error('Failed to save Status:', response.statusText)
      router.push('/statuslist')
      showAlert('Status name must be uniques, please choose another name.', 'rgb(251 146 60)')
    }
  } catch (error) {
    console.error('Error saving task:', error)
    router.push('/statuslist')
    showAlert('An error has occurred, Status Cant Add.', 'rgb(251 146 60)')
  }
}

// Show alert message
function showAlert(message, backgroundColor) {
  const toastDiv = document.createElement('div')
  toastDiv.className = 'toast toast-top toast-center z-50'
  const alertDiv = document.createElement('div')
  alertDiv.className = 'alert alert-success'
  alertDiv.innerHTML = `<span>${message}</span>`
  alertDiv.style.backgroundColor = backgroundColor
  alertDiv.style.color = 'white'
  alertDiv.style.textAlign = 'center'
  alertDiv.style.display = 'flex'

  toastDiv.appendChild(alertDiv)
  document.body.appendChild(toastDiv)

  setTimeout(() => {
    document.body.removeChild(toastDiv)
    window.location.reload()
  }, 2000)
}

const maxLengths = {
  name: 50,
  description: 200
}

const checkMaxLength = (field, value) => {
  if (value.length >= maxLengths[field]) {
    showAlert2(`Your text in ${field} is at maximum length`, 'rgb(251 146 60)')
  }
}

function showAlert2(message, backgroundColor) {
  const existingAlert = document.querySelector('.alert-success')
  if (existingAlert) {
    existingAlert.parentElement.removeChild(existingAlert)
  }

  const toastDiv = document.createElement('div')
  toastDiv.className = 'toast toast-top toast-center z-50'
  const alertDiv = document.createElement('div')
  alertDiv.className = 'alert alert-success'
  alertDiv.innerHTML = `<span>${message}</span>`
  alertDiv.style.backgroundColor = backgroundColor
  alertDiv.style.color = 'white'
  alertDiv.style.textAlign = 'center'
  alertDiv.style.display = 'flex'

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
    class="itbkk-modal-status fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50"
  >
    <div class="bg-blue-100 rounded-lg p-8 max-w-3xl w-full">
      <h2 class="text-rose-400 text-xl font-bold mb-2 text-center text-20 text-black">
        Add Status
      </h2>

      <form @submit.prevent="saveStatus">
        <div class="mb-6">
          <label for="status-name" class="text-rose-400 block text-sm font-medium text-gray-700"
            >Name</label
          >
          <input
            type="text"
            id="itbkk-status-name"
            v-model="name"
            class="itbkk-status-name bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            @input="checkMaxLength('name', $event.target.value)"
            maxlength="50"
          />
        </div>
        <div class="mb-4">
          <label
            for="status-description"
            class="text-rose-400 block text-sm font-medium text-gray-700"
            >Description</label
          >
          <textarea
            id="status-description"
            v-model="description"
            class="itbkk-status-description bg-white text-blue-600 mt-1 block h-40 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            @input="checkMaxLength('description', $event.target.value)"
            maxlength="200"
          ></textarea>
        </div>
        <div class="flex mt-5 justify-center">
          <button
            id="itbkk-button-confirm"
            type="submit"
            :disabled="name.trim().length === 0"
            @click="toggleModal"
            class="itbkk-button-confirm"
            :class="[
              'border-4',
              'border-white',
              'rounded-3xl',
              'mx-5',
              'p-8',
              'px-7',
              'py-2',
              'text-base',
              'text-white',
              'font-semibold',
              'text-center',
              name.trim().length === 0 ? 'bg-gray-400' : 'bg-green-400',
              name.trim().length === 0 ? 'disabled' : ''
            ]"
          >
            Save
          </button>

          <button
            id="itbkk-button-cancel"
            type="button"
            @click="() => router.push('/statuslist')"
            class="itbkk-button-cancel bg-red-400 border-4 border-white rounded-3xl mx-5 p-8 px-6 py-2 text-base text-white font-semibold text-center"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
