<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const tasks = ref([])

const taskId = route.params.id

async function deleteTask(taskId) {
  try {
    const res = await fetch(`http://localhost:8080/v1/tasks/${taskId}`, {
      method: 'DELETE'
    })

    if (res.status === 200) {
      console.log('Task deleted successfully')
      tasks.value = tasks.value.filter((task) => task.id !== taskId)
      router.push('/task') // Redirect to task page after successful deletion
    } else {
      console.error('Failed to delete task')
    }
  } catch (error) {
    console.error('Error:', error)
  }
}

function cancel() {
  router.push('/task')
}
</script>

<template>
  <div
    id="modelConfirm"
    class="fixed z-50 inset-0 bg-gray-900 bg-opacity-60 overflow-y-auto h-full w-full px-4 flex justify-center items-center"
  >
    <div class="relative mx-auto shadow-xl rounded-md bg-white max-w-md">
      <div class="p-6 pt-0 text-center">
        <img src="/image/ico/alert-2-svgrepo-com.svg" class="mt-4 w-20 h-20 text-red-600 mx-auto" />

        <h3 class="itbkk-message text-xl font-normal text-gray-500 mt-5 mb-6">
          Do you want to delete the task number ""?
        </h3>
        <button
          @click="deleteTask(taskId)"
          class="itbkk-button-confirm mr-5 text-white bg-red-600 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-base inline-flex items-center px-3 py-2.5 text-center mr-2"
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
