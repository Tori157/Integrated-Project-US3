<script setup>
import { onMounted, ref } from 'vue'
// import { onMounted, ref, onUnmounted } from 'vue'
// import { useRouter } from 'vue-router'
// const router = useRouter()

const statuses = ref([])
const BASE_URL = import.meta.env.VITE_BASE_URL

async function fetchData() {
  const response = await fetch(BASE_URL + `/v2/statuses`)

  const data = await response.json()
  console.log(data)
  return data
}
onMounted(async () => {
  const data = await fetchData()
  statuses.value = data
  console.log(statuses.value.length)
})

console.log(statuses)

// function formatStatusName(name) {
//   // ถ้าชื่อทุกตัวเป็นตัวพิมพ์เล็กทั้งหมด ให้คืนค่าเป็นชื่อเดิม
//   if (name === name.toLowerCase()) {
//     return name.replace(/_/g, ' ')
//   }

//   // ทำตัวพิมพ์ใหญ่เฉพาะตัวอักษรต้นคำ
//   const formattedName = name
//     .replace(/_/g, ' ')
//     .toLowerCase()
//     .replace(/(?:^|\s)\S/g, (a) => a.toUpperCase())

//   // ตัดช่องว่างและเครื่องหมาย _ ออก
//   return formattedName.replace(/_/g, ' ').trim()
// }
</script>

<template>
  <div class="p-10 w-full">
    <div class="itbkk-us3 w-full">
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
          <button
            class="itbkk-manage-status top-10 right-20 absolute px-4 py-2 bg-blue-500 border-4 border-blue-100 rounded-3xl text-base text-white font-semibold text-center hover:bg-blue-600"
            @click="$router.push('/task')"
          >
            Home View
          </button>
        </div>
      </div>

      <div class="overflow-x-auto overflow-y-auto shadow-md sm:rounded-lg mx-auto my-auto right-22">
        <table class="w-full text-sm text-left rtl:text-right border-blue-300">
          <thead class="text-lg text-white bg-blue-500 border-b border-blue-300">
            <th class="text-center py-3 tracking-wide">Id</th>
            <th class="text-center tracking-wide">Name</th>
            <th class="text-center tracking-wide">Description</th>
            <th class="text-center tracking-wide">Action</th>
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
              <td class="itbkk-status-name text-left whitespace-normal">
                <a href="#" class="font-medisum text-base text-blue-600 hover:underline">
                  {{ statuses.name }}
                </a>
              </td>

              <td
                class="itbkk-status-description px-6 py-4 text-center text-base text-blue-600 font-medium"
                :class="{ 'text-gray-500 italic': !statuses.description }"
              >
                {{ statuses.description || 'No description is provided.' }}
              </td>

              <td class="flex">
                <button
                  id="itbkk-button-edit"
                  v-if="statuses.name !== 'No Status' && statuses.name !== 'Done'"
                  class="itbkk-button-edit"
                  @click="$router.push({ name: 'status-editmodal', params: { id: statuses.id } })"
                >
                  <router-link :to="{ name: 'status-editmodal', params: { id: statuses.id } }">
                    <img src="/image/ico/edit-3-svgrepo-com.svg" class="h-8 li-3 w-36 mt-3" />
                  </router-link>
                </button>
                <button
                  id="itbkk-button-delete"
                  v-if="statuses.name !== 'No Status' && statuses.name !== 'Done'"
                  class="itbkk-button-delete"
                  @click="$router.push({ name: 'status-deletemodal', params: { id: statuses.id } })"
                >
                  <router-link :to="{ name: 'status-deletemodal', params: { id: statuses.id } }">
                    <img src="/image/ico/delete-svgrepo-com.svg" class="h-7 w-36 mt-3.5" />
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

<style scoped>
table td,
table th {
  word-break: break-word;
}
</style>
