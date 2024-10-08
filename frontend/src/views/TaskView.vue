<script setup>
import { onMounted, ref, onUnmounted, computed } from 'vue'
import { useRoute } from 'vue-router/dist/vue-router'
import { useJwt } from '@vueuse/integrations/useJwt'
import { useTaskStore } from '@/stores/TaskStore'
import { useStatusStore } from '@/stores/StatusStore'
import { useCurrentBoardStore, useBoardStore } from '@/stores/BoardStore'

const {
  params: { boardId }
} = useRoute()
const BASE_URL = import.meta.env.VITE_BASE_URL
const taskStore = useTaskStore()
const statusStore = useStatusStore()

const tasks = ref([])
const originalTasks = ref([])
const statuses = ref([])
const searchStatus = ref('')
const showStatusDropdown = ref(false)
const selectedStatuses = ref([])
const access_token = ref(null)
const name = ref('')

const currentBoardStore = useCurrentBoardStore()
const boardStore = useBoardStore()

const boardName = computed(() => currentBoardStore.currentBoardName)

// Fetch tasks from TaskStore
async function loadTasks() {
  try {
    await taskStore.fetchTasks(boardId)
    tasks.value = taskStore.tasks
    originalTasks.value = [...taskStore.tasks]
  } catch (error) {
    console.error('Error fetching tasks:', error)
  }
}

// Fetch statuses from StatusStore
const fetchStatuses = async () => {
  try {
    await statusStore.fetchStatuses(boardId)
    statuses.value = statusStore.statuses
  } catch (error) {
    console.error('Error fetching statuses:', error)
  }
}

// Initial setup on mounted
onMounted(async () => {
  const board = await boardStore.getBoardById(boardId)
  currentBoardStore.setCurrentBoard(boardId, board?.name ?? '')
  const accessToken = document.cookie.match(/access_token=([^;]*)/)
  if (accessToken) {
    access_token.value = accessToken[1]
  }
  const { payload } = useJwt(access_token)
  name.value = payload.value.name

  await loadTasks()
  await fetchStatuses()
})

// Event handling for task addition
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

// Sorting and status filter logic (unchanged)
const sortState = ref(0)
const toggleSortIcon = () => {
  sortState.value = (sortState.value + 1) % 3
  if (sortState.value === 1) {
    tasks.value.sort((a, b) => a.status.name.localeCompare(b.status.name))
  } else if (sortState.value === 2) {
    tasks.value.sort((a, b) => b.status.name.localeCompare(a.status.name))
  } else {
    tasks.value = [...originalTasks.value]
  }
}

const redirectToFilteredTasks = async () => {
  if (selectedStatuses.value && selectedStatuses.value.length > 0) {
    const selectedStatusNames = selectedStatuses.value.map((status) => status.name)
    const filterStatus = encodeURIComponent(selectedStatusNames.join(','))
    const filteredTasksUrl = `${BASE_URL}/v2/tasks?filterStatuses=${filterStatus}`

    try {
      const response = await fetch(filteredTasksUrl)
      if (response.ok) {
        const data = await response.json()
        tasks.value = data
        originalTasks.value = [...data]
      } else {
        console.error('Failed to fetch filtered tasks:', response.statusText)
      }
    } catch (error) {
      console.error('Error fetching filtered tasks:', error)
    }
  } else {
    // Fetch all tasks if no status is selected
    try {
      await loadTasks()
    } catch (error) {
      console.error('Error fetching all tasks:', error)
    }
  }
}

const selectStatus = (status) => {
  if (!selectedStatuses.value.includes(status)) {
    selectedStatuses.value.push(status)
    searchStatus.value += `${searchStatus.value ? ' | ' : ''}${status.name}`
    showStatusDropdown.value = false
    redirectToFilteredTasks()
  }
}

