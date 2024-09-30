<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showAlert, showAlert2 } from '@/utils/toast.js'
import { useStatusStore } from '@/stores/StatusStore'
import { useTaskStore } from '@/stores/TaskStore'

const router = useRouter()
const route = useRoute()

const statusesStore = useStatusStore()
const TaskStore = useTaskStore()
const statuses = ref([])
const tasksCount = ref(0)
const statusId = parseInt(route.params.id)
const statusesname = ref('')
const targetStatusId = ref(null)
const {
  params: { boardId }
} = useRoute()

onMounted(async () => {
  await statusesStore.fetchStatuses()
  statuses.value = statusesStore.statuses

  const status = statuses.value.find((status) => status.id === statusId)
  if (status) {
    statusesname.value = status.name
  }

  await TaskStore.fetchTasks()
  const tasksInStatus = TaskStore.tasks.filter((task) => task.status.id === statusId)
  tasksCount.value = tasksInStatus.length
})

async function deleteStatus(statusId, targetStatusId) {
  try {
    if (tasksCount.value > 0) {
      if (!targetStatusId) {
        showAlert2('Please select a status to transfer tasks.', 'rgb(251 146 60)')
        return
      }
      await statusesStore.transferTasks(statusId, targetStatusId)
      showAlert('The status has been deleted.', 'rgb(244 63 94)')
      router.push(`/boards/${boardId}/status`)
      return
    }

    await statusesStore.deleteStatus(statusId)
    showAlert('The status has been deleted.', 'rgb(244 63 94)')
    router.push(`/boards/${boardId}/status`)
  } catch (error) {
    console.error('Error:', error)
    showAlert('An error has occurred, the status does not exist.', 'rgb(251 146 60)')
  }
}

function cancel() {
  router.push(`/boards/${boardId}/status`)
}
</script>

<template>
  <div
    id="modelConfirm"
    class="fixed z-50 inset-0 bg-gray-900 bg-opacity-60 overflow-y-auto h-full w-full px-4 flex justify-center items-center bg-blue-100"
  >
    <div class="relative mx-auto shadow-xl rounded-md max-w-md bg-blue-100 mb-52">
      <div class="mt-5 mb-5 p-6 pt-0 text-center bg-blue-100">
        <img src="/image/ico/alert-2-svgrepo-com.svg" class="w-20 h-20 mx-auto" />

        <template v-if="tasksCount > 0">
          <h3
            class="itbkk-message text-xl font-semi text-gray-500 mt-5 mb-6 whitespace-normal break-words"
          >
            There are {{ tasksCount }} tasks in {{ statusesname }} status. In order to delete this
            status, the system must transfer tasks in this status to existing status. Transfer tasks
            to
          </h3>

          <select
            v-model="targetStatusId"
            class="itbkk-select mb-3 bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value="" disabled>Transfer to status</option>
            <option
              v-for="status in statuses"
              :key="status.id"
              :value="status.id"
              :disabled="status.id === statusId"
            >
              {{ status.name }}
            </option>
          </select>
        </template>

        <template v-else>
          <h3
            class="itbkk-message text-xl font-semi text-gray-500 mt-5 mb-6 whitespace-normal break-words"
          >
            Do you want to delete {{ statusesname }} status?
          </h3>
        </template>

        <!-- ปุ่มยืนยันการลบ -->
        <button
          @click="deleteStatus(statusId, targetStatusId)"
          class="itbkk-button-confirm mr-5 text-white bg-red-600 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-base inline-flex items-center px-3 py-2.5 text-center mr-2"
        >
          Confirm
        </button>

        <!-- ปุ่มยกเลิก -->
        <button
          @click="cancel"
          class="itbkk-button-cancel ml-5 text-gray-900 bg-white hover:bg-gray-100 focus:ring-4 focus:ring-cyan-200 border border-gray-200 font-medium inline-flex items-center rounded-lg text-base px-3 py-2.5 text-center"
          data-modal-toggle="delete-user-modal"
        >
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>
