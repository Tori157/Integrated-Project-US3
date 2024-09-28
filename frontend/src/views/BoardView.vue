<script setup>
import { ref, onMounted } from 'vue'
import { useJwt } from '@vueuse/integrations/useJwt'
import { useBoardStore } from '@/stores/BoardStore'
import { useTaskStore } from '@/stores/TaskStore'
import { useStatusStore } from '@/stores/StatusStore'
import { useRouter } from 'vue-router'
import { useCurrentBoardStore } from '@/stores/BoardStore'
import BoardModal from '@/components/BoardModal.vue'

const name = ref('')
const access_token = ref(null)
const boardStore = useBoardStore()
const taskStore = useTaskStore()
const StatusStore = useStatusStore()
const router = useRouter()
const dropdownIndex = ref(null)
const currentBoardStore = useCurrentBoardStore()
const toggleDropdown = (index) => {
  dropdownIndex.value = dropdownIndex.value === index ? null : index
}

// const manageStatus = (boardId) => {
//   console.log(`Manage status for board ${boardId}`)
// }

// const deleteBoard = async (boardId) => {
//   await boardStore.deleteBoard(boardId)
//   console.log(`Board ${boardId} deleted`)
// }

// const openBoard = (boardId) => {
//   console.log(`Opening board ${boardId}`)
// }

const modal = ref(null)

const openModal = (board = null) => {
  if (modal.value) {
    modal.value.open(board)
  }
}

const handleModalSubmit = async (boardData) => {
  try {
    if (boardData.id) {
      // Editing existing board
      await boardStore.updateBoard(boardData)
      console.log(`Board "${boardData.name}" updated successfully.`)
    } else {
      // Creating new board
      await boardStore.addBoard(boardData)
      console.log(`Board "${boardData.name}" created successfully.`)
    }
  } catch (error) {
    console.error('Error processing board:', error)
    alert('An error occurred while processing your request.')
  }
}

onMounted(async () => {
  const accessTokenMatch = document.cookie.match(/access_token=([^;]*)/)
  if (accessTokenMatch) {
    access_token.value = accessTokenMatch[1]
  }

  const { payload } = useJwt(access_token.value)
  if (payload.value && payload.value.name) {
    name.value = payload.value.name
  }

  await boardStore.fetchBoards()
})

const openBoard = async (board) => {
  if (!board || !board.id) {
    console.error('Invalid board or missing ID')
    return
  }

  await taskStore.fetchTasks(board.id)
  await StatusStore.fetchStatuses(board.id)

  currentBoardStore.setCurrentBoard(board.id, board.name)

  router.push({ name: 'tasks', params: { boardId: board.id } })
}
</script>
<template>
  <div class="flex flex-col h-screen">
    <!-- Header Section -->
    <header class="flex justify-between items-center p-4 bg-blue-500 shadow">
      <div class="flex items-center">
        <span class="text-lg font-bold text-black">IT-Bangmod Kradan Kanban</span>
      </div>
      <div class="flex items-center">
        <span class="itbkk-fullname mr-4 font-semibold text-black">{{ name }}</span>
      </div>
    </header>

    <!-- Main Content Section -->
    <main class="flex-grow p-6 bg-gray-50">
      <div class="max-w-4xl mx-auto">
        <!-- Board List Heading -->
        <h1 class="text-2xl font-semibold text-gray-700 mb-4">Board List</h1>

        <!-- Board Creation Button -->
        <div class="text-right mb-4">
          <button
            class="itbkk-button-create bg-gray-300 text-gray-700 font-semibold py-2 px-4 rounded-md hover:bg-gray-400"
            @click="openModal()"
          >
            Create personal board
          </button>
        </div>

        <!-- Board List -->
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          <!-- Loop through boards -->
          <div
            v-for="(board, index) in boardStore.boards"
            :key="board.id"
            class="bg-white p-4 rounded-lg shadow-md"
          >
            <div class="flex justify-between items-center">
              <div>
                <!-- <h2 class="itbkk-board-name text-lg font-semibold">
                  {{ board.name.length > 12 ? board.name.slice(0, 12) + '...' : board.name }}
                </h2> -->
                <h2 class="itbkk-board-name text-lg font-semibold">
                  {{ board.name }}
                </h2>
              </div>
              <!-- Dropdown Menu -->
              <div class="relative">
                <button class="focus:outline-none" @click="toggleDropdown(index)">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-6 w-6"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M6 12h.01M12 12h.01M18 12h.01"
                    />
                  </svg>
                </button>

                <!-- Dropdown content -->
                <!-- <div
                  v-if="dropdownIndex === index"
                  class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg z-10"
                >
                  <ul class="py-1">
                    <li>
                      <a
                        href="#"
                        class="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                        @click.prevent="manageStatus(board.id)"
                        >Manage Status</a
                      >
                    </li>
                    <li>
                      <a
                        href="#"
                        class="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                        @click.prevent="openModal(board)"
                        >Edit</a
                      >
                    </li>
                    <li>
                      <a
                        href="#"
                        class="block px-4 py-2 text-gray-700 hover:bg-gray-100"
                        @click.prevent="deleteBoard(board.id)"
                        >Delete</a
                      >
                    </li>
                  </ul>
                </div> -->
              </div>
            </div>

            <!-- Open Board Button -->
            <button
              class="mt-4 bg-blue-500 text-white py-2 px-4 rounded-md w-full hover:bg-blue-600"
              @click="openBoard(board)"
            >
              Open Board
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- Modal for creating/editing boards -->
    <BoardModal ref="modal" @submit="handleModalSubmit" />
  </div>
</template>

<style scoped>
.table {
  width: 100%;
  table-layout: fixed;
}
</style>
