<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useTaskStore } from '@/stores/TaskStore'
import { showAlert } from '@/utils/toast.js'
import { useCurrentBoardStore } from '@/stores/BoardStore'

const currentBoardStore = useCurrentBoardStore()
const boardId = computed(() => currentBoardStore.currentBoardId)

const router = useRouter()
const route = useRoute()
const taskStore = useTaskStore()

const taskId = parseInt(route.params.id)
const taskTitle = ref('')
const taskIndex = ref(null)

// Fetch tasks on component mount
onMounted(async () => {
  await taskStore.fetchTasks()

  if (taskStore.tasks.value) {
    const task = taskStore.tasks.value.find((task) => task.id === taskId)

    if (task) {
      taskTitle.value = task.title
      taskIndex.value = findIndexById(taskId) + 1 // Set taskIndex here
    } else {
      console.error('Task not found')
    }
  } else {
    console.error('Tasks are not loaded yet')
  }
})

function findIndexById(taskId) {
  if (!taskStore.tasks.value) {
    return null // Return null if tasks are not loaded yet
  }

  for (let i = 0; i < taskStore.tasks.value.length; i++) {
    if (taskStore.tasks.value[i].id === taskId) {
      return i
    }
  }
  return null // Return null if no task with the specified ID is found
}

async function deleteTask(taskId) {
  try {
    await taskStore.deleteTask(taskId)
    router.push(`/boards/${boardId.value}/tasks`)
    showAlert('The task has been deleted.', 'rgb(244 63 94)')
  } catch (error) {
    console.error('Error deleting task:', error)
    showAlert('An error occurred while deleting the task.', 'rgb(251 146 60)')
  }
}

function cancel() {
  router.push(`/boards/${boardId.value}/tasks`)
}
</script>

<template>
  <div
    id="modelConfirm"
    class="fixed z-50 inset-0 bg-gray-900 bg-opacity-60 overflow-y-auto h-full w-full px-4 flex justify-center items-center bg-blue-100"
  >
    <div class="relative mx-auto shadow-xl rounded-md max-w-md bg-blue-100 mb-52">
      <div class="mt-5 mb-5 p-6 pt-0 text-center bg-blue-100">
        <img src="/image/ico/alert-2-svgrepo-com.svg" class="w-20 h-20 mx-auto" />

        <h3
          class="itbkk-message text-xl font-semi text-gray-500 mt-5 mb-6 whitespace-normal break-words"
        >
          "Do you want to delete the task number {{ taskIndex }} - {{ taskTitle }}"?
        </h3>

        <button
          @click="deleteTask(taskId)"
          class="itbkk-button-delete mr-5 text-white bg-red-600 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-base inline-flex items-center px-3 py-2.5 text-center mr-2"
        >
          Confirm
        </button>

        <button
          @click="cancel"
          class="itbkk-button-cancel ml-5 text-gray-900 bg-white hover:bg-gray-100 focus:ring-4 focus:ring-cyan-200 border border-gray-200 font-medium inline-flex items-center rounded-lg text-base px-3 py-2.5 text-center"
          data-modal-toggle="delete-user-modal"
        >
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>
