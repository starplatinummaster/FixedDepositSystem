<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg w-full max-w-2xl h-3/4 flex flex-col">
      <!-- Header -->
      <div class="p-4 border-b flex justify-between items-center">
        <div class="flex-1">
          <h3 class="text-lg font-semibold">{{ ticket.subject }}</h3>
          <p class="text-sm text-gray-600">
            Ticket #{{ ticket.id.substring(0, 8) }} | User #{{ ticket.userId.substring(0, 8) }}
          </p>
          <div class="flex items-center space-x-2 mt-2">
            <span class="text-sm font-medium">Status:</span>
            <select 
              v-model="currentStatus" 
              @change="updateStatus"
              class="text-sm border rounded px-2 py-1"
              :class="getStatusClass(currentStatus)"
            >
              <option value="OPEN">OPEN</option>
              <option value="RESOLVED">RESOLVED</option>
            </select>
          </div>
        </div>
        <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </button>
      </div>

      <!-- Messages -->
      <div class="flex-1 overflow-y-auto p-4 space-y-4">
        <div v-for="message in messages" :key="message.id" 
             :class="['flex', message.senderRole === 'ADMIN' ? 'justify-end' : 'justify-start']">
          <div :class="['max-w-xs lg:max-w-md px-4 py-2 rounded-lg', 
                       message.senderRole === 'ADMIN' ? 'bg-green-500 text-white' : 'bg-gray-200 text-gray-800']">
            <div class="text-xs opacity-75 mb-1">
              {{ message.senderRole }} - {{ formatTime(message.createdAt) }}
            </div>
            <div>{{ message.message }}</div>
          </div>
        </div>
      </div>

      <!-- Input -->
      <div class="p-4 border-t">
        <form @submit.prevent="sendMessage" class="flex space-x-2">
          <input 
            v-model="newMessage" 
            type="text" 
            placeholder="Type your admin response..."
            class="flex-1 p-2 border rounded-md"
            :disabled="loading"
          />
          <button 
            type="submit" 
            :disabled="loading || !newMessage.trim()"
            class="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 disabled:opacity-50"
          >
            Send as Admin
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'AdminTicketChat',
  props: {
    ticket: {
      type: Object,
      required: true
    }
  },
  setup() {
    const authStore = useAuthStore()
    return { authStore }
  },
  data() {
    return {
      messages: [],
      newMessage: '',
      loading: false,
      currentStatus: this.ticket.status
    }
  },
  mounted() {
    this.loadMessages()
  },
  methods: {
    async loadMessages() {
      try {
        const response = await axios.get(`http://localhost:8080/api/support/admin/${this.ticket.id}/messages`, {
          headers: {
            'Authorization': `Bearer ${this.authStore.token}`
          }
        })
        this.messages = response.data
      } catch (error) {
        console.error('Error loading messages:', error)
      }
    },
    async sendMessage() {
      if (!this.newMessage.trim()) return
      
      this.loading = true
      try {
        const response = await axios.post(`http://localhost:8080/api/support/admin/${this.ticket.id}/messages`, {
          message: this.newMessage
        }, {
          headers: {
            'Authorization': `Bearer ${this.authStore.token}`
          }
        })
        
        this.messages.push(response.data)
        this.newMessage = ''
      } catch (error) {
        console.error('Error sending message:', error)
      } finally {
        this.loading = false
      }
    },
    formatTime(dateString) {
      return new Date(dateString).toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    async updateStatus() {
      try {
        await axios.put(`http://localhost:8080/api/support/admin/${this.ticket.id}/status?status=${this.currentStatus}`, {}, {
          headers: {
            'Authorization': `Bearer ${this.authStore.token}`
          }
        })
        // Update the ticket object
        this.ticket.status = this.currentStatus
      } catch (error) {
        console.error('Error updating status:', error)
        // Revert the status if update failed
        this.currentStatus = this.ticket.status
      }
    },
    getStatusClass(status) {
      switch (status) {
        case 'OPEN':
          return 'bg-green-100 text-green-800'
        case 'RESOLVED':
          return 'bg-yellow-100 text-yellow-800'
        default:
          return 'bg-blue-100 text-blue-800'
      }
    }
  }
}
</script>