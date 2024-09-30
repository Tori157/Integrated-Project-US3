import { ref } from 'vue'
import { defineStore } from 'pinia'
import { useRouter } from 'vue-router'
import { useCurrentBoardStore } from '@/stores/BoardStore'
import { showAlert } from '@/utils/toast.js'

const BASE_URL = import.meta.env.VITE_BASE_URL
const router = useRouter()
// Helper function to get access token
const getAccessToken = () => {
  const match = document.cookie.match(/access_token=([^;]*)/)
  if (!match) {
    console.error('Access token is missing or expired')
    router.push('/login') // Redirect to login if token is missing
    throw new Error('Access token not found')
  }
  return match[1]
}

// Helper function to generate headers
const getHeaders = (isJson = false) => {
  const headers = {
    Authorization: `Bearer ${getAccessToken()}`
  }
  if (isJson) {
    headers['Content-Type'] = 'application/json'
  }
  return headers
}

export const useStatusStore = defineStore('statusStore', () => {
  const statuses = ref([])
  const router = useRouter()
  const currentBoardStore = useCurrentBoardStore()

  // Handle unauthorized access
  const handleUnauthorized = (status) => {
    if (status === 401) {
      router.push('/login')
    }
  }

  // Fetch all statuses for the current board
  const fetchStatuses = async () => {
    try {
      const boardId = currentBoardStore.currentBoardId
      if (!boardId) {
        throw new Error('No current board selected')
      }

      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses`, {
        headers: getHeaders()
      })

      if (response.ok) {
        const data = await response.json()
        if (Array.isArray(data)) {
          statuses.value = data
        } else {
          console.error('Unexpected data format:', data)
        }
      } else {
        handleUnauthorized(response.status)
        console.error('Failed to fetch statuses:', response.statusText)
      }
    } catch (error) {
      console.error('Error fetching statuses:', error)
    }
  }

  // Add new status to the current board
  const addStatus = async (newStatus) => {
    try {
      const boardId = currentBoardStore.currentBoardId
      if (!boardId) {
        throw new Error('No current board selected')
      }

      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses`, {
        method: 'POST',
        headers: getHeaders(true),
        body: JSON.stringify(newStatus)
      })

      if (response.status === 201) {
        const addedStatus = await response.json()
        statuses.value.push(addedStatus)
        showAlert('The status has been added', 'rgb(34 197 94)')
      } else if (response.status === 400) {
        console.error('Failed to save Status:', response.statusText)
        showAlert('Status name must be unique, please choose another name.', 'rgb(251 146 60)')
      } else {
        handleUnauthorized(response.status)
        console.error('Failed to add status:', response.statusText)
      }
    } catch (error) {
      console.error('Error adding status:', error)
    }
  }

  // Update status in the current board
  const updateStatus = async (id, updatedStatus) => {
    try {
      const boardId = currentBoardStore.currentBoardId
      if (!boardId) {
        throw new Error('No current board selected')
      }

      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses/${id}`, {
        method: 'PUT',
        headers: getHeaders(true),
        body: JSON.stringify(updatedStatus)
      })

      if (response.ok) {
        const data = await response.json()
        const statusIndex = statuses.value.findIndex((status) => status.id === id)
        if (statusIndex !== -1) {
          statuses.value[statusIndex] = data
          showAlert('The status has been updated.', 'rgb(34 197 94)')
        }
      } else if (response.status === 404) {
        console.error('The status does not exist.')
        showAlert('An error has occurred, the status does not exist.', 'rgb(251 146 60)')
      } else if (response.status === 500) {
        console.error('Failed to update status')
        showAlert('Status name must be unique, please choose another name.', 'rgb(251 146 60)')
      } else {
        handleUnauthorized(response.status)
        console.error('Failed to update status:', response.statusText)
      }
    } catch (error) {
      console.error('Error updating status:', error)
    }
  }

  // Transfer tasks from one status to another
  const transferTasks = async (fromStatusId, toStatusId) => {
    const boardId = currentBoardStore.currentBoardId
    if (!boardId) {
      throw new Error('No current board selected')
    }

    const response = await fetch(
      `${BASE_URL}/v3/boards/${boardId}/statuses/${fromStatusId}/${toStatusId}`,
      {
        headers: getHeaders(),
        method: 'DELETE'
      }
    )

    if (!response.ok) {
      console.error('Failed to fetch tasks:', response.statusText)
      handleUnauthorized(response.status)
      throw new Error('Failed to fetch tasks for transfer.')
    }

    const tasks = await response.json()

    if (tasks.length === 0) {
      return
    }
  }

  // Delete status from the current board with task transfer
  const deleteStatus = async (id, newStatusId = null) => {
    try {
      const boardId = currentBoardStore.currentBoardId
      if (!boardId) {
        throw new Error('No current board selected')
      }

      let response

      if (newStatusId) {
        // หากมีการโอนย้าย Tasks ก่อนลบ Status
        await transferTasks(id, newStatusId)

        // ลบ Status โดยใช้ endpoint ที่โอนย้าย Tasks ไปแล้ว
        response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses/${id}/${newStatusId}`, {
          method: 'DELETE',
          headers: getHeaders()
        })

        if (response.ok) {
          statuses.value = statuses.value.filter((status) => status.id !== id)
          showAlert('The status has been transferred and deleted.', 'rgb(244 63 94)')
        } else {
          handleUnauthorized(response.status)
          console.error('Failed to delete status with transfer:', response.statusText)
          throw new Error('Failed to delete status with transfer.')
        }
      } else {
        // หากไม่มี Tasks ใน Status ให้ลบโดยตรง
        response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses/${id}`, {
          method: 'DELETE',
          headers: getHeaders()
        })

        if (response.ok) {
          statuses.value = statuses.value.filter((status) => status.id !== id)
          showAlert('The status has been deleted.', 'rgb(244 63 94)')
        } else {
          handleUnauthorized(response.status)
          console.error('Failed to delete status:', response.statusText)
          throw new Error('Failed to delete status.')
        }
      }
    } catch (error) {
      console.error('Error deleting status:', error)
      showAlert('An error occurred while deleting the status.', 'rgb(251 146 60)')
      throw error
    }
  }

  return { statuses, fetchStatuses, addStatus, updateStatus, deleteStatus, transferTasks }
})
