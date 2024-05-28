<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const BASE_URL = import.meta.env.VITE_BASE_URL
const route = useRoute()
const router = useRouter()

const tasks = ref({})
const originalTasks = ref({})
const statuses = ref([])

onMounted(async () => {
  await fetchTask()
  await fetchStatuses()
})

async function fetchTask() {
  try {
    const response = await fetch(`${BASE_URL}/v2/tasks/${route.params.id}`)
    if (!response.ok) {
      throw new Error('Failed to fetch tasks')
    }
    const data = await response.json()
    tasks.value = { ...data }
    originalTasks.value = { ...data }
    console.log(tasks.value)
    getTimezone()
  } catch (error) {
    console.error('Error fetching tasks:', error)
    router.push('/editerror')
  }
}

async function fetchStatuses() {
  try {
    const response = await fetch(`${BASE_URL}/v2/statuses`)
    if (!response.ok) {
      throw new Error('Failed to fetch statuses')
    }
    statuses.value = await response.json()
  } catch (error) {
    console.error('Error fetching statuses:', error)
  }
}

// Save changes to task
async function saveChanges() {
  try {
    const updatedTask = {
      id: tasks.value.id,
      title: tasks.value.title.trim(),
      description: tasks.value.description ? tasks.value.description.trim() : '',
      assignees: tasks.value.assignees ? tasks.value.assignees.trim() : '',
      statusId: tasks.value.status.id
    }

    const response = await fetch(`${BASE_URL}/v2/tasks/${route.params.id}`, {
      method: `PUT`,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(updatedTask)
    })

    if (response.ok) {
      console.log('Task updated successfully')
      showAlert('The task has been updated', 'rgb(34 197 94)')
      router.push('/task')
      console.log(updatedTask)
      console.log(route.params.id)
    } else {
      console.error('Failed to update task')
      console.log(updatedTask)
      console.log(route.params.id)
      router.push('/task')
      showAlert('An error has occurred, the task does not exist.', 'rgb(251 146 60)')
    }
  } catch (error) {
    console.error('Error updating task:', error)
    showAlert('An error has occurred during the update process.', 'rgb(251 146 60)')
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

function formateDateTime(time) {
  const date = new Date(time)
  const options = {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }
  return date.toLocaleString('en-GB', options)
}

function getTimezone() {
  return Intl.DateTimeFormat().resolvedOptions().timeZone
}

function cancelEditing() {
  router.back()
}

const handleStatusChange = (event) => {
  const selectedStatusId = event.target.value
  tasks.value.status.isModified = selectedStatusId !== originalTasks.value.status.id
}

const isModified = computed(() => {
  return (
    JSON.stringify(tasks.value) !== JSON.stringify(originalTasks.value) ||
    (tasks.value.status && tasks.value.status.isModified)
  )
})

const isStatusModified = computed(() => {
  return tasks.value.status && tasks.value.status.id !== originalTasks.value.status.id
})

const maxLengths = {
  title: 100,
  description: 500,
  assignees: 30
}

const checkMaxLength = (field) => {
  if (tasks.value[field].length >= maxLengths[field]) {
    showAlert2(`Your text in ${field} is at maximum length`, 'rgb(251 146 60)')
  }
}

function showAlert2(message, backgroundColor) {
  const existingAlert = document.querySelector('.alert-success')
  if (existingAlert) {
    existingAlert.parentElement.removeChild(existingAlert)
  }

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
    if (toastDiv.parentElement) {
      toastDiv.parentElement.removeChild(toastDiv)
    }
  }, 2000)
}
</script>

<template>
  <div
    v-if="tasks && Object.keys(tasks).length !== 0"
    class="absolute top-0 left-0 backdrop-blur-sm"
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
            <input
              v-model="tasks.title"
              type="text"
              class="w-full"
              style="background-color: white"
              @input="checkMaxLength('title')"
              maxlength="100"
            />
          </div>
          <div class="ml-10 mt-4">
            <h2 class="w-max text-base text-rose-400 font-medium">Description:</h2>
            <textarea
              v-model="tasks.description"
              class="itbkk-description break-words text-blue-600 bg-white rounded-xl mt-2 text-sm p-5 mb-10 w-96 h-64"
              @input="checkMaxLength('description')"
              maxlength="500"
            ></textarea>
          </div>
        </div>
        <div class="r-zone m-10">
          <div class="text-base text-rose-400 font-medium">Assigness:</div>
          <input
            v-model="tasks.assignees"
            type="text"
            class="itbkk-assignees break-words text-blue-600 bg-white rounded-xl mt-2 text-sm p-4 mb-8 w-96"
            @input="checkMaxLength('assignees')"
            maxlength="30"
          />
          <div class="text-base text-rose-400 font-medium">Status:</div>
          <select
            id="itbkk-status"
            v-model="tasks.status.id"
            class="bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            @change="handleStatusChange"
          >
            <option v-for="status in statuses" :value="status.id" :key="status.id">
              {{ status.name }}
            </option>
          </select>
          <div class="itbkk-timezone mt-6 mb-3 ml-2 text-sm text-blue-600 font-normal">
            <span class="text-base text-rose-400 font-medium">Timezone :</span>
            {{ getTimezone() }}
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
              <button
                id="itbkk-button-edit"
                :disabled="!isModified || tasks.title.trim().length === 0 || isStatusModified"
                @click="saveChanges"
                class="itbkk-button-edit btn text-white border-white mr-6 bg-green-500 hover:bg-green-600 border-4 hover:border-green-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base font-semibold text-center ml-16"
                :class="{
                  'bg-gray-400': !isModified || tasks.title.trim().length === 0 || isStatusModified,
                  'text-white': !isModified || tasks.title.trim().length === 0 || isStatusModified,
                  'border-white':
                    !isModified || tasks.title.trim().length === 0 || isStatusModified,
                  disabled: !isModified || tasks.title.trim().length === 0 || isStatusModified
                }"
              >
                Save
              </button>
              <!-- <button
                id="itbkk-button-edit"
                :disabled="!isModified || tasks.title.trim().length === 0"
                @click="saveChanges"
                class="itbkk-button-edit btn text-white border-white mr-6 bg-green-500 hover:bg-green-600 border-4 hover:border-green-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base font-semibold text-center ml-16"
                :class="{
                  'bg-gray-400': !isModified || tasks.title.trim().length === 0,
                  'text-white': !isModified || tasks.title.trim().length === 0,
                  'border-white': !isModified || tasks.title.trim().length === 0,
                  disabled: !isModified || tasks.title.trim().length === 0
                }"
              >
                Save
              </button> -->
              <button
                id="itbkk-button-cancel"
                class="itbkk-button-cancel btn mr-6 bg-red-500 hover:bg-red-600 border-4 border-white hover:border-red-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base text-white font-semibold text-center ml-16"
                @click="cancelEditing"
              >
                Cancel
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
