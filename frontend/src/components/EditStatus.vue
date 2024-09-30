<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showAlert, showAlert2 } from '@/utils/toast.js'

import { useStatusStore } from '@/stores/StatusStore' // Import StatusStore

const {
  params: { boardId }
} = useRoute()
const route = useRoute()
const router = useRouter()

// Store setup

const statusStore = useStatusStore() // Initialize StatusStore

const statuses = ref({ name: '', description: '' })
const originalStatus = ref({ name: '', description: '' })

// Fetch status details using StatusStore
async function fetchStatus() {
  try {
    const status = await statusStore.fetchStatuses()

    const fetchedStatus = status.find((s) => s.id === route.params.id)
    if (!fetchedStatus) {
      throw new Error('Failed to fetch status')
    }

    // Validate status name
    if (fetchedStatus.name === 'No Status' || fetchedStatus.name === 'Done') {
      console.error('Cannot edit status named No Status.')
      showAlert('This status is the default status and cannot be modified.', 'rgb(251 146 60)')
      router.push(`/boards/${boardId}/status`)
      return
    }

    // Assign the status details to form
    statuses.value = fetchedStatus
    originalStatus.value = { ...fetchedStatus }
  } catch (error) {
    console.error('Error fetching status:', error)
    // router.push('/editstatuserror')
  }
}

// Save changes using StatusStore
async function saveChanges() {
  try {
    statuses.value.name = statuses.value.name.trim()
    statuses.value.description = statuses.value.description.trim()

    await statusStore.updateStatus(route.params.id, statuses.value) // Update the status

    router.push(`/boards/${boardId}/status`)
    showAlert('The status has been updated.', 'rgb(34 197 94)')
  } catch (error) {
    console.error('Failed to update Status:', error)
    if (error.response?.status === 500) {
      showAlert('Status name must be unique, please choose another name.', 'rgb(251 146 60)')
    }
  }
}

onMounted(fetchStatus)

const isModified = computed(() => {
  return (
    statuses.value.name !== originalStatus.value.name ||
    statuses.value.description !== originalStatus.value.description
  )
})

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
  <div class="fixed inset-0 flex items-center justify-center bg-gray-900 bg-opacity-50">
    <div class="bg-blue-100 rounded-lg p-8 max-w-3xl w-full">
      <h2 class="text-rose-400 text-xl font-bold mb-2 text-center text-20 text-black">
        Edit Status
      </h2>

      <form @submit.prevent="">
        <div class="itbkk-item mb-6">
          <label for="status-name" class="text-rose-400 block text-sm font-medium text-gray-700">
            Name
          </label>
          <input
            type="text"
            id="itbkk-status-name"
            v-model="statuses.name"
            class="bg-white text-blue-600 mt-1 block h-9 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            @input="checkMaxLength('name', $event.target.value)"
            maxlength="50"
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
            id="itbkk-status-description"
            v-model="statuses.description"
            class="bg-white text-blue-600 mt-1 block h-40 w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            @input="checkMaxLength('description', $event.target.value)"
            maxlength="200"
          ></textarea>
        </div>

        <div class="flex mt-5 justify-center">
          <button
            id="itbkk-button-edit"
            :disabled="!isModified || statuses.name.trim().length === 0"
            @click="saveChanges"
            class="itbkk-button-edit btn text-white border-white mr-6 bg-green-500 hover:bg-green-600 border-4 hover:border-green-300 w-max h-5 text-slate-600 rounded-3xl p-6 px-8 py-2 text-base font-semibold text-center ml-16"
            :class="{
              'bg-gray-400': !isModified || statuses.name.trim().length === 0,
              'text-white': !isModified || statuses.name.trim().length === 0,
              'border-white': !isModified || statuses.name.trim().length === 0,
              disabled: !isModified || statuses.name.trim().length === 0
            }"
          >
            Save
          </button>

          <button
            id="itbkk-button-cancel"
            type="button"
            @click="() => router.push(`/boards/${boardId}/status`)"
            class="itbkk-button-cancel bg-red-400 border-4 border-white rounded-3xl mx-5 p-8 px-6 py-2 text-base text-white font-semibold text-center"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
