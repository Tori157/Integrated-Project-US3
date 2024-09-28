<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStatusStore } from '@/stores/StatusStore' // Adjust the path as necessary
import { showAlert, showAlert2 } from '@/utils/toast'
import { useCurrentBoardStore } from '@/stores/BoardStore'

const name = ref('')
const description = ref('')
const router = useRouter()
const statusStore = useStatusStore()

const currentBoardStore = useCurrentBoardStore()
const boardId = computed(() => currentBoardStore.currentBoardId)

const saveStatus = async () => {
  console.log('Saving status...')
  console.log('Name:', name.value)
  console.log('Description:', description.value)
  console.log('Board ID:', boardId.value)

  const statusData = {
    name: name.value.trim(),
    description: description.value.trim()
  }
  console.log('Payload:', statusData)

  if (!boardId.value) {
    showAlert('Invalid board ID.', 'rgb(251 146 60)')
    return
  }

  try {
    // ลบ boardId ออกจากพารามิเตอร์
    await statusStore.addStatus(statusData)
    router.push(`/boards/${boardId.value}/status`)
    showAlert('The status has been added', 'rgb(34 197 94)')
  } catch (error) {
    if (error.response && error.response.data && error.response.data.message.includes('unique')) {
      console.error('Failed to save Status:', error.response.data.message)
      showAlert('Status name must be unique, please choose another name.', 'rgb(251 146 60)')
    } else {
      console.error('Error saving task:', error)
      showAlert('An error has occurred, Status Cant Add.', 'rgb(251 146 60)')
    }
    router.push(`/boards/${boardId.value}/status`)
  }
}

const maxLengths = {
  name: 50,
  description: 200
}

const checkMaxLength = (field, value) => {
  if (value.length >= maxLengths[field]) {
    showAlert2(`Your text in ${field} is at maximum length`, 'rgb(251 146 60)')
  }
}
</script>

<template>
  <div
    class="itbkk-modal-status fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50"
  >
    <div class="bg-blue-100 rounded-lg p-8 max-w-3xl w-full">
      <h2 class="text-rose-400 text-xl font-bold mb-2 text-center text-20 text-black">
        Add Status
      </h2>

      <form @submit.prevent="saveStatus">
        <div class="mb-6">
          <label for="status-name" class="text-rose-400 block text-sm font-medium text-gray-700">
            Name
          </label>
          <input
            type="text"
            id="itbkk-status-name"
            v-model.trim="name"
            class="itbkk-status-name bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            @input="checkMaxLength('name', $event.target.value)"
            maxlength="50"
            required
          />
        </div>
        <div class="mb-4">
          <label
            for="status-description"
            class="text-rose-400 block text-sm font-medium text-gray-700"
          >
            Description
          </label>
          <textarea
            id="status-description"
            v-model.trim="description"
            class="itbkk-status-description bg-white text-blue-600 mt-1 block h-40 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            @input="checkMaxLength('description', $event.target.value)"
            maxlength="200"
            required
          ></textarea>
        </div>
        <div class="flex mt-5 justify-center">
          <button
            id="itbkk-button-confirm"
            type="submit"
            :disabled="name.trim().length === 0"
            class="itbkk-button-confirm"
            :class="[
              'border-4',
              'border-white',
              'rounded-3xl',
              'mx-5',
              'p-8',
              'px-7',
              'py-2',
              'text-base',
              'text-white',
              'font-semibold',
              'text-center',
              name.trim().length === 0 ? 'bg-gray-400' : 'bg-green-400',
              name.trim().length === 0 ? 'disabled' : ''
            ]"
          >
            Save
          </button>

          <button
            id="itbkk-button-cancel"
            type="button"
            @click="() => router.push(`/boards/${boardId.value}/status`)"
            class="itbkk-button-cancel bg-red-400 border-4 border-white rounded-3xl mx-5 p-8 px-6 py-2 text-base text-white font-semibold text-center"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
