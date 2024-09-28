import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useRouter } from 'vue-router' // import router

export const useTaskStore = defineStore('taskStore', () => {
  const tasks = ref([])
  const BASE_URL = import.meta.env.VITE_BASE_URL
  const router = useRouter() // instantiate router

  // Helper function to handle 401 status
  const handleUnauthorized = (status) => {
    if (status === 401) {
      router.push('/login') // redirect to login page
    }
  }

  // Fetch all tasks in a specific board
  const fetchTasks = async (boardId) => {
    try {
      const accessToken = document.cookie.match(/access_token=([^;]*)/)[1]
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

  // Add a task to a specific board
  const addTask = async (boardId, newTask) => {
    try {
      const accessToken = document.cookie.match(/access_token=([^;]*)/)[1]
      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/tasks`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`
        },
        body: JSON.stringify(newTask)
      })
      if (response.status === 201) {
        await fetchTasks(boardId) // Refresh tasks after adding
      } else {
        handleUnauthorized(response.status)
      }
      return response
    } catch (error) {
      console.error('Error adding task:', error)
      throw error
    }
  }

  // Update a task in a specific board
  const updateTask = async (boardId, updatedTask) => {
    try {
      const accessToken = document.cookie.match(/access_token=([^;]*)/)[1]
      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/tasks/${updatedTask.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`
        },
        body: JSON.stringify(updatedTask)
      })
      if (response.ok) {
        await fetchTasks(boardId) // Refresh tasks after updating
      } else {
        handleUnauthorized(response.status)
        console.error('Failed to update task:', response.statusText)
      }
    } catch (error) {
      console.error('Error updating task:', error)
    }
  }

  // Delete a task from a specific board
  const deleteTask = async (boardId, taskId) => {
    try {
      const accessToken = document.cookie.match(/access_token=([^;]*)/)[1]
      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/tasks/${taskId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      })
      if (response.ok) {
        await fetchTasks(boardId) // Refresh tasks after deletion
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
