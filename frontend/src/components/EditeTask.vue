<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const tasks = ref([])
const editedTask = ref({})
const SERVER_URL = import.meta.env.VITE_SERVER_URL
const route = useRoute()
const router = useRouter()

// Fetch task details
async function fetchTask() {
  try {
    const response = await fetch(SERVER_URL + `/v1/tasks/${route.params.id}`)
    if (!response.ok) {
      throw new Error('Failed to fetch tasks')
    }
    const data = await response.json()
    tasks.value = data
    getTimezone()
  } catch (error) {
    console.error('Error fetching tasks:', error)
    router.push('/error')
  }
}

// Save changes to task
async function saveChanges() {
  try {
    const response = await fetch(SERVER_URL + `/v1/tasks/${route.params.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(editedTask.value)
    })
    if (response.ok) {
      console.log('Task updated successfully')
    } else {
      throw new Error('Failed to update task')
    }
  } catch (error) {
    console.error('Error updating task:', error)
  }
}

// Format functions and getTimezone function

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

onMounted(fetchTask)
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
            class="itbkk-title bg-white ml-10 mt-10 items-center p-5 break-words w-96 rounded-xl text-sm text-blue-600 font-normal h-22"
          >
            <!-- Editable Title -->
            <input v-model="editedTask.title" type="text" class="w-full" />
          </div>

          <div class="ml-10 mt-4">
            <h2 class="w-max text-base text-rose-400 font-medium">Description:</h2>

            <!-- Editable Description -->
            <textarea
              v-model="editedTask.description"
              class="itbkk-description break-words bg-white rounded-xl mt-2 text-sm p-5 mb-10 w-96 h-64"
            ></textarea>
          </div>
        </div>

        <div class="r-zone m-10">
          <div class="text-base text-rose-400 font-medium">Assigness:</div>
          <!-- Editable Assignees -->
          <input
            v-model="editedTask.assignees"
            type="text"
            class="itbkk-assignees break-words bg-white rounded-xl mt-2 text-sm p-4 mb-8 w-96"
          />

          <div class="text-base text-rose-400 font-medium">Status:</div>
          <!-- Editable Status -->
          <select
            v-model="editedTask.status"
            class="itbkk-status break-words bg-white rounded-lg mt-2 h-max text-blue-600 text-center h-10 p-2 mb-8"
          >
            <option value="NO_STATUS">No Status</option>
            <option value="TO_DO">To Do</option>
            <option value="DOING">Doing</option>
            <option value="DONE">Done</option>
          </select>

          <!-- Display Timezone, Created On, and Updated On -->

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
              <button
                @click="saveChanges"
                class="itbkk-button-edit btn mr-6 bg-green-500 hover:bg-green-600 border-4 border-white hover:border-green-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base text-white font-semibold text-center ml-16"
              >
                Save
              </button>

              <button
                class="itbkk-button btn mr-6 bg-green-500 hover:bg-green-600 border-4 border-white hover:border-green-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base text-white font-semibold text-center ml-16"
                @click="() => router.back()"
              >
                OK
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
