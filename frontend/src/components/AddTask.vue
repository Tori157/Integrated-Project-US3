<script setup>
import { reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useTaskStore } from '@/stores/TaskStore'
import { useStatusStore } from '@/stores/StatusStore'
import { showAlert, showAlert2 } from '@/utils/toast.js'
import { useCurrentBoardStore } from '@/stores/BoardStore'

const formData = reactive({
  title: '',
  description: '',
  assignees: '',
  status: null
})

const router = useRouter()
const taskStore = useTaskStore()
const statusStore = useStatusStore()
const currentBoardStore = useCurrentBoardStore()
const boardId = computed(() => currentBoardStore.currentBoardId)

onMounted(async () => {
  await statusStore.fetchStatuses(boardId.value)
})

const saveTask = async () => {
  if (!formData.status) {
    const noStatus = statusStore.statuses.find((status) => status.name === 'No Status')
    if (noStatus) {
      formData.status = noStatus.id
    } else {
      console.error('No Status ID not found')
      showAlert('No Status ID not found. Please select a valid status.', 'rgb(251 146 60)')
      return
    }
  }

  const taskData = {
    title: formData.title.trim(),
    description: formData.description.trim(),
    assignees: formData.assignees.trim(),
    status: formData.status
  }

  try {
    const response = await taskStore.addTask(taskData)
    if (response.status === 201) {
      showAlert('The task has been successfully added.', 'rgb(34 197 94)')
      router.push(`/boards/${boardId.value}/tasks`)
    } else {
      showAlert("An error has occurred, the task can't be added.", 'rgb(251 146 60)')
      console.error('Failed to save task:', response.statusText)
      router.push(`/boards/${boardId.value}/tasks`)
    }
  } catch (error) {
    console.error('Error saving task:', error)
  }
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
</script>

<template>
  <div class="inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50">
    <div class="itbkk-modal-task fixed bg-blue-100 rounded-lg p-8 max-w-3xl w-full">
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
            class="itbkk-title bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
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
            class="itbkk-description bg-white text-blue-600 mt-1 block h-40 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
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
            class="itbkk-assignees bg-white text-blue-600 mt-1 h-9 block w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            maxlength="30"
          />
        </div>
        <div class="mb-4">
          <label for="status" class="text-rose-400 block text-sm font-medium text-gray-700"
            >Status</label
          >
          <select
            id="itbkk-status"
            v-model="formData.status"
            class="itbkk-status bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value="" disabled>Select a status</option>
            <option v-for="status in statusStore.statuses" :value="status.id" :key="status.id">
              {{ status.name }}
            </option>
          </select>
        </div>
        <div class="flex mt-5 justify-center">
          <button
            id="itbkk-button-confirm"
            type="submit"
            :disabled="formData.title.trim().length === 0"
            class="itbkk-button-confirm border-4 border-white rounded-3xl mx-5 p-8 px-7 py-2 text-base text-white font-semibold text-center"
            :class="formData.title.trim().length === 0 ? 'bg-gray-400 disabled' : 'bg-green-400'"
          >
            Save
          </button>
          <button
            id="itbkk-button-cancel"
            type="button"
            @click="() => router.push(`/boards/${boardId.value}/tasks`)"
            class="itbkk-button-cancel bg-red-400 border-4 border-white rounded-3xl mx-5 p-8 px-6 py-2 text-base text-white font-semibold text-center"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
