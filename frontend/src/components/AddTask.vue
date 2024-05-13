<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

import { onMounted } from 'vue'

const formData = reactive({
  title: '',
  description: '',
  assignees: '',
  selectedStatus: {}
})

const router = useRouter()
const SERVER_URL = import.meta.env.VITE_SERVER_URL

onMounted(async () => {
  await fetchStatuses()
})
const statuses = ref([])
const selectedStatus = ref({})

const fetchStatuses = async () => {
  try {
    const response = await fetch(SERVER_URL + '/v2/statuses')
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
  const taskData = {
    title: formData.title.trim(),
    description: formData.description.trim(),
    assignees: formData.assignees.trim(),
    status: {
      id: formData.selectedStatus[0],
      name: formData.selectedStatus[1],
      description: formData.selectedStatus[2]
    }
  }
  if (formData.selectedStatus && formData.selectedStatus.length > 0) {
    taskData.status.id = formData.selectedStatus[0]
    taskData.status.name = formData.selectedStatus[1]
    taskData.status.description = formData.selectedStatus[2]
  } else {
    taskData.status.id = 1
    taskData.status.name = 'No Status'
    taskData.status.description = 'The default status'
  }

  try {
    const response = await fetch(SERVER_URL + `/v2/tasks`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(taskData)
    })
    if (response.status === 201) {
      router.push('/task')
      // console.log(taskData)
      console.log(formData.selectedStatus)
      // Alert
      const toastDiv = document.createElement('div')
      toastDiv.className = 'toast toast-top toast-center z-50'
      const alertSuccessDiv = document.createElement('div')
      alertSuccessDiv.className = 'alert alert-success'
      alertSuccessDiv.innerHTML = '<span>The task has been successfully added.</span>'
      alertSuccessDiv.style.backgroundColor = 'rgb(34 197 94)' // สีพื้นหลัง
      alertSuccessDiv.style.color = 'white' // สีข้อความ
      alertSuccessDiv.style.textAlign = 'center' // ตรงกลาง
      alertSuccessDiv.style.display = 'flex' // ให้เนื้อหาอยู่ตรงกลาง

      toastDiv.appendChild(alertSuccessDiv)
      document.body.appendChild(toastDiv)

      setTimeout(function () {
        document.body.removeChild(toastDiv)
        window.location.reload()
      }, 2000)
    } else {
      console.error('Failed to save task:', response.statusText)
      console.log(taskData)
      console.log(formData.selectedStatus)
      console.log(selectedStatus)
    }
  } catch (error) {
    console.error('Error saving task:', error)
  }
}
</script>

<template>
  <div class="fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50">
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
            class="bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          />
        </div>
        <div class="mb-4">
          <label for="description" class="text-rose-400 block text-sm font-medium text-gray-700"
            >Description</label
          >
          <textarea
            id="itbkk-description"
            v-model="formData.description"
            class="bg-white text-blue-600 mt-1 block h-40 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
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
            class="bg-white text-blue-600 mt-1 h-9 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          />
        </div>
        <div class="mb-4">
          <label for="status" class="text-rose-400 block text-sm font-medium text-gray-700"
            >Status</label
          >
          <select
            id="itbkk-status"
            v-model="formData.selectedStatus"
            class="bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option
              v-for="status in statuses"
              :value="[status.id, status.name, status.description]"
              :key="status.id"
            >
              {{ status.name }}
            </option>
          </select>
        </div>
        <div class="flex mt-5 justify-center">
          <button
            id="itbkk-button-confirm"
            type="submit"
            :disabled="formData.title.trim().length === 0 || !formData.selectedStatus"
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
              formData.title.trim().length === 0 || !formData.selectedStatus
                ? 'bg-gray-400'
                : 'bg-green-400',
              formData.title.trim().length === 0 || !formData.selectedStatus ? 'disabled' : ''
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
