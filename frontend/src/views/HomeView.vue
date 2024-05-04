<script setup>
import { onMounted, ref } from 'vue'

const tasks = ref([])
const modalVisible = ref(false)

async function fetchData() {
  const response = await fetch('http://ip23us3.sit.kmutt.ac.th:8080/v1/tasks')
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

function toggleModal() {
  modalVisible.value = !modalVisible.value
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

      <div class="relative overflow-x-auto shadow-md sm:rounded-lg">
        <table class="md:w-full table-auto text-sm text-left rtl:text-right border-blue-300">
          <thead class="text-lg text-white bg-blue-500 border-b border-blue-300">
            <tr>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Id</th>

              <th scope="col" class="px-6 py-3 text-center tracking-wide">Title</th>

              <th scope="col" class="px-6 py-3 text-center tracking-wide">Assignees</th>

              <th scope="col" class="px-6 py-3 text-center tracking-wide">Status</th>
            </tr>
          </thead>

          <tbody>
            <tr
              class="itbkk-item bg-blue-100 border-blue-300 hover:bg-blue-200"
              v-for="(task, index) in tasks"
              :key="index"
            >
              <!-- <tr
              class="itbkk-item bg-blue-100 border-blue-300 hover:bg-blue-200"
              v-for="(task, index) in tasks"
              :key="index"
            > -->
              <!-- id -->
              <th class="itbkk-id px-6 py-4 text-base text-blue-600 font-medium">
                {{ task.id }}
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
                  class="border-4 border-blue-100 rounded-3xl p-8 px-4 py-2 text-base text-white font-semibold text-center"
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
      <div>
        <button @click="toggleModal"><img src="/image/ico/trash-xmark-svgrepo-com.svg" class="h-10 w-10 mt-4"></button>
      </div>
      <div v-if="modalVisible" class="absolute left-0 right-0 m-auto bg-slate-50 flex h-1/6 w-1/6 shadow-lg rounded-md">
        <div class="flex flex-col gap-10 justify-center mx-8 mt-2">
          <h1 class="font-bold text-xl text-stone-600">Delete a Task</h1>
          <h3 class="itbkk-message text-stone-600">Do you want to delete the task "{{ tasks.title }}"?</h3>
          <div class="flex flex-wrap justify-end">
            <button class="itbkk-button-confirm px-6 py-0.5 text-green-700 rounded-lg">Confirm</button>
            <button @click="toggleModal" class="itbkk-button-cancel px-6 py-0.5 text-red-700 rounded-lg">Cancel</button>
          </div>
        </div>
      </div>
      <router-view />
    </div>
  </div>
</template>