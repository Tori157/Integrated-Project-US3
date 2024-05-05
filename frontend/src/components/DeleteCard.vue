<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const tasks = ref([])

async function fetchData() {
  try {
    const response = await fetch('http://localhost:8080/v1/tasks')
    const data = await response.json()
    return data
  } catch (error) {
    console.error('Error fetching tasks:', error)
    return []
  }
}

const taskId = route.params.id

onMounted(async () => {
  tasks.value = await fetchData()
})

async function deleteTask(taskId) {
  try {
    const res = await fetch(`http://localhost:8080/v1/tasks/${taskId}`, {
      method: 'DELETE'
    })

    if (res.status === 200) {
      console.log('Task deleted successfully')
      tasks.value = tasks.value.filter((task) => task.id !== taskId)
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
    class="absolute left-0 right-0 m-auto bg-slate-50 flex h-1/6 w-1/6 max-w-[300px] shadow-lg rounded-md"
  >
    <div class="flex flex-col gap-10 justify-center mx-8 mt-2">
      <h1 class="font-bold text-xl text-stone-600">Delete a Task</h1>
      <h3 class="itbkk-message text-stone-600">
        Do you want to delete the task "{{ route.params.title }}"?
      </h3>
      <div class="flex flex-wrap justify-end">
        <button
          @click="deleteTask(taskId)"
          class="itbkk-button-confirm px-6 py-0.5 text-green-700 rounded-lg"
        >
          Confirm
        </button>
        <button @click="cancel" class="itbkk-button-cancel px-6 py-0.5 text-red-700 rounded-lg">
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>
