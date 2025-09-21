<template>
  <div class="min-h-screen bg-gray-50">
    <Navbar />
    <div class="container mx-auto px-4 py-8">
      <!-- Back Button -->
      <button
        @click="goBack"
        class="mb-4 flex items-center gap-2 text-gray-700 hover:text-gray-900 font-medium"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
        </svg>
        Back
      </button>
      <h1 class="text-3xl font-bold text-gray-900 text-center mb-8">
        {{ isAdmin ? 'Admin Support Center' : 'Support Center' }}
      </h1>
      
      <!-- Admin View -->
      <div v-if="isAdmin" class="max-w-6xl mx-auto">
        <AdminTicketList />
      </div>
      
      <!-- User View -->
      <div v-else class="max-w-4xl mx-auto">
        <TicketList ref="ticketList" />
      </div>
    </div>
  </div>
</template>

<script>
import TicketList from './TicketList.vue'
import AdminTicketList from './AdminTicketList.vue'
import Navbar from './Navbar.vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

export default {
  name: 'SupportPage',
  components: {
    TicketList,
    AdminTicketList,
    Navbar
  },
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const goBack = () => router.back()
    return { authStore, goBack }
  },
  computed: {
    isAdmin() {
      return this.authStore.user?.role === 'ADMIN'
    }
  },
}
</script>