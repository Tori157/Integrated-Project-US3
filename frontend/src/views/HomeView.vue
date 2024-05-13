<script setup>
// import { onMounted, ref } from 'vue'
import { onMounted, ref, onUnmounted } from 'vue'

const tasks = ref([])
const SERVER_URL = import.meta.env.VITE_SERVER_URL

async function fetchData() {
  const response = await fetch(SERVER_URL + `/v2/tasks`)

  const data = await response.json()
  console.log(data)
  return data
}
onMounted(async () => {
  const data = await fetchData()
  tasks.value = data
  console.log(tasks.value.length)
})

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
        <div class="">
          <button
            class="itbkk-button-add top-10 right-60 absolute px-4 py-2 bg-blue-500 border-4 border-blue-100 rounded-3xl text-base text-white font-semibold text-center hover:bg-blue-600"
            @click="$router.push({ name: 'task-addmodal' })"
          >
            Add Task
          </button>
          <button
            class="itbkk-manage-status top-10 right-20 absolute px-4 py-2 bg-blue-500 border-4 border-blue-100 rounded-3xl text-base text-white font-semibold text-center hover:bg-blue-600"
            @click="$router.push({ name: 'statuslist' })"
          >
            Manage Status
          </button>
        </div>
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
                  class="w-[115px] border-4 border-blue-100 bg-blue-300 rounded-3xl p-8 px-4 py-2 text-base text-white font-semibold text-center"
                >
                  {{ formatStatusName(task.status.name) }}
                </div>
              </td>
              <td class="flex">
                <div class="flex mt-3 space-x-0">
                  <!-- <div class="itbkk-button-action flex mt-3 space-x-0"> -->
                  <button
                    id="itbkk-button-edite"
                    class="itbkk-button-edite"
                    @click="$router.push({ name: 'task-edite', params: { id: task.id } })"
                  >
                    <router-link :to="{ name: 'task-edite', params: { id: task.id } }">
                      <img
                        src="/image/ico/edit-3-svgrepo-com.svg"
                        class="itbkk-button-edite h-8 li-3 w-36"
                      />
                    </router-link>
                  </button>

                  <button
                    id="itbkk-button-delete"
                    class="itbkk-button-delete"
                    @click="$router.push({ name: 'task-deletemodal', params: { id: task.id } })"
                  >
                    <router-link :to="{ name: 'task-deletemodal', params: { id: task.id } }">
                      <img
                        src="/image/ico/delete-svgrepo-com.svg"
                        class="itbkk-button-delete h-7 w-36 mt-0.5"
                      />
                    </router-link>
                  </button>
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
      <router-view />
      <router-view name="addmodal" />
      <router-view name="deletemodal" :id="tasks.id" />
      <router-view name="editemodal" :id="tasks.id" />
    </div>
  </div>
</template>
