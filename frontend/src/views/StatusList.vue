<script setup>
import { onMounted, computed } from 'vue'
import { useStatusStore } from '@/stores/StatusStore'
import { useCurrentBoardStore } from '@/stores/BoardStore'
// เรียกใช้ StatusStore
const statusStore = useStatusStore()

// เรียกใช้ lifecycle hook เพื่อดึงข้อมูล
onMounted(() => {
  statusStore.fetchStatuses()
})
const currentBoardStore = useCurrentBoardStore()
const boardId = computed(() => currentBoardStore.currentBoardId)
const boardName = computed(() => currentBoardStore.currentBoardName)
</script>

<template>
  <div>
    <div class="bg-blue-500 text-white p-4 flex justify-between items-center">
      <button
        @click="$router.push(`/boards/${boardId}/tasks`)"
        class="itbkk-back-button px-4 py-2 bg-blue-500 border-4 border-blue-100 rounded-3xl text-base text-white font-semibold hover:bg-blue-600"
      >
        &lt; Status
      </button>
      <h2 class="itbkk-fullname font-bold">Welcome, {{ boardName }}!</h2>
    </div>

    <div class="p-10 w-full">
      <div class="itbkk-us3 w-full">
        <div class="flex justify-between items-center mb-4">
          <h1
            class="mb-4 text-3xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r to-emerald-400 from-sky-400"
          >
            {{ boardName }}
          </h1>
          <div class="flex space-x-4">
            <button
              class="itbkk-button-add px-4 py-2 bg-blue-500 border-4 border-blue-100 rounded-3xl text-base text-white font-semibold text-center hover:bg-blue-600"
              @click="$router.push({ name: 'status-addmodal' })"
            >
              Add Status
            </button>
          </div>
        </div>

        <div
          class="overflow-x-auto overflow-y-auto shadow-md sm:rounded-lg mx-auto my-auto right-22"
        >
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
                v-for="(status, index) in statusStore.statuses"
                :key="index"
              >
                <!-- id -->
                <th class="itbkk-id px-6 py-4 text-base text-blue-600 font-medium">
                  {{ index + 1 }}
                </th>

                <!-- Title -->
                <td class="itbkk-status-name text-left whitespace-normal">
                  <a href="#" class="font-medisum text-base text-blue-600 hover:underline">
                    {{ status.name }}
                  </a>
                </td>

                <td
                  class="itbkk-status-description px-6 py-4 text-center text-base text-blue-600 font-medium"
                  :class="{ 'text-gray-500 italic': !status.description }"
                >
                  {{ status.description || 'No description is provided.' }}
                </td>

                <td class="flex">
                  <button
                    id="itbkk-button-edit"
                    v-if="status.name !== 'No Status' && status.name !== 'Done'"
                    class="itbkk-button-edit"
                    @click="$router.push({ name: 'status-editmodal', params: { id: status.id } })"
                  >
                    <router-link :to="{ name: 'status-editmodal', params: { id: status.id } }">
                      <img src="/image/ico/edit-3-svgrepo-com.svg" class="h-8 li-3 w-36 mt-3" />
                    </router-link>
                  </button>
                  <button
                    id="itbkk-button-delete"
                    v-if="status.name !== 'No Status' && status.name !== 'Done'"
                    class="itbkk-button-delete"
                    @click="$router.push({ name: 'status-deletemodal', params: { id: status.id } })"
                  >
                    <router-link :to="{ name: 'status-deletemodal', params: { id: status.id } }">
                      <img src="/image/ico/delete-svgrepo-com.svg" class="h-7 w-36 mt-3.5" />
                    </router-link>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>

          <div v-if="!statusStore.statuses.length">
            <p
              class="text-center text-gray-500 text-base font-medium bg-white h-10 items-center flex justify-center"
            >
              no statuses
            </p>
          </div>
        </div>
        <router-view />
        <router-view name="addmodal" />
        <router-view name="deletemodal" :id="statusStore.statuses.id" />
        <router-view name="editemodal" :id="statusStore.statuses.id" />
      </div>
    </div>
  </div>
</template>

<style scoped>
table td,
table th {
  word-break: break-word;
}
</style>
