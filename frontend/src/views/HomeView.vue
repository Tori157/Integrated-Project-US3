<script setup>
import { onMounted, ref } from 'vue'
const tasks = ref([])
async function fetchData() {
  const response = await fetch('http://localhost:8080/v1/tasks')

  const data = await response.json()
  console.log(data)
  return data
}
onMounted(async () => {
  const data = await fetchData()
  tasks.value = data
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
</script>

<template>
  <div class="p-10 w-screen h-screen bg-gray-800">
    <div class="itbkk-us3 container mx-auto w-full max-w-screen-xl">
      <div class="flex justify-between items-center mb-4">
        <h1
          class="mb-4 text-3xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r to-emerald-400 from-sky-400"
        >
          IT-Bangmod Kradan Kanban
        </h1>
      </div>
      <div class="flex justify-between items-center mx-auto max-w-lg">
        <!-- alert add success -->
        <div
        
          id="toast-success"
          class="flex items-center w-full max-w-xs p-2 mb-4 text-gray-500 rounded-lg shadow bg-white"
          role="alert"
        >
          <div
            class="inline-flex items-center justify-center flex-shrink-0 w-8 h-8 text-green-500 bg-green-100 rounded-lg"
          >
            <svg
              class="w-5 h-5"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path
                d="M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5Zm3.707 8.207-4 4a1 1 0 0 1-1.414 0l-2-2a1 1 0 0 1 1.414-1.414L9 10.586l3.293-3.293a1 1 0 0 1 1.414 1.414Z"
              />
            </svg>
            <span class="sr-only">Check icon</span>
          </div>
          <div class="ml-3 text-sm font-normal">The task has been successfully added.</div>
          <button
            type="button"
            class="ml-auto -mx-1.5 -my-1.5 bg-white text-gray-400 hover:text-gray-900 rounded-lg focus:ring-2 focus:ring-gray-300 p-1.5 hover:bg-gray-100 inline-flex items-center justify-center h-8 w-8"
            data-dismiss-target="#toast-success"
            aria-label="Close"
          >
            <span class="sr-only">Close</span>
            <svg
              class="w-3 h-3"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 14 14"
            >
              <path
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"
              />
            </svg>
          </button>
        </div>

        <button
          class="itbkk-btn absolute top-10 right-10 px-4 py-2 bg-blue-500 border-4 border-blue-100 rounded-3xl text-base text-white font-semibold text-center hover:bg-blue-600"
          @click="$router.push({ name: 'task-addmodal' })"
        >
          Add Task
        </button>
      </div>

      <div
        class="relative overflow-x-auto overflow-y-auto shadow-md sm:rounded-lg h-full w-[1350px] mx-auto ml-0 my-auto"
      >
        <!-- table-fixed break-words border border-separate border-spacing-y-2 mb-16 -->
        <table class="md:w-full table-auto text-sm text-left rtl:text-right border-blue-300">
          <thead class="text-lg text-white bg-blue-500 border-b border-blue-300">
            <tr>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Id</th>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Title</th>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Assignees</th>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Status</th>
              <th scope="col" class="px-6 py-3 text-center tracking-wide"></th>
              <th scope="col" class="px-6 py-3 text-center tracking-wide"></th>
            </tr>
          </thead>

          <tbody>
            <tr
              class="itbkk-item bg-blue-100 border-blue-300 hover:bg-blue-200"
              v-for="(task, index) in tasks"
              :key="index"
            >
              <!-- id -->
              <th class="itbkk-id px-6 py-4 text-base text-blue-600 font-medium">
                <!-- {{ task.id }} -->
                {{ index + 1 }}
              </th>

              <!-- Title -->
              <td class="itbkk-title text-left">
                <a href="#" class="px-6 py-4 font-medium text-base text-blue-600 hover:underline">
                  <router-link :to="{ name: 'task-modaldetail', params: { id: task.id } }">
                    {{ task.title.trim() }}
                  </router-link>
                </a>
              </td>

              <td
                class="itbkk-assignees px-6 py-4 text-center text-base text-blue-600 font-medium"
                :class="{ 'text-gray-500 italic': !task.assignees }"
              >
                {{ task.assignees || 'Unassigned' }}
              </td>

              <!-- Status -->

              <td class="itbkk-status">
                <div
                  div
                  class="w-[115px] border-4 border-blue-100 rounded-3xl p-8 px-4 py-2 text-base text-white font-semibold text-center"
                  :class="{
                    'bg-red-400': getStatusText(task.status) === 'No Status',
                    'bg-purple-400': getStatusText(task.status) === 'To Do',
                    'bg-yellow-400': getStatusText(task.status) === 'Doing',
                    'bg-green-500': getStatusText(task.status) === 'Done'
                  }"
                >
                  {{ getStatusText(task.status) }}
                </div>
              </td>
              <td>
                <button onclick="">
                  <img src="/image/ico/edit-3-svgrepo-com.svg" class="h-8 li-3 w-36 mt-1" />
                </button>
              </td>
              <td>
                <button
                  @click="$router.push({ name: 'task-deletemodal', params: { id: task.id } })"
                >
                  <router-link :to="{ name: 'task-deletemodal', params: { id: task.id } }">
                    <img src="/image/ico/delete-svgrepo-com.svg" class="h-7 w-36 mt-2" />
                  </router-link>
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-if="!tasks.length">
          <p
            class="text-center text-gray-500 text-base font-medium bg-white h-10 items-center flex justify-center"
          >
            no tasks
          </p>
        </div>
      </div>

      <router-view />
      <router-view name="addmodal" />
      <router-view name="deletemodal" :id="tasks.id" />
    </div>
  </div>
</template>
