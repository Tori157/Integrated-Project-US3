<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const title = ref('')
const description = ref('')
const assignees = ref('')
const status = ref('NO_STATUS')
const router = useRouter()

const saveTask = async () => {
  const taskData = {
    title: title.value.trim(),
    description: description.value.trim(),
    assignees: assignees.value.trim(),
    status: status.value
  }
  try {
    const response = await fetch('http://localhost:8080/v1/tasks', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(taskData)
    })
    if (response.status === 200) {
      router.push('/task')
      // Alert
      const toastDiv = document.createElement('div')
      toastDiv.className = 'toast toast-top toast-end z-50'
      const alertSuccessDiv = document.createElement('div')
      alertSuccessDiv.className = 'alert alert-success'
      alertSuccessDiv.innerHTML = '<span>The task has been successfully added.</span>'
      toastDiv.appendChild(alertSuccessDiv)
      document.body.appendChild(toastDiv)

      setTimeout(function () {
        window.location.reload()
      }, 2000)
    } else {
      console.error('Failed to save task:', response.statusText)
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
            v-model="title"
            class="bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          />
        </div>
        <div class="mb-4">
          <label for="description" class="text-rose-400 block text-sm font-medium text-gray-700"
            >Description</label
          >
          <textarea
            id="itbkk-description"
            v-model="description"
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
            v-model="assignees"
            class="bg-white text-blue-600 mt-1 h-9 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          />
        </div>
        <div class="mb-4">
          <label for="status" class="text-rose-400 block text-sm font-medium text-gray-700"
            >Status</label
          >
          <select
            id="itbkk-status"
            v-model="status"
            class="bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value="NO_STATUS">No Status</option>
            <option value="TO_DO">To Do</option>
            <option value="DOING">Doing</option>
            <option value="DONE">Done</option>
          </select>
        </div>
        <div class="flex mt-5 justify-center">
          <button
            id="itbkk-button-confirm"
            type="submit"
            v-if="title.trim().length > 0"
            @click="toggleModal"
            class="bg-green-400 border-4 border-white rounded-3xl mx-5 p-8 px-7 py-2 text-base text-white font-semibold text-center"
          >
            Save
          </button>
          <!-- <button
            id="itbkk-button-confirm"
            @click="handleSaveClick"
            v-if="title.trim().length > 0"
            class="bg-green-400 border-4 border-white rounded-3xl mx-5 p-8 px-7 py-2 text-base text-white font-semibold text-center"
          >
            Save
          </button> -->
          <button
            id="itbkk-button-cancle"
            type="button"
            @click="() => router.push('/task')"
            class="bg-red-400 border-4 border-white rounded-3xl mx-5 p-8 px-6 py-2 text-base text-white font-semibold text-center"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
