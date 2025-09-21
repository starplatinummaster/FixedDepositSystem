<template>
  <div class="max-w-md mx-auto bg-white p-6 rounded-lg shadow-md">
    <h2 class="text-2xl font-bold mb-4">Create Support Ticket</h2>
    
    <form @submit.prevent="submitTicket">
      <div class="mb-4">
        <label class="block text-sm font-medium mb-2">Logged in as:</label>
        <div class="w-full p-2 border rounded-md bg-gray-50">
          {{ authStore.userLoading ? 'Loading...' : (authStore.user?.name || 'Unknown User') }}
        </div>
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium mb-2">Related Fixed Deposit (Optional)</label>
        <select
          v-model="ticket.fdId"
          class="w-full p-2 border rounded-md"
        >
          <option value="">Select a Fixed Deposit (Optional)</option>
          <option
            v-for="fd in fixedDeposits"
            :key="fd.id"
            :value="fd.id"
          >
            FD #{{ fd.id.substring(0, 8) }} - ₹{{ fd.amount?.toLocaleString() || 'N/A' }} ({{ fd.status }})
          </option>
        </select>
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium mb-2">Subject *</label>
        <input
          v-model="ticket.subject"
          type="text"
          required
          class="w-full p-2 border rounded-md"
          placeholder="Brief description of your issue"
        />
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium mb-2">Description *</label>
        <textarea
          v-model="ticket.description"
          required
          rows="4"
          class="w-full p-2 border rounded-md"
          placeholder="Detailed description of your issue"
        ></textarea>
      </div>

      <button
        type="submit"
        :disabled="loading || authStore.userLoading || !userReady"
        class="w-full bg-blue-500 text-white p-2 rounded-md hover:bg-blue-600 disabled:opacity-50"
      >
        {{ loading ? 'Submitting...' : authStore.userLoading ? 'Loading user...' : 'Submit Ticket' }}
      </button>
    </form>

    <div v-if="message" class="mt-4 p-3 rounded-md" :class="messageClass">
      {{ message }}
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { useAuthStore } from '../stores/auth'

export default {
  name: 'SupportForm',
  setup() {
    const authStore = useAuthStore()
    return { authStore }
  },
  computed: {
    userReady() {
      return this.authStore.user && !this.authStore.userLoading
    }
  },
  data() {
    return {
      ticket: {
        subject: '',
        description: '',
        fdId: ''
      },
      fixedDeposits: [],
      loading: false,
      message: '',
      messageClass: ''
    }
  },
  mounted() {
    this.loadFixedDeposits()
  },

  methods: {
    async submitTicket() {
      this.loading = true
      this.message = ''

      try {
        // Get user ID from auth store
        const userId = this.authStore.user?.id

        console.log('Auth store user:', this.authStore.user)
        console.log('User ID:', userId)

        const response = await axios.post(`http://localhost:8080/api/support`, {
          subject: this.ticket.subject,
          description: this.ticket.description,
          fdId: this.ticket.fdId === '' ? null : this.ticket.fdId
        }, {
          headers: {
            'Authorization': `Bearer ${this.authStore.token}`
          }
        })

        this.message = 'Support ticket created successfully!'
        this.messageClass = 'bg-green-100 text-green-800'
        this.resetForm()
        this.$emit('ticket-created', response.data)
      } catch (error) {
        this.message = 'Error creating ticket. Please try again.'
        this.messageClass = 'bg-red-100 text-red-800'
        console.error('Error:', error)
      } finally {
        this.loading = false
      }
    },
    resetForm() {
      this.ticket = {
        subject: '',
        description: '',
        fdId: ''
      }
    },
    async loadFixedDeposits() {
      try {
        const response = await axios.get('http://localhost:8080/api/fd', {
          headers: {
            'Authorization': `Bearer ${this.authStore.token}`
          }
        })
        this.fixedDeposits = response.data
      } catch (error) {
        console.error('Error loading fixed deposits:', error)
        this.fixedDeposits = []
      }
    }
  }
}
</script>