<template>
  <div class="max-w-6xl mx-auto bg-white p-6 rounded-lg shadow-md">
    <h2 class="text-2xl font-bold mb-4">All Support Tickets (Admin)</h2>
    
    <div v-if="loading" class="text-center py-4">
      Loading tickets...
    </div>
    
    <div v-else-if="tickets.length === 0" class="text-center py-8 text-gray-500">
      No support tickets found.
    </div>
    
    <div v-else class="space-y-4">
      <div 
        v-for="ticket in tickets" 
        :key="ticket.id"
        class="border rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer"
        @click="openTicket(ticket)"
      >
        <div class="flex justify-between items-start mb-2">
          <h3 class="text-lg font-semibold">{{ ticket.subject }}</h3>
          <span 
            class="px-2 py-1 rounded-full text-xs font-medium"
            :class="getStatusClass(ticket.status)"
          >
            {{ ticket.status }}
          </span>
        </div>
        
        <p class="text-gray-600 mb-3">{{ ticket.description }}</p>
        
        <div class="flex justify-between items-center text-sm text-gray-500">
          <div class="flex space-x-4">
            <span>User ID: #{{ ticket.userId.substring(0, 8) }}</span>
            <span v-if="ticket.fdId">
              Related FD: #{{ ticket.fdId.substring(0, 8) }}
            </span>
            <span v-else>General Inquiry</span>
          </div>
          <div>
            Created: {{ formatDate(ticket.createdAt) }}
          </div>
        </div>
      </div>
    </div>
    
    <AdminTicketChat 
      v-if="selectedTicket" 
      :ticket="selectedTicket" 
      @close="selectedTicket = null"
    />
  </div>
</template>

<script>
import axios from 'axios'
import { useAuthStore } from '../stores/auth'
import AdminTicketChat from './AdminTicketChat.vue'

export default {
  name: 'AdminTicketList',
  props: {
    filter: {
      type: String,
      default: 'OPEN'
    }
  },
  components: {
    AdminTicketChat
  },
  setup() {
    const authStore = useAuthStore()
    return { authStore }
  },
  data() {
    return {
      allTickets: [],
      loading: false,
      selectedTicket: null
    }
  },
  computed: {
    tickets() {
      return this.allTickets.filter(ticket => ticket.status === this.filter)
    }
  },
  mounted() {
    this.loadTickets()
  },
  methods: {
    async loadTickets() {
      this.loading = true
      try {
        const response = await axios.get(`http://localhost:8080/api/support/tickets`, {
          headers: {
            'Authorization': `Bearer ${this.authStore.token}`
          }
        })
        this.allTickets = response.data
      } catch (error) {
        console.error('Error loading tickets:', error)
        this.allTickets = []
      } finally {
        this.loading = false
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
    },
    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    openTicket(ticket) {
      this.selectedTicket = ticket
    },
    refreshTickets() {
      this.loadTickets()
    }
  }
}
</script>