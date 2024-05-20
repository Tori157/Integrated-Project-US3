<script setup>
import { onMounted, ref, onUnmounted, computed } from 'vue'

const tasks = ref([])
const originalTasks = ref([])
const BASE_URL = import.meta.env.VITE_BASE_URL
const statuses = ref([])
const searchStatus = ref('')
const showStatusDropdown = ref(false)
const selectedStatuses = ref([])

async function fetchData() {
  const response = await fetch(BASE_URL + `/v2/tasks`)

  const data = await response.json()
  tasks.value = data
  originalTasks.value = [...data]
  console.log(data)
  return data
}
onMounted(async () => {
  const data = await fetchData()
  tasks.value = data
  originalTasks.value = [...data]
  console.log(tasks.value.length)
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

onMounted(async () => {
  await fetchStatuses()
})

const sortState = ref(0) // Initial state for the sort icon

const toggleSortIcon = () => {
  sortState.value = (sortState.value + 1) % 3 // Cycle through 0, 1, 2

  if (sortState.value === 1) {
    // Sort tasks by title A-Z
    tasks.value.sort((a, b) => a.statusName.localeCompare(b.statusName))
  } else if (sortState.value === 2) {
    // Sort tasks by title Z-A
    tasks.value.sort((a, b) => b.statusName.localeCompare(a.statusName))
  } else {
    tasks.value = [...originalTasks.value]
  }
}

const fetchStatuses = async () => {
  try {
    const response = await fetch(BASE_URL + '/v2/statuses')
    if (response.ok) {
      const data = await response.json()
      statuses.value = data
    } else {
      console.error('Failed to fetch statuses:', response.statusText)
    }
  } catch (error) {
    console.error('Error fetching statuses:', error)
  }
}

const redirectToFilteredTasks = async () => {
  if (searchStatus.value.trim() !== '') {
    const selectedStatusIds = selectedStatuses.value.map((status) => status.id)
    const filterStatus = encodeURIComponent(selectedStatusIds.join(','))
    const filteredTasksUrl = `${BASE_URL}/v2/tasks?statusId=${filterStatus}`
    // console.log(filteredTasksUrl)

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
    await fetchData()
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
  await fetchData()
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
  <div class="p-10 w-full bg-gray-800">
    <div class="itbkk-us3 w-full">
      <div class="flex justify-between items-center mb-4">
        <h1
          class="mb-4 text-3xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r to-emerald-400 from-sky-400"
        >
          IT-Bangmod Kradan Kanban
        </h1>
      </div>

      <div class="flex justify-between items-center mx-auto">
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

      <div class="itbkk-status-filter mb-5 flex flex-row">
        <input
          v-model="searchStatus"
          @focus="showStatusDropdown = true"
          readonly
          class="bg-white text-transparent px-2 mt-1 block h-12 w-[360px] border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          placeholder="Filter by status(es)"
        />
        <div
          class="absolute overflow-auto no-scrollbar w-[360px] flex items-center px-1 mt-2"
          @click="showStatusDropdown = true"
        >
          <span
            v-for="(status, index) in selectedStatuses"
            :key="index"
            class="itbkk-filter-item select-none pointer-events-none px-2 mr-1.5 py-1 bg-gray-300 hover:bg-gray-400 rounded-full flex items-center text-black text-base text-nowrap"
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
          class="absolute z-10 bg-white mt-16 w-[360px] rounded-md shadow-sm"
        >
          <li
            v-for="status in filteredStatuses"
            :key="status.id"
            @click="selectStatus(status)"
            :class="{ 'cursor-not-allowed': isSelected(status) }"
            class="px-2 py-1 cursor-pointer text-black text-base rounded-md hover:bg-blue-200"
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

      <div class="overflow-x-auto overflow-y-auto shadow-md sm:rounded-lg mx-auto my-auto right-22">
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
                  :src="
                    sortState === 0
                      ? '/image/ico/default-alphabetical-sorting-svgrepo-com.svg'
                      : sortState === 1
                        ? '/image/ico/alphabetical-sorting-svgrepo-com.svg'
                        : '/image/ico/alphabetical-sorting-2-svgrepo-com.svg'
                  "
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
                  {{ task.statusName }}
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
