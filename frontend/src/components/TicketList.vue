<template>
  <div class="max-w-4xl mx-auto bg-white p-6 rounded-lg shadow-md">
    <h2 class="text-2xl font-bold mb-4">My Support Tickets</h2>

    <div v-if="loading" class="text-center py-4">
      Loading tickets...
    </div>

    <div v-else-if="tickets.length === 0" class="text-center py-8 text-gray-500">
      No support tickets found. Create your first ticket above.
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
          <div>
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
    
    <TicketChat 
      v-if="selectedTicket" 
      :ticket="selectedTicket" 
      @close="selectedTicket = null"
    />
  </div>
</template>

<script>
import axios from 'axios'
import { useAuthStore } from '../stores/auth'
import TicketChat from './TicketChat.vue'

export default {
  name: 'TicketList',
  components: {
    TicketChat
  },
  setup() {
    const authStore = useAuthStore()
    return { authStore }
  },
  data() {
    return {
      tickets: [],
      loading: false,
      selectedTicket: null
    }
  },
  mounted() {
    this.loadTickets()
  },
  watch: {
    'authStore.user': {
      handler(newUser) {
        if (newUser?.id) {
          this.loadTickets()
        }
      },
      immediate: true
    }
  },
  methods: {
    async loadTickets() {
      this.loading = true
      try {
        const userId = this.authStore.user?.id

        if (!userId) {
          console.warn('User ID not available, skipping ticket load')
          return
        }

        const response = await axios.get(`http://localhost:8080/api/support`, {
          headers: {
            'Authorization': `Bearer ${this.authStore.token}`
          }
        })
        this.tickets = response.data
      } catch (error) {
        console.error('Error loading tickets:', error)
        this.tickets = []
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
    refreshTickets() {
      this.loadTickets()
    },
    openTicket(ticket) {
      this.selectedTicket = ticket
    }
  }
}
</script>