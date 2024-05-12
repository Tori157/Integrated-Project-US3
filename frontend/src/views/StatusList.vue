<script setup>
import { onMounted, ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const statuses = ref([])
const SERVER_URL = import.meta.env.VITE_SERVER_URL

async function fetchData() {
  const response = await fetch(SERVER_URL + `/v2/statuses`)

  const data = await response.json()
  console.log(data)
  return data
}
onMounted(async () => {
  const data = await fetchData()
  statuses.value = data
  console.log(statuses.value.length)
})

const showAlert = ref(false)
const toggleModal = () => {
  showAlert.value = !showAlert.value
}

const handleTaskAdded = () => {
  toggleModal()
}

onMounted(() => {
  window.addEventListener('taskAdded', handleTaskAdded)
})

onUnmounted(() => {
  window.removeEventListener('taskAdded', handleTaskAdded)
})
console.log(statuses)

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
            @click="$router.push({ name: 'status-addmodal' })"
          >
            Add Status
          </button>
        </div>
      </div>
      <h1
        class="mb-4 text-3xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r to-emerald-400 from-sky-400"
      >
        <a href="#" class="hover:underline" @click="router.push('/task')">Home</a> >>> StatusList
      </h1>
      <div
        class="relative overflow-x-auto overflow-y-auto shadow-md sm:rounded-lg h-full w-[1600px] mx-auto my-auto right-22"
      >
        <table class="md:w-full table-auto text-sm text-left rtl:text-right border-blue-300">
          <thead class="text-lg text-white bg-blue-500 border-b border-blue-300">
            <tr>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Id</th>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Status</th>
              <th scope="col" class="px-6 py-3 text-center tracking-wide">Description</th>
              <th scope="col" class="px-6 py-3 text-center tracking-wide"></th>
              <th scope="col" class="px-0 py-0 text-center tracking-wide"></th>
            </tr>
          </thead>

          <tbody>
            <tr
              class="itbkk-item bg-blue-100 border-blue-300 hover:bg-blue-200"
              v-for="(statuses, index) in statuses"
              :key="index"
            >
              <!-- id -->
              <th class="itbkk-id px-6 py-4 text-base text-blue-600 font-medium">
                <!-- {{ task.id }} -->
                {{ index + 1 }}
              </th>

              <!-- Title -->
              <td class="itbkk-title text-left whitespace-normal">
                <a href="#" class="px-6 py-4 font-medisum text-base text-blue-600 hover:underline">
                  {{ formatStatusName(statuses.name) }}
                </a>
              </td>

              <td
                class="itbkk-assignees px-6 py-4 text-center text-base text-blue-600 font-medium"
                :class="{ 'text-gray-500 italic': !statuses.description }"
              >
                {{ statuses.description || 'No Description Unassigned' }}
              </td>

              <td>
                <button
                  v-if="statuses.name !== 'NO_STATUS'"
                  class="itbkk-button-edite"
                  @click="$router.push({ name: 'status-editmodal', params: { id: statuses.id } })"
                >
                  <router-link :to="{ name: 'status-editmodal', params: { id: statuses.id } }">
                    <img src="/image/ico/edit-3-svgrepo-com.svg" class="h-8 li-3 w-36 mt-1" />
                  </router-link>
                </button>
              </td>
              <td>
                <button
                  v-if="statuses.name !== 'NO_STATUS'"
                  class="itbkk-button-action"
                  @click="$router.push({ name: 'status-deletemodal', params: { id: statuses.id } })"
                >
                  <router-link :to="{ name: 'status-deletemodal', params: { id: statuses.id } }">
                    <img src="/image/ico/delete-svgrepo-com.svg" class="h-7 w-36 mt-1.5" />
                  </router-link>
                </button>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-if="!statuses.length">
          <p
            class="text-center text-gray-500 text-base font-medium bg-white h-10 items-center flex justify-center"
          >
            no statuses
          </p>
        </div>
      </div>
      <router-view />
      <router-view name="addmodal" />
      <router-view name="deletemodal" :id="statuses.id" />
      <router-view name="editemodal" :id="statuses.id" />
    </div>
  </div>
</template>
