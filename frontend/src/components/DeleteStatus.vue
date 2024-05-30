<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const BASE_URL = import.meta.env.VITE_BASE_URL

const statuses = ref([])
const tasksCount = ref(0)
const statusId = parseInt(route.params.id)
const statusesname = ref('')
const targetStatusId = ref(null)

onMounted(async () => {
  try {
    const response = await fetch(`${BASE_URL}/v2/statuses`)
    const data = await response.json()
    statuses.value = data

    const status = statuses.value.find((status) => status.id === parseInt(statusId))
    if (status) {
      statusesname.value = status.name
    }
    // นับจำนวน Task ที่ใช้ Status นั้นอยู่
    const Taskresponse = await fetch(`${BASE_URL}/v2/tasks`)
    const tasksData = await Taskresponse.json()
    const tasksInStatus = tasksData.filter((task) => task.status.id === statusId)
    tasksCount.value = tasksInStatus.length
    console.log(tasksCount.value)
    console.log(tasksInStatus.length)
    console.log(tasksData)
    //
  } catch (error) {
    console.error('Error fetching tasks:', error)
  }
})

async function deleteStatus(statusId) {
  try {
    if (statusId === 1 && statusToDelete.name === 'No Status') {
      console.error('Cannot delete status named No Status.')
      showAlert('This status is the default status and cannot be modified.', 'rgb(251 146 60)')
      router.push('/statuslist')
      return
    }

    if (statusId === 7) {
      console.error('Cannot delete status named Done.')
      showAlert('This status is the default status and cannot be modified.', 'rgb(251 146 60)')
      router.push('/statuslist')
      return
    }

    if (tasksCount.value > 0) {
      if (!targetStatusId.value) {
        console.error('Target status ID is required for transferring tasks.')
        showAlert2('Please select a status to transfer tasks.', 'rgb(251 146 60)')
        return
      }
      await transferTasks(statusId, targetStatusId.value)
    }

    const url =
      tasksCount.value > 0
        ? `${BASE_URL}/v2/statuses/${statusId}/${targetStatusId.value}`
        : `${BASE_URL}/v2/statuses/${statusId}`

    const res = await fetch(url, {
      method: 'DELETE'
    })
    const statusToDelete = statuses.value.find((status) => status.id === statusId)
    if (!statusToDelete) {
      console.error('Status not found.')
      showAlert('An error has occurred, the status does not exist.', 'rgb(251 146 60)')
      router.push('/statuslist')
      return
    }

    if (statusToDelete.name === 'No Status') {
      console.error('Cannot delete status named No Status.')
      showAlert('This status is the default status and cannot be modified.', 'rgb(251 146 60)')
    }

    if (statusToDelete.name === 'Done') {
      console.error('Cannot delete status named Done.')
      showAlert('This status is the default status and cannot be modified.', 'rgb(251 146 60)')
    }

    if (res.status === 200) {
      console.log('Status deleted successfully')

      statuses.value = statuses.value.filter((status) => status.id !== statusId)
      if (targetStatusId.value) {
        await transferTasks(statusId, targetStatusId.value)
      }
      router.push('/statuslist')
      // alert
      const message =
        tasksCount.value > 0
          ? 'The status has been deleted. And Task has been transferred status'
          : 'The status has been deleted.'

      showAlert(message, 'rgb(244 63 94)')
    }
    if (res.status === 404) {
      console.log('Status not found.')
      console.error('Failed to delete status')
      showAlert('An error has occurred, the status does not exist.', 'rgb(251 146 60)')
    }
  } catch (error) {
    console.error('Error:', error)
  }
}

async function transferTasks(fromStatusId, toStatusId) {
  try {
    // Fetch tasks associated with the status being deleted
    const response = await fetch(`${BASE_URL}/v2/tasks?filterStatuses=${statusId}`)
    // const response = await fetch(BASE_URL + `/v2/tasks?statusId=${fromStatusId}`)
    const tasks = await response.json()
    console.log('Tasks transferred successfully')
    const tasksToTransfer = tasks.filter((task) => task.statusId === fromStatusId)

    // Update each task to set the new status
    for (const task of tasksToTransfer) {
      await fetch(BASE_URL + `/v2/tasks/${task.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          ...task,
          statusId: toStatusId
        })
      })
    }
  } catch (error) {
    console.error('Error transferring tasks:', error)
    throw error // Propagate the error to the caller
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

function showAlert2(message, backgroundColor) {
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
  }, 2000)
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

        <template v-if="tasksCount > 0">
          <h3
            class="itbkk-message text-xl font-semi text-gray-500 mt-5 mb-6 whitespace-normal break-words"
          >
            There are {{ tasksCount }} tasks in {{ statusesname }} status. In order to delete this
            status, the system must transfer tasks in this status to existing status. Transfer tasks
            to
          </h3>

          <select
            v-model="targetStatusId"
            class="itbkk-select mb-3 bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value="" disabled>Transfer to status</option>
            <option
              v-for="status in statuses"
              :key="status.id"
              :value="status.id"
              :disabled="status.id === statusId"
            >
              {{ status.name }}
            </option>
          </select>
        </template>

        <template v-else>
          <h3
            class="itbkk-message text-xl font-semi text-gray-500 mt-5 mb-6 whitespace-normal break-words"
          >
            Do you want to delete {{ statusesname }} status?
          </h3>
        </template>

        <button
          @click="deleteStatus(statusId)"
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
