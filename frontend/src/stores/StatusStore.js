// src/stores/StatusStore.js
import { ref } from 'vue'
import { defineStore } from 'pinia'
import { useRouter } from 'vue-router'
import { useCurrentBoardStore } from '@/stores/BoardStore' // Import the current board store

const BASE_URL = import.meta.env.VITE_BASE_URL

const getAccessToken = () => {
  const match = document.cookie.match(/access_token=([^;]*)/)
  if (!match) {
    throw new Error('Access token not found')
  }
  return match[1]
}

export const useStatusStore = defineStore('statusStore', () => {
  const statuses = ref([])
  const router = useRouter() // instantiate router
  const currentBoardStore = useCurrentBoardStore() // initialize CurrentBoardStore

  // Helper function to handle 401 status
  const handleUnauthorized = (status) => {
    if (status === 401) {
      router.push('/login') // redirect to login page
    }
  }

  // Fetch all statuses for the current board
  const fetchStatuses = async () => {
    try {
      const boardId = currentBoardStore.currentBoardId
      if (!boardId) {
        throw new Error('No current board selected')
      }

      const accessTokenMatch = document.cookie.match(/access_token=([^;]*)/)
      if (!accessTokenMatch) {
        throw new Error('Access token not found')
      }
      const accessToken = accessTokenMatch[1]

      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses`, {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      })

      if (response.ok) {
        statuses.value = await response.json()
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

      const accessTokenMatch = document.cookie.match(/access_token=([^;]*)/)
      if (!accessTokenMatch) {
        throw new Error('Access token not found')
      }
      const accessToken = accessTokenMatch[1]

      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`
        },
        body: JSON.stringify(newStatus) // ส่ง newStatus ที่ประกอบไปด้วย name และ description
      })

      if (response.ok) {
        const addedStatus = await response.json()
        statuses.value.push(addedStatus)
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

      const accessTokenMatch = document.cookie.match(/access_token=([^;]*)/)
      if (!accessTokenMatch) {
        throw new Error('Access token not found')
      }
      const accessToken = accessTokenMatch[1]

      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`
        },
        body: JSON.stringify(updatedStatus)
      })

      if (response.ok) {
        statuses.value = statuses.value.filter((status) => status.id !== id)
        const data = await response.json()
        const statusIndex = statuses.value.findIndex((status) => status.id === id)
        if (statusIndex !== -1) {
          statuses.value[statusIndex] = data
        }
      } else {
        handleUnauthorized(response.status)
        console.error('Failed to update status:', response.statusText)
      }
    } catch (error) {
      console.error('Error updating status:', error)
    }
  }

  // Delete status from the current board
  const deleteStatus = async (id) => {
    try {
      const boardId = currentBoardStore.currentBoardId
      if (!boardId) {
        throw new Error('No current board selected')
      }

      const accessTokenMatch = document.cookie.match(/access_token=([^;]*)/)
      if (!accessTokenMatch) {
        throw new Error('Access token not found')
      }
      const accessToken = accessTokenMatch[1]

      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses/${id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      })

      if (response.ok) {
        statuses.value = statuses.value.filter((status) => status.id !== id)
      } else {
        handleUnauthorized(response.status)
        console.error('Failed to delete status:', response.statusText)
      }
    } catch (error) {
      console.error('Error deleting status:', error)
    }
  }

  // Transfer tasks from one status to another
  const transferTasks = async (fromStatusId, toStatusId) => {
    try {
      const boardId = currentBoardStore.currentBoardId
      if (!boardId) {
        throw new Error('No current board selected')
      }

      const accessToken = getAccessToken()

      // Fetch tasks associated with the fromStatusId
      const response = await fetch(
        `${BASE_URL}/v3/boards/${boardId}/tasks?statusId=${fromStatusId}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`
          }
        }
      )

      if (!response.ok) {
        handleUnauthorized(response.status)
        throw new Error('Failed to fetch tasks for transfer.')
      }

      const tasks = await response.json()

      if (tasks.length === 0) {
        console.log('No tasks to transfer.')
        return
      }

      // Prepare update promises
      const updatePromises = tasks.map((task) =>
        fetch(`${BASE_URL}/v3/boards/${boardId}/tasks/${task.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${accessToken}`
          },
          body: JSON.stringify({ ...task, statusId: toStatusId })
        })
      )

      // Execute all update requests in parallel
      const updateResponses = await Promise.all(updatePromises)

      // Check for any failed updates
      const failedUpdates = updateResponses.filter((res) => !res.ok)
      if (failedUpdates.length > 0) {
        console.error('Some tasks failed to transfer.')
        throw new Error('Failed to transfer some tasks.')
      }

      console.log('All tasks transferred successfully.')
    } catch (error) {
      console.error('Error transferring tasks:', error)
      throw error // Propagate the error to the caller
    }
  }

  return { statuses, fetchStatuses, addStatus, updateStatus, deleteStatus, transferTasks }
})
