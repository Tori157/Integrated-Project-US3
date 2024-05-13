<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const SERVER_URL = import.meta.env.VITE_SERVER_URL

const statuses = ref([])
const statusId = parseInt(route.params.id)
const statusesname = ref('')

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
    const res = await fetch(SERVER_URL + `/v2/statuses/${statusId}`, {
      method: 'DELETE'
    })
    const statusToDelete = statuses.value.find((status) => status.id === statusId)
    if (!statusToDelete) {
      console.error('Status not found.')
      return
    }

    // Prevent deletion of NO_STATUS
    if (statusToDelete.name === 'NO_STATUS') {
      console.error('Cannot delete status named NO_STATUS.')
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

      router.push('/statuslist')
      // alert

      const toastDiv = document.createElement('div')
      toastDiv.className = 'toast toast-top toast-center' // ตำเเหน่ง
      const alertSuccessDiv = document.createElement('div')
      alertSuccessDiv.className = 'alert alert-success'
      alertSuccessDiv.innerHTML = '<span>The Status has been deleted.</span>'
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
      console.log('Status not found.')
      console.error('Failed to delete status')

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
    }
  } catch (error) {
    console.error('Error:', error)
  }
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
