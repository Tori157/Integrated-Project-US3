<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const tasks = ref([])
const SERVER_URL = import.meta.env.VITE_SERVER_URL
const route = useRoute()
const router = useRouter()
async function fetchTask() {
  try {
    const response = await fetch(SERVER_URL + `/v2/tasks/${route.params.id}`)
    if (!response.ok) {
      throw new Error('Failed to fetch tasks')
    }
    const data = await response.json()
    console.log(data)
    tasks.value = data
    getTimezone() // Call setTimezone function here
  } catch (error) {
    console.error('Error fetching tasks:', error)
    router.push('/error')
  }
}

onMounted(async () => {
  await fetchTask()
  console.log(tasks.value)
})

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
            class="itbkk-title ml-10 mt-10 items-center p-5 break-words bg-white w-96 rounded-xl text-sm text-blue-600 font-normal h-22"
          >
            {{ tasks.title }}
          </div>

          <div class="ml-10 mt-4">
            <h2 class="w-max text-base text-rose-400 font-medium">Description:</h2>
            <div
              class="itbkk-description break-words bg-white rounded-xl mt-2 text-sm p-5 mb-10 w-96 h-64"
              :class="!tasks.description ? 'text-[grey] italic' : 'text-blue-600'"
            >
              {{ tasks.description || 'No Description Provided' }}
            </div>
          </div>
        </div>

        <div class="r-zone m-10">
          <div class="text-base text-rose-400 font-medium">Assigness:</div>
          <div
            class="itbkk-assignees break-words bg-white rounded-xl mt-2 text-sm p-4 mb-8"
            :class="!tasks.assignees ? 'text-[grey] italic' : 'text-blue-600'"
          >
            {{ tasks.assignees || 'Unassigned' }}
          </div>

          <div class="text-base text-rose-400 font-medium">Status:</div>
          <div
            class="itbkk-status break-words bg-white rounded-lg mt-2 h-max text-blue-600 text-center h-10 p-2 mb-8"
          >
            {{ tasks.status.name }}
          </div>

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
                  class="itbkk-button btn mr-6 bg-green-500 hover:bg-green-600 w-max h-5 border-solid text-slate-600 border-4 border-white rounded-3xl p-6 px-8 py-2 text-base text-white font-semibold text-center"
                >
                  Save
                </button> -->

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
  <!-- </div> -->
</template>
