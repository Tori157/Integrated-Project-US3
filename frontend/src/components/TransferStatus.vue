<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const tasks = ref([])
const taskId = parseInt(route.params.id)
const taskTitle = ref('')
const taskStatus = ref('')

function getStatusText(status) {
  switch (status) {
    case 'NO_STATUS':
      return 'No Status'
    case 'TO_DO':
      return 'To Do'
    case 'DOING':
      return 'Doing'
    case 'DONE':
      return 'Done'
    default:
      return 'No Status'
  }
}

onMounted(async () => {
  try {
    const response = await fetch('http://localhost:8080/v1/tasks')
    const data = await response.json()
    tasks.value = data

    const task = tasks.value.find((task) => task.id === parseInt(taskId))
    if (task) {
      taskTitle.value = task.title
    }

    const status = tasks.value.find((status) => status.id === parseInt(taskId))
    if (status) {
      taskStatus.value = task.status
    }
  } catch (error) {
    console.error('Error fetching tasks:', error)
  }
})

async function deleteTask(taskId) {
  try {
    const res = await fetch(`http://localhost:8080/v1/tasks/${taskId}`, {
      method: 'DELETE'
    })

    if (res.status === 200) {
      console.log('Task deleted successfully')
      tasks.value = tasks.value.filter((task) => task.id !== taskId)
      router.push('/statuslist')
      // alert
      console.error('Failed to delete task')
      const toastDiv = document.createElement('div')
      toastDiv.className = 'toast toast-top toast-center' // ตำเเหน่ง
      const alertSuccessDiv = document.createElement('div')
      alertSuccessDiv.className = 'alert alert-success'
      alertSuccessDiv.innerHTML = '<span>The task has been deleted.</span>'
      alertSuccessDiv.style.backgroundColor = 'rgb(244 63 94)' // สีพื้นหลัง
      alertSuccessDiv.style.color = 'white' // สีข้อความ
      alertSuccessDiv.style.textAlign = 'center' // ตรงกลาง
      alertSuccessDiv.style.display = 'flex' // ให้เนื้อหาอยู่ตรงกลาง

      toastDiv.appendChild(alertSuccessDiv)
      document.body.appendChild(toastDiv)

      // reload
      setTimeout(function () {
        document.body.removeChild(toastDiv)
        window.location.reload()
      }, 2000)
    }
    if (res.status === 404) {
      console.log('The task does not exist.')
      console.error('Failed to delete task')

      console.error('Failed to delete task')
      const toastDiv = document.createElement('div')
      toastDiv.className = 'toast toast-top toast-center' // ตำเเหน่ง
      const alertSuccessDiv = document.createElement('div')
      alertSuccessDiv.className = 'alert alert-success'
      alertSuccessDiv.innerHTML = '<span>An error has occurred, the task does not exist.</span>'
      alertSuccessDiv.style.backgroundColor = 'rgb(251 146 60)' // สีพื้นหลัง
      alertSuccessDiv.style.color = 'white' // สีข้อความ
      alertSuccessDiv.style.textAlign = 'center' // ตรงกลาง
      alertSuccessDiv.style.display = 'flex' // ให้เนื้อหาอยู่ตรงกลาง

      toastDiv.appendChild(alertSuccessDiv)
      document.body.appendChild(toastDiv)

      router.push('/statuslist')
      setTimeout(function () {
        document.body.removeChild(toastDiv)
        window.location.reload()
      }, 2000)
    }
  } catch (error) {
    console.error('Error:', error)
  }
}
function findIndexById(taskId) {
  for (let i = 0; i < tasks.value.length; i++) {
    if (tasks.value[i].id === taskId) {
      return i
    }
  }
  return null
}

function cancel() {
  router.push('/statuslist')
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
          There is some task associated with the {{ getStatusText(taskStatus) }} status.<br />
          Transfer to
          <select
            class="itbkk-status block bg-slate-200 rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:max-w-xs sm:text-sm sm:leading-6"
          >
            <option>No Status</option>
            <option>To Do</option>
            <option>Doing</option>
            <option>Done</option>
          </select>
        </h3>

        <button
          @click="transferTask(taskId)"
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
