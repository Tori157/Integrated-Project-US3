<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const tasks = ref([])
const SERVER_URL = import.meta.env.VITE_SERVER_URL
const route = useRoute()
const router = useRouter()
async function fetchTask() {
  try {
    const response = await fetch(SERVER_URL + `/v1/tasks/${route.params.id}`)
    if (!response.ok) {
      throw new Error('Failed to fetch tasks')
    }
    const data = await response.json()
    console.log(data)
    tasks.value = data
    getTimezone() // Call setTimezone function here
  } catch (error) {
    console.error('Error fetching tasks:', error)
    alert('The requested task does not exist')
    setTimeout(() => {
      router.push('/task')
    }, 3000)
  }
}

onMounted(async () => {
  await fetchTask()
  console.log(tasks.value)
})

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

const modal = document.getElementById('my_modal_3')
window.addEventListener('click', function (event) {
  if (event.target === modal) {
    modal.style.display = 'none'
  }
})
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
            class="itbkk-title text-16px ml-10 mt-10 flex justify-between items-center p-5 bg-white w-96 rounded-xl text-slate-600 overflow-auto"
          >
            {{ tasks.title }}
          </div>
          <div class="ml-10 mt-4">
            <h2 class="w-max text-slate-600">Description:</h2>
            <textarea
              class="itbkk-description h-64 bg-white rounded-xl w-96 mt-2 text-sm p-4"
              :class="!tasks.description ? 'text-[grey] italic' : 'text-slate-600'"
              :value="tasks.description || 'No Description Provided'"
              @input="tasks.description = $event.target.value"
              v-show="tasks.description || tasks.description === ''"
            >
            </textarea>
            <div
              :class="!tasks.description ? 'text-[grey] italic' : 'text-slate-600'"
              class="itbkk-description hidden"
            >
              {{ tasks.description.trim() || 'No Description Provided' }}
            </div>
          </div>
        </div>

        <div class="r-zone m-10">
          <div class="text-slate-600">Assigness:</div>
          <textarea
            class="itbkk-assignees mt-3 h-44 w-full bg-white rounded-xl mb-2 text-sm p-4"
            :class="!tasks.assignees ? 'text-[grey] italic' : 'text-slate-600'"
            :value="tasks.assignees || 'Unassigned'"
            @input="tasks.assignees = $event.target.value"
            v-show="tasks.assignees || tasks.assignees === ''"
          >
          </textarea>

          <div class="text-slate-600">Status:</div>
          <select
            class="itbkk-status mt-2 bg-white rounded-lg w-24 ml-2 h-max text-slate-600 border border-slate-600 hover:bg-white"
          >
            <option value=" ">{{ getStatusText(tasks.status) }}</option>
            <!-- <option value="nostatus">No Status</option>
            <option value="todo">To Do</option>
            <option value="doing">Doing</option>
            <option value="done">Done</option> -->
          </select>

          <div class="itbkk-timezone mt-4 mb-2 text-slate-600 text-sm">
            Timezone : {{ getTimezone(tasks.timezone) }}
          </div>
          <div class="itbkk-created-on mb-2 text-slate-600 text-sm">
            Create On: {{ formateDateTime(tasks.createdOn) }}
          </div>
          <div class="itbkk-updated-on mb-2 text-slate-600 text-sm">
            Update On: {{ formateDateTime(tasks.updatedOn) }}
          </div>
          <div class="flex">
            <form method="dialog" class="modal-backdrop">
              <button>close</button>
            </form>
            <div class="modal-action">
              <form method="dialog" class="flex">
                <!-- <button
                  class="itbkk-button btn mr-6 bg-green-500 hover:bg-green-600 w-max h-5 border-solid text-slate-600 border-4 border-white rounded-3xl p-6 px-8 py-2 text-base text-white font-semibold text-center"
                >
                  Save
                </button> -->
                <button
                  class="itbkk-button btn bg-red-400 hover:bg-red-500 w-max h-5 border-solid text-slate-600 border-4 border-white rounded-3xl p-6 px-8 py-2 text-base text-white font-semibold text-center"
                  @click="() => router.back()"
                >
                  Close
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
