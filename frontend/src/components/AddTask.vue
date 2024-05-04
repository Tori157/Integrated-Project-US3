<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
// import {toggleModal} from '../views/HomeView.vue'
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
    if (response.ok) {
      router.push('/task')
    } else {
      console.error('Failed to save task:', response.statusText)
    }
  } catch (error) {
    console.error('Error saving task:', error)
  }
}
// const handleSaveClick = () => {
//   // ส่งค่าไปยัง HomeView.vue
//   toggleModal()
// }
</script>

<template>
  <div class="fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50">
    <div class="bg-blue-100 rounded-lg p-8 max-w-3xl w-full">
      <h2 class="text-xl font-bold mb-2 text-center text-20 text-black">Add Task</h2>

      <form @submit.prevent="saveTask">
        <div class="mb-6">
          <label for="title" class="block text-sm font-medium text-gray-700">Title</label>
          <input
            type="text"
            id="itbkk-title"
            v-model="title"
            class="text-white mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          />
        </div>
        <div class="mb-4">
          <label for="description" class="block text-sm font-medium text-gray-700"
            >Description</label
          >
          <textarea
            id="itbkk-description"
            v-model="description"
            class="text-white mt-1 block h-40 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          ></textarea>
        </div>
        <div class="mb-4">
          <label for="assignees" class="block text-sm font-medium text-gray-700">Assignees</label>
          <input
            type="text"
            id="itbkk-assignees"
            v-model="assignees"
            class="text-white mt-1 h-9 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          />
        </div>
        <div class="mb-4">
          <label for="status" class="block text-sm font-medium text-gray-700">Status</label>
          <select
            id="itbkk-status"
            v-model="status"
            class="text-white mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
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
            @click="() => router.back()"
            class="bg-red-400 border-4 border-white rounded-3xl mx-5 p-8 px-6 py-2 text-base text-white font-semibold text-center"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
