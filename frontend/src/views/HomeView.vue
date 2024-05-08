<script setup>
// import { onMounted, ref } from 'vue'
import { onMounted, ref, onUnmounted } from 'vue'

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
  console.log(tasks.value.length)
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
// const showAlert = ref(false)
// function toggleModal() {
//   showAlert.value = !showAlert.value
// }

// watch(tasks.value.length, (newTasks, oldTasks) => {
//   if (newTasks.value.length > oldTasks.value.length) {
//     toggleModal()
//     console.log(tasks.value.length)
//   }
// })
const showAlert = ref(false)
const toggleModal = () => {
  showAlert.value = !showAlert.value
}

const handleTaskAdded = () => {
  toggleModal()
}
// Listen for taskAdded event from Add Task component
onMounted(() => {
  // Listen for taskAdded event
  window.addEventListener('taskAdded', handleTaskAdded)
})

onUnmounted(() => {
  // Cleanup event listener
  window.removeEventListener('taskAdded', handleTaskAdded)
})
</script>

<template>
  <div class="p-10 w-screen h-screen bg-gray-800">
    <div class="itbkk-us3 container mx-auto w-full max-w-screen-xl ml-40">
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
          v-if="showAlert"
          class="bg-green-200 mr-40 px-6 py-3 mx-2 my-4 rounded-md text-lg flex items-center mx-auto max-w-lg fixed top-5 right-20 z-50"
        >
          <svg viewBox="0 0 24 24" class="text-green-600 w-5 h-5 sm:w-5 sm:h-5 mr-3">
            <path
              fill="currentColor"
              d="M12,0A12,12,0,1,0,24,12,12.014,12.014,0,0,0,12,0Zm6.927,8.2-6.845,9.289a1.011,1.011,0,0,1-1.43.188L5.764,13.769a1,1,0,1,1,1.25-1.562l4.076,3.261,6.227-8.451A1,1,0,1,1,18.927,8.2Z"
            ></path>
          </svg>
          <span class="text-green-800">The task has been successfully added.</span>
          <button
            @click="toggleModal()"
            type="button"
            class="-mx-1.5 -my-1.5 bg-green-200 text-gray-400 hover:text-gray-700 rounded-full ml-1 focus:ring-1 focus:ring-gray-300 p-1.5 hover:bg-gray-100 inline-flex items-center h-8 w-8"
            data-dismiss-target="#toast-success"
            aria-label="Close"
          >
            <span class="sr-only">Close</span>
            <svg
              class="w-3 h-3 mt-1.5 mb-1.5 ml-1"
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
          class="itbkk-button-add absolute top-10 right-20 px-4 py-2 bg-blue-500 border-4 border-blue-100 rounded-3xl text-base text-white font-semibold text-center hover:bg-blue-600"
          @click="$router.push({ name: 'task-addmodal' })"
        >
          Add Task
        </button>
      </div>

      <div
        class="relative overflow-x-auto overflow-y-auto shadow-md sm:rounded-lg h-full w-[1600px] mx-auto my-auto right-22"
      >
        <!-- table-fixed break-words border border-separate border-spacing-y-2 mb-16 -->
        <table class="md:w-full table-auto text-sm text-left rtl:text-right border-blue-300">
          <thead class="text-lg text-white bg-blue-500 border-b border-blue-300">
            <tr>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Id</th>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Title</th>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Assignees</th>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Status</th>
              <th scope="col" class="px-0 py-0 text-center tracking-wide"></th>
              <th scope="col" class="px-0 py-0 text-center tracking-wide"></th>
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
              <td class="itbkk-title text-left whitespace-normal">
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
                <button
                  class="itbkk-button-edite"
                  @click="$router.push({ name: 'task-edite', params: { id: task.id } })"
                >
                  <router-link :to="{ name: 'task-edite', params: { id: task.id } }">
                    <img src="/image/ico/edit-3-svgrepo-com.svg" class="h-8 li-3 w-36 mt-1" />
                  </router-link>
                </button>
              </td>
              <td>
                <button
                  class="itbkk-button-action"
                  @click="$router.push({ name: 'task-deletemodal', params: { id: task.id } })"
                >
                  <router-link :to="{ name: 'task-deletemodal', params: { id: task.id } }">
                    <img src="/image/ico/delete-svgrepo-com.svg" class="h-7 w-36 mt-1.5" />
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
      <router-view name="editemodal" :id="tasks.id" />
    </div>
  </div>
</template>
