<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const formData = reactive({
  title: '',
  description: '',
  assignees: '',
  statusId: null
})

const router = useRouter()
const BASE_URL = import.meta.env.VITE_BASE_URL

onMounted(async () => {
  await fetchStatuses()
})
const statuses = ref([])

const fetchStatuses = async () => {
  try {
    const response = await fetch(BASE_URL + '/v2/statuses')
    if (response.ok) {
      const data = await response.json()
      statuses.value = data

      // if (statuses.value.length > 0) {
      //   selectedStatus.value = statuses.value[0]
      // }
    } else {
      console.error('Failed to fetch statuses:', response.statusText)
    }
  } catch (error) {
    console.error('Error fetching statuses:', error)
  }
}

console.log(statuses)
fetchStatuses()

const saveTask = async () => {
  if (!formData.statusId) {
    formData.statusId = 1
  }
  const taskData = {
    title: formData.title.trim(),
    description: formData.description.trim(),
    assignees: formData.assignees.trim(),
    statusId: formData.statusId
  }
  // if (formData.selectedStatusId && formData.selectedStatusId.length > 0) {
  //   taskData.status.id = formData.selectedStatusId
  // } else {
  //   taskData.status.id = 1
  // }

  try {
    const response = await fetch(BASE_URL + `/v2/tasks`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(taskData)
    })
    if (response.status === 201) {
      router.push('/task')
      // console.log(taskData)
      // console.log(formData.statusId)
      // Alert
      showAlert('The task has been successfully added.', 'rgb(34 197 94)')
    } else {
      router.push('/task')
      console.error('Failed to save task:', response.statusText)
      showAlert('An error has occurred, the task Cant Add.', 'rgb(251 146 60)')
      console.log(taskData)
      console.log(formData.statusId)
    }
  } catch (error) {
    console.error('Error saving task:', error)
  }
}

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
  title: 100,
  description: 500,
  assignees: 30
}

const checkMaxLength = (field) => {
  if (formData[field].length >= maxLengths[field]) {
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
    class="itbkk-modal-task fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50"
  >
    <div class="bg-blue-100 rounded-lg p-8 max-w-3xl w-full">
      <h2 class="text-rose-400 text-xl font-bold mb-2 text-center text-20 text-black">Add Task</h2>

      <form @submit.prevent="saveTask">
        <div class="mb-6">
          <label for="title" class="text-rose-400 block text-sm font-medium text-gray-700"
            >Title</label
          >
          <input
            type="text"
            id="itbkk-title"
            v-model="formData.title"
            @input="checkMaxLength('title')"
            class="bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            maxlength="100"
          />
        </div>
        <div class="mb-4">
          <label for="description" class="text-rose-400 block text-sm font-medium text-gray-700"
            >Description</label
          >
          <textarea
            id="itbkk-description"
            v-model="formData.description"
            @input="checkMaxLength('description')"
            class="bg-white text-blue-600 mt-1 block h-40 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            maxlength="500"
          ></textarea>
        </div>
        <div class="mb-4">
          <label for="assignees" class="text-rose-400 block text-sm font-medium text-gray-700"
            >Assignees</label
          >
          <input
            type="text"
            id="itbkk-assignees"
            v-model="formData.assignees"
            @input="checkMaxLength('assignees')"
            class="bg-white text-blue-600 mt-1 h-9 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            maxlength="30"
          />
        </div>
        <div class="mb-4">
          <label for="status" class="text-rose-400 block text-sm font-medium text-gray-700"
            >Status</label
          >
          <select
            id="itbkk-status"
            v-model="formData.statusId"
            class="bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option v-for="status in statuses" :value="status.id" :key="status.id">
              {{ status.name }}
            </option>
          </select>
        </div>
        <div class="flex mt-5 justify-center">
          <button
            id="itbkk-button-confirm"
            type="submit"
            :disabled="formData.title.trim().length === 0"
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
              formData.title.trim().length === 0 ? 'bg-gray-400' : 'bg-green-400',
              formData.title.trim().length === 0 ? 'disabled' : ''
            ]"
          >
            Save
          </button>
          <!-- <button
            id="itbkk-button-confirm"
            type="submit"
            :disabled="formData.title.trim().length === 0"
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
              formData.title.trim().length === 0 ? 'bg-gray-400' : 'bg-green-400',
              formData.title.trim().length === 0 ? 'disabled' : ''
            ]"
          >
            Save
          </button> -->

          <button
            id="itbkk-button-cancel"
            type="button"
            @click="() => router.push('/task')"
            class="itbkk-button-cancel bg-red-400 border-4 border-white rounded-3xl mx-5 p-8 px-6 py-2 text-base text-white font-semibold text-center"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
