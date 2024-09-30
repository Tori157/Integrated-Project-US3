<script setup>
import { ref, defineEmits, computed, onMounted } from 'vue'
import { useBoardStore } from '@/stores/BoardStore'
import { useRouter } from 'vue-router'
import { useJwt } from '@vueuse/integrations/useJwt'
import { showAlert2 } from '@/utils/toast.js'

const emit = defineEmits(['submit'])
const router = useRouter()
const access_token = ref(null)
const isVisible = ref(false)
const boardName = ref('')
const currentBoard = ref(null)
const boardStore = useBoardStore()
const name = ref('')

const isEditMode = computed(() => currentBoard.value !== null)

// Function to open modal
const open = (board) => {
  isVisible.value = true
  currentBoard.value = board

  if (board) {
    boardName.value = board.name
  } else {
    boardName.value = `${name.value} personal board`
  }
}

// Function to close modal
const closeModal = () => {
  isVisible.value = false
  boardName.value = ''
  currentBoard.value = null
}

// Function to submit form
const submitForm = async () => {
  // Validate board name
  if (!boardName.value || boardName.value.length > 120) {
    showAlert2(
      'Board name must not be empty and should not exceed 120 characters.',
      'rgb(251 146 60)'
    )
    return
  }

  if (currentBoard.value) {
    emit('submit', { ...currentBoard.value, name: boardName.value })
  } else {
    try {
      await boardStore.addBoard({ name: boardName.value })
      closeModal()
    } catch (error) {
      if (error.response && error.response.status === 401) {
        router.push('/login')
      } else {
        console.error('Error creating board:', error)
      }
    }
  }
}

// Fetch the access token and user name on mount
onMounted(() => {
  const token = document.cookie.match(/access_token=([^;]*)/)
  if (token) {
    access_token.value = token[1]
  }
  const { payload } = useJwt(access_token)
  name.value = payload.value.name
})

defineExpose({
  open
})
</script>

<template>
  <div
    v-if="isVisible"
    class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-20"
  >
    <div class="itbkk-modal-new bg-white rounded-lg shadow-lg w-96">
      <div class="flex justify-between items-center p-4 border-b">
        <h2 class="text-xl font-semibold">{{ isEditMode ? 'Edit Board' : 'Create Board' }}</h2>
        <button @click="closeModal" class="text-gray-500 hover:text-gray-700">
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
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      </div>
      <form @submit.prevent="submitForm" class="p-4">
        <div class="mb-4">
          <label for="boardName" class="block text-gray-700 font-medium mb-2">Board Name</label>
          <input
            id="itbkk-board-name"
            v-model="boardName"
            type="text"
            required
            class="itbkk-board-name w-full px-3 py-2 border rounded-md focus:outline-none focus:ring focus:border-blue-300"
            maxlength="120"
          />
        </div>
        <div class="flex justify-end">
          <button
            type="submit"
            class="itbkk-button-ok mr-2 px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
          >
            {{ isEditMode ? 'Update' : 'Create' }}
          </button>
          <button
            type="button"
            @click="closeModal"
            class="itbkk-button-cancel px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
/* Optional: Add any scoped styles here */
</style>
