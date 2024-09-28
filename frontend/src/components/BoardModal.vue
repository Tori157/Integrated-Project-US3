<script setup>
import { ref, defineEmits, computed } from 'vue'

// Define the events that this component can emit
const emit = defineEmits(['submit'])

const isVisible = ref(false)
const boardName = ref('')
const currentBoard = ref(null)

// Computed property to determine if the modal is in edit mode
const isEditMode = computed(() => currentBoard.value !== null)

const open = (board) => {
  if (board) {
    isVisible.value = true
    currentBoard.value = board
    boardName.value = board.name
  } else {
    isVisible.value = true
    currentBoard.value = null
    boardName.value = ''
  }
}

const closeModal = () => {
  isVisible.value = false
  boardName.value = ''
  currentBoard.value = null
}

const submitForm = () => {
  if (currentBoard.value) {
    // Editing existing board
    emit('submit', { ...currentBoard.value, name: boardName.value })
  } else {
    // Creating new board
    emit('submit', { name: boardName.value })
  }
  closeModal()
}

// Expose the open method to parent via ref
defineExpose({
  open
})
</script>

<!-- src/components/BoardCreate.vue -->
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
            placeholder="Enter board name"
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
