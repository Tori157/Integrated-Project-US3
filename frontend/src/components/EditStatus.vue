<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const statuses = ref({ name: '', description: '' })

const BASE_URL = import.meta.env.VITE_BASE_URL
const route = useRoute()
const router = useRouter()

// Fetch status details
async function fetchStatus() {
  try {
    const response = await fetch(BASE_URL + `/v2/statuses/${route.params.id}`)
    if (!response.ok) {
      throw new Error('Failed to fetch status')
    }
    const data = await response.json()

    if (data.name === 'No Status') {
      console.error('Cannot edit status named No Status.')
      showAlert('This status is the default status and cannot be modified.', 'rgb(251 146 60)')
      router.push('/statuslist')
      return
    }

    statuses.value = data
    originalStatus.value = { ...data }
  } catch (error) {
    console.error('Error fetching status:', error)
    router.push('/editstatuserror')
  }
}

// Save changes to task
async function saveChanges() {
  try {
    // statuses.value.name = statuses.value.name.trim()
    // statuses.value.description = statuses.value.description.trim()

    const response = await fetch(BASE_URL + `/v2/statuses/${route.params.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(statuses.value)
    })

    if (response.ok) {
      router.push('/statuslist')
      console.log('status updated successfully')
      console.log(statuses.value)
      // Alert
      showAlert('The task has been Add.', 'rgb(34 197 94)')
    }
    if (response.status === 404) {
      console.log('The task does not exist.')
      console.error('Failed to update task')

      // alert
      showAlert('An error has occurred, the status does not exist.', 'rgb(251 146 60)')
    }
  } catch (error) {
    console.error('Error updating task:', error)
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

onMounted(fetchStatus)

const originalStatus = ref({ name: '', description: '' })
const isModified = computed(() => {
  if (!statuses.value) return false // If status is not defined, consider it not modified
  return (
    statuses.value.name !== originalStatus.value.name ||
    statuses.value.description !== originalStatus.value.description
  )
})
</script>

<template>
  <div class="fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50">
    <div class="bg-blue-100 rounded-lg p-8 max-w-3xl w-full">
      <h2 class="text-rose-400 text-xl font-bold mb-2 text-center text-20 text-black">
        Edit Status
      </h2>

      <form @submit.prevent="">
        <div class="itbkk-item mb-6">
          <label for="status-name" class="text-rose-400 block text-sm font-medium text-gray-700"
            >Name</label
          >
          <input
            type="text"
            id="itbkk-status-name"
            v-model="statuses.name"
            class="bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
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
            id="itbkk-status-description"
            v-model="statuses.description"
            class="bg-white text-blue-600 mt-1 block h-40 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            maxlength="200"
          ></textarea>
        </div>

        <div class="flex mt-5 justify-center">
          <button
            id="itbkk-button-edit"
            :disabled="!isModified || statuses.name.trim().length === 0"
            @click="saveChanges"
            class="itbkk-button-edit btn text-white border-white mr-6 bg-green-500 hover:bg-green-600 border-4 hover:border-green-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base font-semibold text-center ml-16"
            :class="
              (!isModified || statuses.name.trim().length === 0 ? 'bg-gray-400' : 'bg-green-400',
              !isModified || statuses.name.trim().length === 0 ? 'text-white' : '',
              !isModified || statuses.name.trim().length === 0 ? 'border-white' : '',
              !isModified || statuses.name.trim().length === 0 ? 'disabled' : '')
            "
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
