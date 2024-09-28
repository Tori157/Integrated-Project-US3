// src/stores/TaskStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useRouter } from 'vue-router' // import router
import { useCurrentBoardStore } from '@/stores/BoardStore' // นำเข้า CurrentBoardStore

export const useTaskStore = defineStore('taskStore', () => {
  const tasks = ref([])
  const BASE_URL = import.meta.env.VITE_BASE_URL
  const router = useRouter() // instantiate router
  const currentBoardStore = useCurrentBoardStore() // เริ่มต้น CurrentBoardStore

  // Helper function to handle 401 status
  const handleUnauthorized = (status) => {
    if (status === 401) {
      router.push('/login') // redirect to login page
    }
  }

  // Fetch all tasks in the current board
  const fetchTasks = async () => {
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

      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/tasks`, {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      })

      if (response.ok) {
        tasks.value = await response.json()
        console.log(tasks.value)
      } else {
        handleUnauthorized(response.status)
        console.error('Failed to fetch tasks:', response.statusText)
      }
    } catch (error) {
      console.error('Error fetching tasks:', error)
    }
  }

  // Add a task to the current board
  const addTask = async (newTask) => {
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

      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/tasks`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`
        },
        body: JSON.stringify(newTask)
      })
      if (!response.ok) {
        const errorResponse = await response.json() // Try parsing the error response
        console.error('Error:', errorResponse)
      }
      if (response.status === 201) {
        await fetchTasks() // Refresh tasks after adding
      } else {
        handleUnauthorized(response.status)
        console.error('Failed to add task:', response.statusText)
      }
      return response
    } catch (error) {
      console.error('Error adding task:', error)
      throw error
    }
  }

  // Update a task in the current board
  const updateTask = async (updatedTask) => {
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

      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/tasks/${updatedTask.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`
        },
        body: JSON.stringify(updatedTask)
      })

      if (response.ok) {
        await fetchTasks() // Refresh tasks after updating
      } else {
        handleUnauthorized(response.status)
        console.error('Failed to update task:', response.statusText)
      }
    } catch (error) {
      console.error('Error updating task:', error)
    }
  }

  // Delete a task from the current board
  const deleteTask = async (taskId) => {
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

      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/tasks/${taskId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      })

      if (response.ok) {
        await fetchTasks() // Refresh tasks after deletion
      } else {
        handleUnauthorized(response.status)
        console.error('Failed to delete task:', response.statusText)
      }
    } catch (error) {
      console.error('Error deleting task:', error)
    }
  }

  return { tasks, fetchTasks, addTask, updateTask, deleteTask }
})
