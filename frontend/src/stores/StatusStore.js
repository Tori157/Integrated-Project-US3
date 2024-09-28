import { defineStore } from 'pinia'
import { useRouter } from 'vue-router' // import the router
const BASE_URL = import.meta.env.VITE_BASE_URL

export const useStatusStore = defineStore('statusStore', {
  state: () => ({
    statuses: []
  }),

  actions: {
    // Fetch all statuses for a specific board (GET)
    async fetchStatuses(boardId) {
      const router = useRouter() // instantiate router
      
      try {
        const accessToken = document.cookie.match(/access_token=([^;]*)/)[1]
        const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses`, {
          headers: {
            Authorization: `Bearer ${accessToken}`
          }
        })
        if (response.ok) {
          const data = await response.json()
          this.statuses = data
        } else if (response.status === 401) {
          router.push('/login') // redirect to login page
        } else {
          console.error('Failed to fetch statuses:', response.statusText)
        }
      } catch (error) {
        console.error('Error fetching statuses:', error)
      }
    },

    // Add new status to a specific board (POST)
    async addStatus(boardId, newStatus) {
      const router = useRouter() // instantiate router

      try {
        const accessToken = document.cookie.match(/access_token=([^;]*)/)[1]
        const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${accessToken}`
          },
          body: JSON.stringify(newStatus)
        })
        if (response.ok) {
          const addedStatus = await response.json()
          this.statuses.push(addedStatus)
        } else if (response.status === 401) {
          router.push('/login') // redirect to login page
        } else {
          console.error('Failed to add status:', response.statusText)
        }
      } catch (error) {
        console.error('Error adding status:', error)
      }
    },

    // Update status in a specific board (PUT)
    async updateStatus(boardId, id, updatedStatus) {
      const router = useRouter() // instantiate router

      try {
        const accessToken = document.cookie.match(/access_token=([^;]*)/)[1]
        const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses/${id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${accessToken}`
          },
          body: JSON.stringify(updatedStatus)
        })
        if (response.ok) {
          const data = await response.json()
          const statusIndex = this.statuses.findIndex((status) => status.id === id)
          if (statusIndex !== -1) {
            this.statuses[statusIndex] = data
          }
        } else if (response.status === 401) {
          router.push('/login') // redirect to login page
        } else {
          console.error('Failed to update status:', response.statusText)
        }
      } catch (error) {
        console.error('Error updating status:', error)
      }
    },

    // Delete status from a specific board (DELETE)
    async deleteStatus(boardId, id) {
      const router = useRouter() // instantiate router

      try {
        const accessToken = document.cookie.match(/access_token=([^;]*)/)[1]
        const response = await fetch(`${BASE_URL}/v3/boards/${boardId}/statuses/${id}`, {
          method: 'DELETE',
          headers: {
            Authorization: `Bearer ${accessToken}`
          }
        })
        if (response.ok) {
          this.statuses = this.statuses.filter((status) => status.id !== id)
        } else if (response.status === 401) {
          router.push('/login') // redirect to login page
        } else {
          console.error('Failed to delete status:', response.statusText)
        }
      } catch (error) {
        console.error('Error deleting status:', error)
      }
    }
  }
})
