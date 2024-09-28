import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useRouter } from 'vue-router' // import router

export const useBoardStore = defineStore('boardStore', () => {
  const boards = ref([])
  const BASE_URL = import.meta.env.VITE_BASE_URL
  const router = useRouter() // instantiate router

  // Fetch all boards
  const fetchBoards = async () => {
    try {
      const accessTokenMatch = document.cookie.match(/access_token=([^;]*)/)
      if (!accessTokenMatch) {
        throw new Error('Access token not found')
      }
      const accessToken = accessTokenMatch[1]
      const response = await fetch(`${BASE_URL}/v3/boards`, {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      })
      if (response.ok) {
        const data = await response.json()
        boards.value = data.sort((a, b) => new Date(b.createdOn) - new Date(a.createdOn))
      } else if (response.status === 401) {
        router.push('/login') // Redirect to login page on 401
      } else {
        console.error('Failed to fetch boards:', response.statusText)
      }
    } catch (error) {
      console.error('Error fetching boards:', error)
    }
  }

  // Add a new board
  const addBoard = async (newBoard) => {
    try {
      const accessTokenMatch = document.cookie.match(/access_token=([^;]*)/)
      if (!accessTokenMatch) {
        throw new Error('Access token not found')
      }
      const accessToken = accessTokenMatch[1]
      const response = await fetch(`${BASE_URL}/v3/boards`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`
        },
        body: JSON.stringify(newBoard)
      })
      if (response.status === 201) {
        await fetchBoards()
      } else if (response.status === 401) {
        router.push('/login') // Redirect to login page on 401
      } else {
        console.error('Failed to add board:', response.statusText)
      }
      return response
    } catch (error) {
      console.error('Error adding board:', error)
      throw error
    }
  }

  // Get a board by ID
  const getBoardById = async (boardId) => {
    try {
      const accessTokenMatch = document.cookie.match(/access_token=([^;]*)/)
      if (!accessTokenMatch) {
        throw new Error('Access token not found')
      }
      const accessToken = accessTokenMatch[1]
      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      })
      if (response.ok) {
        return await response.json()
      } else if (response.status === 401) {
        router.push('/login') // Redirect to login page on 401
      } else {
        console.error('Failed to get board:', response.statusText)
      }
    } catch (error) {
      console.error('Error getting board:', error)
    }
  }

  // Update a board by ID
  const updateBoard = async (updatedBoard) => {
    try {
      const accessTokenMatch = document.cookie.match(/access_token=([^;]*)/)
      if (!accessTokenMatch) {
        throw new Error('Access token not found')
      }
      const accessToken = accessTokenMatch[1]
      const response = await fetch(`${BASE_URL}/v3/boards/${updatedBoard.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${accessToken}`
        },
        body: JSON.stringify(updatedBoard)
      })
      if (response.ok) {
        await fetchBoards()
      } else if (response.status === 401) {
        router.push('/login') // Redirect to login page on 401
      } else {
        console.error('Failed to update board:', response.statusText)
      }
    } catch (error) {
      console.error('Error updating board:', error)
    }
  }

  // Delete a board by ID
  const deleteBoard = async (boardId) => {
    try {
      const accessTokenMatch = document.cookie.match(/access_token=([^;]*)/)
      if (!accessTokenMatch) {
        throw new Error('Access token not found')
      }
      const accessToken = accessTokenMatch[1]
      const response = await fetch(`${BASE_URL}/v3/boards/${boardId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${accessToken}`
        }
      })
      if (response.ok) {
        await fetchBoards()
      } else if (response.status === 401) {
        router.push('/login') // Redirect to login page on 401
      } else {
        console.error('Failed to delete board:', response.statusText)
      }
    } catch (error) {
      console.error('Error deleting board:', error)
    }
  }

  return { boards, fetchBoards, addBoard, getBoardById, updateBoard, deleteBoard }
})