const removeSelectedStatus = (index) => {
  selectedStatuses.value.splice(index, 1)
  const selectedStatusNames = selectedStatuses.value.map((status) => status.name)
  searchStatus.value = selectedStatusNames.join(' | ')
  redirectToFilteredTasks()
}

const isSelected = (status) => {
  return selectedStatuses.value.includes(status)
}
const handleDocumentClick = (event) => {
  const isClickInsideDropdown = event.target.closest('.itbkk-status-filter') !== null
  if (!isClickInsideDropdown) {
    showStatusDropdown.value = false
  }
}

onMounted(() => {
  document.body.addEventListener('click', handleDocumentClick)
})

onUnmounted(() => {
  document.body.removeEventListener('click', handleDocumentClick)
})

const clearFilter = async () => {
  selectedStatuses.value = []
  searchStatus.value = ''
  await loadTasks()
}

const showClearFilterButton = computed(() => {
  return selectedStatuses.value.length > 0
})

const filteredStatuses = computed(() => {
  return statuses.value.filter((status) => !selectedStatuses.value.includes(status))
})
</script>

<style>
.table {
  width: 100%;
  table-layout: fixed;
}
</style>

<template>
  <div>
    <div class="bg-blue-500 text-white p-4 flex justify-between items-center">
      <button
        @click="$router.push('/boards')"
        class="itbkk-back-button px-4 py-2 bg-blue-500 border-4 border-blue-100 rounded-3xl text-base text-white font-semibold hover:bg-blue-600"
      >
        &lt; Task
      </button>
      <h2 class="itbkk-fullname font-bold">Welcome, {{ name }}!</h2>
    </div>
    <div class="p-10 w-full">
      <div class="itbkk-us3 w-full">
        <div class="flex justify-between items-center mb-4">
          <h1
            class="mb-4 text-3xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r to-emerald-400 from-sky-400"
          >
            {{ boardName }}
          </h1>

          <div class="flex justify-between items-center">
            <button
              class="itbkk-button-add px-4 py-2 mr-2 bg-blue-500 border-4 border-blue-100 rounded-3xl text-base text-white font-semibold text-center hover:bg-blue-600"
              @click="$router.push({ name: 'task-addmodal' })"
            >
              Add Task
            </button>

            <button
              class="itbkk-manage-status px-4 py-2 bg-blue-500 border-4 border-blue-100 rounded-3xl text-base text-white font-semibold text-center hover:bg-blue-600"
              @click="$router.push({ name: 'statuslist', params: { boardId } })"
            >
              Manage Status
            </button>
          </div>
        </div>

        <div class="itbkk-status-filter mb-5 flex flex-row">
          <input
            v-model="searchStatus"
            @focus="showStatusDropdown = true"
            readonly
            class="bg-white text-transparent px-2 mt-1 block h-12 w-[580px] border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            placeholder="Filter by status(es)"
          />
          <div
            class="itbkk-status-choice absolute overflow-auto no-scrollbar w-[580px] flex items-center px-1 mt-2"
            @click="showStatusDropdown = true"
          >
            <span
              v-for="(status, index) in selectedStatuses"
              :key="index"
              class="itbkk-status-choice select-none pointer-events-none px-2 mr-1.5 py-1 bg-gray-300 hover:bg-gray-400 rounded-full flex items-center text-black text-base text-nowrap"
            >
              {{ status.name }}
              <button
                @click="removeSelectedStatus(index)"
                class="itbkk-filter-item-clear pointer-events-auto mb-1 ml-2 text-xl"
              >
                &times;
              </button>
            </span>
          </div>
          <ul
            v-show="showStatusDropdown"
            class="absolute z-10 bg-white mt-16 w-[580px] rounded-md shadow-sm"
          >
            <li
              v-for="status in filteredStatuses"
              :key="status.id"
              @click="selectStatus(status)"
              :class="{ 'cursor-not-allowed': isSelected(status) }"
              class="itbkk-status px-2 py-1 cursor-pointer text-black text-base rounded-md hover:bg-blue-200"
            >
              {{ status.name }}
            </li>
          </ul>
          <button
            v-if="showClearFilterButton"
            @click="clearFilter"
            class="itbkk-filter-clear px-2 my-1 ml-3 mt-2 rounded-md bg-red-500 text-white"
          >
            Clear Filter
          </button>
        </div>

        <div
          class="overflow-x-auto overflow-y-auto shadow-md sm:rounded-lg mx-auto my-auto right-22"
        >
          <table class="md:w-full table-auto text-sm text-left rtl:text-right border-blue-300">
            <thead class="text-lg text-white bg-blue-500 border-b border-blue-300">
              <tr>
                <th scope="col" class="px-6 py-3 text-center tracking-wide">Id</th>
                <th scope="col" class="px-6 py-3 text-center tracking-wide">Title</th>
                <th scope="col" class="px-6 py-3 text-center tracking-wide">Assignees</th>
                <th
                  scope="col"
                  class="px-6 py-3 text-center tracking-wide flex justify-between items-center"
                >
                  <span>Status</span>
                  <img
                    v-if="sortState === 0"
                    src="/image/ico/default-alphabetical-sorting-svgrepo-com.svg"
                    class="itbkk-status-sort h-7 w-10 mt-0.5 cursor-pointer"
                    @click="toggleSortIcon"
                  />
                  <img
                    v-else-if="sortState === 1"
                    src="/image/ico/alphabetical-sorting-svgrepo-com.svg"
                    class="itbkk-status-sort h-7 w-10 mt-0.5 cursor-pointer"
                    @click="toggleSortIcon"
                  />
                  <img
                    v-else
                    src="/image/ico/alphabetical-sorting-2-svgrepo-com.svg"
                    class="itbkk-status-sort h-7 w-10 mt-0.5 cursor-pointer"
                    @click="toggleSortIcon"
                  />
                </th>
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
                  {{ index + 1 }}
                </th>

                <!-- Title -->
                <td class="itbkk-title text-left break-words">
                  <a
                    href="#"
                    class="px-6 py-4 font-medium text-base text-blue-600 hover:underline break-words"
                  >
                    <router-link
                      :to="{ name: 'task-modaldetail', params: { boardId, id: task.id } }"
                    >
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

                <td>
                  <div
                    class="itbkk-status first-letter:w-[115px] border-4 border-blue-100 bg-blue-300 rounded-3xl p-8 px-4 py-2 text-base text-white font-semibold text-center"
                  >
                    {{ task.status.name }}
                  </div>
                </td>
                <td class="flex">
                  <div class="flex mt-3 space-x-0">
                    <!-- <div class="itbkk-button-action flex mt-3 space-x-0"> -->
                    <button
                      id="itbkk-button-edite"
                      class="itbkk-button-edite"
                      @click="
                        $router.push({ name: 'task-edite', params: { boardId, id: task.id } })
                      "
                    >
                      <router-link :to="{ name: 'task-edite', params: { boardId, id: task.id } }">
                        <img
                          src="/image/ico/edit-3-svgrepo-com.svg"
                          class="itbkk-button-edite h-8 li-3 w-36"
                        />
                      </router-link>
                    </button>

                    <button
                      id="itbkk-button-delete"
                      class="itbkk-button-delete"
                      @click="
                        $router.push({ name: 'task-deletemodal', params: { boardId, id: task.id } })
                      "
                    >
                      <router-link
                        :to="{ name: 'task-deletemodal', params: { boardId, id: task.id } }"
                      >
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
  </div>
</template>

<style scoped>
/* Hide scrollbar for Chrome, Safari and Opera */
.no-scrollbar::-webkit-scrollbar {
  display: none;
}

/* Hide scrollbar for IE, Edge and Firefox */
.no-scrollbar {
  -ms-overflow-style: none; /* IE and Edge */
  scrollbar-width: none; /* Firefox */
}
</style>
