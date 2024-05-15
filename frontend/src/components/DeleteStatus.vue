<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const SERVER_URL = import.meta.env.VITE_SERVER_URL

const statuses = ref([])
const statusId = parseInt(route.params.id)
const statusesname = ref('')
const targetStatusId = ref(null)

onMounted(async () => {
  try {
    const response = await fetch(SERVER_URL + `/v2/statuses`)
    const data = await response.json()
    statuses.value = data

    const status = statuses.value.find((status) => status.id === parseInt(statusId))
    if (status) {
      statusesname.value = status.name
    }
  } catch (error) {
    console.error('Error fetching tasks:', error)
  }
})

async function deleteStatus(statusId) {
  try {
    if (targetStatusId.value) {
      await transferTasks(statusId, targetStatusId.value)
    }
    const res = await fetch(SERVER_URL + `/v2/statuses/${statusId}`, {
      method: 'DELETE'
    })
    const statusToDelete = statuses.value.find((status) => status.id === statusId)
    if (!statusToDelete) {
      console.error('Status not found.')
      const toastDiv = document.createElement('div')
      toastDiv.className = 'toast toast-top toast-center' // ตำเเหน่ง
      const alertSuccessDiv = document.createElement('div')
      alertSuccessDiv.className = 'alert alert-success'
      alertSuccessDiv.innerHTML = '<span>An error has occurred, the status does not exist.</span>'
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
      return
    }

    // Prevent deletion of NO_STATUS
    if (statusToDelete.name === 'No Status') {
      console.error('Cannot delete status named No Status.')
      const toastDiv = document.createElement('div')
      toastDiv.className = 'toast toast-top toast-center' // ตำเเหน่ง
      const alertSuccessDiv = document.createElement('div')
      alertSuccessDiv.className = 'alert alert-success'
      alertSuccessDiv.innerHTML =
        '<span>This status is the default status and cannot be modified.</span>'
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

    if (res.status === 200) {
      console.log('Status deleted successfully')

      statuses.value = statuses.value.filter((status) => status.id !== statusId)
      if (targetStatusId.value) {
        await transferTasks(statusId, targetStatusId.value)
      }
      router.push('/statuslist')
      // alert
      showAlert('The Status has been deleted.', 'rgb(34 197 94)')
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

function findIndexById(statusId) {
  for (let i = 0; i < statuses.value.length; i++) {
    if (statuses.value[i].id === statusId) {
      return i
    }
  }
  return null
}

function cancel() {
  router.push('/statuslist')
}

function formatStatusName(name) {
  // ถ้าชื่อทุกตัวเป็นตัวพิมพ์เล็กทั้งหมด ให้คืนค่าเป็นชื่อเดิม
  if (name === name.toLowerCase()) {
    return name.replace(/_/g, ' ')
  }

  // ทำตัวพิมพ์ใหญ่เฉพาะตัวอักษรต้นคำ
  const formattedName = name
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/(?:^|\s)\S/g, (a) => a.toUpperCase())

  // ตัดช่องว่างและเครื่องหมาย _ ออก
  return formattedName.replace(/_/g, ' ').trim()
}
async function transferTasks(fromStatusId, toStatusId) {
  try {
    // Fetch tasks associated with the status being deleted
    const response = await fetch(SERVER_URL + `/v2/tasks?statusId=${fromStatusId}`)
    const tasks = await response.json()

    // Update each task to set the new status
    for (const task of tasks) {
      await fetch(SERVER_URL + `/v2/tasks/${task.id}`, {
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

    console.log('Tasks transferred successfully')
  } catch (error) {
    console.error('Error transferring tasks:', error)
    throw error // Propagate the error to the caller
  }
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
          Do you want to delete the {{ findIndexById(statusId) + 1 }} -
          {{ formatStatusName(statusesname) }} status?
        </h3>

        <select
          v-model="targetStatusId"
          class="itbkk-select mb-3 bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
        >
          <option value="" disabled>Select a status to transfer tasks</option>
          <option
            v-for="status in statuses"
            :key="status.id"
            :value="status.id"
            :disabled="status.id === statusId"
          >
            Transfer tasks to {{ status.name }}
          </option>
        </select>

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
