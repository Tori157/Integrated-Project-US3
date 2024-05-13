<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const SERVER_URL = import.meta.env.VITE_SERVER_URL
const route = useRoute()
const router = useRouter()

const tasks = ref({})
const originalTasks = ref({})
const statuses = ref([])

onMounted(async () => {
  await fetchTask()
  await fetchStatuses()
})

// Fetch task details
async function fetchTask() {
  try {
    const response = await fetch(SERVER_URL + `/v2/tasks/${route.params.id}`)
    if (!response.ok) {
      throw new Error('Failed to fetch tasks')
    }
    const data = await response.json()
    tasks.value = { ...data }
    originalTasks.value = { ...data }
    getTimezone()
  } catch (error) {
    console.error('Error fetching tasks:', error)
    router.push('/editerror')
  }
}

// Fetch statuses
async function fetchStatuses() {
  try {
    const response = await fetch(SERVER_URL + '/v2/statuses')
    if (response.ok) {
      statuses.value = await response.json()
    } else {
      console.error('Failed to fetch statuses:', response.statusText)
    }
  } catch (error) {
    console.error('Error fetching statuses:', error)
  }
}

// Save changes to task
async function saveChanges() {
  try {
    const response = await fetch(SERVER_URL + `/v2/tasks/${route.params.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(tasks.value)
    })
    if (response.ok) {
      router.push('/task')
      console.log('Task updated successfully')
      console.log(tasks.value)

      // Alert
      const toastDiv = document.createElement('div')
      toastDiv.className = 'toast toast-top toast-center z-50'
      const alertSuccessDiv = document.createElement('div')
      alertSuccessDiv.className = 'alert alert-success'
      alertSuccessDiv.innerHTML = '<span>The task has been updated</span>'
      alertSuccessDiv.style.backgroundColor = 'rgb(34 197 94)' // สีพื้นหลัง
      alertSuccessDiv.style.color = 'white' // สีข้อความ
      alertSuccessDiv.style.textAlign = 'center' // ตรงกลาง
      alertSuccessDiv.style.display = 'flex' // ให้เนื้อหาอยู่ตรงกลาง

      toastDiv.appendChild(alertSuccessDiv)
      document.body.appendChild(toastDiv)

      setTimeout(function () {
        document.body.removeChild(toastDiv)
        window.location.reload()
      }, 2000)
    } else {
      console.error('Failed to update task')
      console.log(tasks.value)

      // alert
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

      // router.push('/task')
      // setTimeout(function () {
      //   document.body.removeChild(toastDiv)
      //   window.location.reload()
      // }, 2000)
    }
  } catch (error) {
    console.error('Error updating task:', error)
  }
}
function formateDateTime(time) {
  const date = new Date(time)
  const formate = {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }
  return date.toLocaleString('en-GB', formate)
}

function getTimezone() {
  return Intl.DateTimeFormat().resolvedOptions().timeZone
}

function cancelEditing() {
  router.back()
}

const handleStatusChange = (event) => {
  const selectedStatusId = event.target.value
  if (selectedStatusId === originalTasks.value.status.id) {
    tasks.value.status.isModified = false
  } else {
    tasks.value.status.isModified = true
  }
}

const isModified = computed(() => {
  return (
    JSON.stringify(tasks.value) !== JSON.stringify(originalTasks.value) ||
    (tasks.value.status && tasks.value.status.isModified)
  )
})

const isStatusModified = computed(() => {
  return tasks.value.status !== originalTasks.value.status
})

function formatStatusName(name) {
  if (name === name.toLowerCase()) {
    return name.replace(/_/g, ' ')
  }
  const formattedName = name
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/(?:^|\s)\S/g, (a) => a.toUpperCase())
  return formattedName.replace(/_/g, ' ').trim()
}
</script>

<template>
  <div
    class="absolute top-0 left-0 backdrop-blur-sm"
    v-if="tasks && Object.keys(tasks).length !== 0"
  >
    <div id="my_modal_2" class="flex items-center justify-center w-screen h-screen">
      <div
        id="my_modal_3"
        class="flex h-max w-max bg-blue-100 rounded-xl border-2 border-slate-600"
      >
        <div class="l-zone w-max">
          <div
            class="itbkk-title ml-10 mt-10 break-words text-blue-600 bg-white rounded-xl p-5 w-96 text-sm font-normal h-22"
          >
            <!-- Editable Title -->
            <input
              v-model="tasks.title"
              type="text"
              class="w-full"
              style="background-color: white"
              maxlength="100"
            />
          </div>

          <div class="ml-10 mt-4">
            <h2 class="w-max text-base text-rose-400 font-medium">Description:</h2>

            <!-- Editable Description -->
            <textarea
              v-model="tasks.description"
              class="itbkk-description break-words text-blue-600 bg-white rounded-xl mt-2 text-sm p-5 mb-10 w-96 h-64"
              maxlength="500"
            ></textarea>
          </div>
        </div>

        <div class="r-zone m-10">
          <div class="text-base text-rose-400 font-medium">Assigness:</div>
          <!-- Editable Assignees -->
          <input
            v-model="tasks.assignees"
            type="text"
            class="itbkk-assignees break-words text-blue-600 bg-white rounded-xl mt-2 text-sm p-4 mb-8 w-96"
            maxlength="30"
          />

          <div class="text-base text-rose-400 font-medium">Status:</div>
          <!-- Editable Status -->
          <select
            id="itbkk-status"
            v-model="tasks.status.id"
            class="bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            @change="handleStatusChange"
          >
            <option v-for="status in statuses" :value="status.id" :key="status.id">
              {{ formatStatusName(status.name) }}
            </option>
          </select>

          <div class="itbkk-timezone mt-6 mb-3 ml-2 text-sm text-blue-600 font-normal">
            <span class="text-base text-rose-400 font-medium">Timezone :</span>
            {{ getTimezone(tasks.timezone) }}
          </div>

          <div class="itbkk-created-on mb-3 ml-2 text-sm text-blue-600 font-normal">
            <span class="text-base text-rose-400 font-medium">Create On :</span>
            {{ formateDateTime(tasks.createdOn).replace(/,/g, '') }}
          </div>

          <div class="itbkk-updated-on mb-8 ml-2 text-sm text-blue-600 font-normal">
            <span class="text-base text-rose-400 font-medium">Update On :</span>
            {{ formateDateTime(tasks.updatedOn).replace(/,/g, '') }}
          </div>

          <div class="flex">
            <form method="dialog" class="flex">
              <!-- <button
                id="itbkk-button-edit"
                :disabled="!isModified || tasks.title.trim().length === 0"
                @click="saveChanges"
                class="itbkk-button-edit btn text-white border-white mr-6 bg-green-500 hover:bg-green-600 border-4 hover:border-green-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base font-semibold text-center ml-16"
                :class="
                  (!isModified || tasks.title.trim().length === 0
                    ? 'bg-gray-400'
                    : 'bg-green-400',
                  !isModified || tasks.title.trim().length === 0
                    ? 'text-white'
                    : '',
                  !isModified || tasks.title.trim().length === 0
                    ? 'border-white'
                    : '',
                  !isModified || tasks.title.trim().length === 0
                    ? 'disabled'
                    : '')
                "
              >
                Save
              </button> -->
              <!-- <button
                id="itbkk-button-edit"
                :disabled="!isModified || tasks.title.trim().length === 0 || isStatusModified"
                @click="saveChanges"
                class="itbkk-button-edit btn text-white border-white mr-6 bg-green-500 hover:bg-green-600 border-4 hover:border-green-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base font-semibold text-center ml-16"
                :class="
                  (!isModified || tasks.title.trim().length === 0 || isStatusModified
                    ? 'bg-gray-400'
                    : 'bg-green-400',
                  !isModified || tasks.title.trim().length === 0 || isStatusModified
                    ? 'text-white'
                    : '',
                  !isModified || tasks.title.trim().length === 0 || isStatusModified
                    ? 'border-white'
                    : '',
                  !isModified || tasks.title.trim().length === 0 || isStatusModified
                    ? 'disabled'
                    : '')
                "
              >
                Save
              </button> -->
              <button
                id="itbkk-button-edit"
                :disabled="!isModified || tasks.title.trim().length === 0 || isStatusModified"
                @click="saveChanges"
                class="itbkk-button-edit btn text-white border-white mr-6 bg-green-500 hover:bg-green-600 border-4 hover:border-green-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base font-semibold text-center ml-16"
                :class="
                  (!isModified || tasks.title.trim().length === 0 || isStatusModified
                    ? 'bg-gray-400'
                    : 'bg-green-400',
                  !isModified || tasks.title.trim().length === 0 || isStatusModified
                    ? 'text-white'
                    : '',
                  !isModified || tasks.title.trim().length === 0 || isStatusModified
                    ? 'border-white'
                    : '',
                  !isModified || tasks.title.trim().length === 0 || isStatusModified
                    ? 'disabled'
                    : '')
                "
              >
                Save
              </button>

              <button
                id="itbkk-button-cancel"
                class="itbkk-button-cancel btn mr-6 bg-red-500 hover:bg-red-600 border-4 border-white hover:border-red-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base text-white font-semibold text-center ml-16"
                @click="cancelEditing"
              >
                Cancle
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
