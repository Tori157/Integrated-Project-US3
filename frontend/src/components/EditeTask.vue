<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTaskStore } from '@/stores/TaskStore'
import { useStatusStore } from '@/stores/StatusStore'
import { showAlert } from '@/components/utils/toast'
import { showAlert2 } from '@/components/utils/toast'
import { useCurrentBoardStore } from '@/stores/BoardStore'

const route = useRoute()
const router = useRouter()

// Access Pinia stores
const taskStore = useTaskStore()
const statusStore = useStatusStore()

// Reactive task and status data
const tasks = ref({})
const originalTasks = ref({})
const statuses = ref([])

const currentBoardStore = useCurrentBoardStore()
const boardId = computed(() => currentBoardStore.currentBoardId)

onMounted(async () => {
  await fetchTask()
  await statusStore.fetchStatuses()
  statuses.value = statusStore.statuses
})

// Fetch task from the store based on route params
async function fetchTask() {
  try {
    await taskStore.fetchTasks()
    const fetchedTask = taskStore.tasks.find((task) => task.id === parseInt(route.params.id))
    if (!fetchedTask) {
      throw new Error('Task not found')
    }
    tasks.value = { ...fetchedTask }
    originalTasks.value = { ...fetchedTask }
    console.log(fetchedTask)
    console.log(tasks.value.description)

    getTimezone()
  } catch (error) {
    console.error('Error fetching task:', error)
    router.push('/editerror')
  }
  console.log(tasks)
}

// Save changes to task using the store's update method
async function saveChanges() {
  try {
    const updatedTask = {
      id: tasks.value.id,
      title: tasks.value.title.trim(),
      description: tasks.value.description ? tasks.value.description.trim() : '',
      assignees: tasks.value.assignees ? tasks.value.assignees.trim() : '',
      status: tasks.value.status.id
    }
    console.log(tasks.value.status.id)
    await taskStore.updateTask(updatedTask)

    showAlert('The task has been updated', 'rgb(34 197 94)')
    router.push(`/boards/${boardId.value}/tasks`)
  } catch (error) {
    console.error('Error updating task:', error)
    showAlert('An error has occurred during the update process.', 'rgb(251 146 60)')
  }
}

// Utility functions
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
  router.push(`/boards/${boardId.value}/tasks`)
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
          <div class="text-base text-rose-400 font-medium">Assignees:</div>
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
            <option value="" disabled>Select a status</option>
            <option v-for="status in statuses" :value="status.id" :key="status.id">
              {{ status.name }}
            </option>
          </select>
          <div class="itbkk-timezone mt-6 mb-3 ml-2 text-sm text-blue-600 font-normal">
            <span class="text-base text-rose-400 font-medium">Timezone:</span>
            {{ getTimezone() }}
          </div>
          <div class="itbkk-created-on mb-3 ml-2 text-sm text-blue-600 font-normal">
            <span class="text-base text-rose-400 font-medium">Created On:</span>
            {{ formateDateTime(tasks.createdOn).replace(/,/g, '') }}
          </div>
          <div class="itbkk-updated-on mb-8 ml-2 text-sm text-blue-600 font-normal">
            <span class="text-base text-rose-400 font-medium">Updated On:</span>
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
                  'bg-green-500': isModified && tasks.title.trim().length > 0 && !isStatusModified
                }"
              >
                Save
              </button>
            </form>
            <button
              id="itbkk-button-cancel"
              @click="cancelEditing"
              class="btn text-white border-white mr-6 bg-red-500 hover:bg-red-600 border-4 hover:border-red-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base font-semibold text-center ml-16"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
