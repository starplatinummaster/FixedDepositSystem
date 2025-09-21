<template>
  <div class="h-screen bg-gray-50 flex flex-col">
    <Navbar />
    <div class="flex-1">
      <div v-if="isAdmin" class="p-6">
        <div class="flex justify-center mb-6">
          <div class="flex bg-gray-100 rounded-lg p-1">
            <button
              @click="ticketFilter = 'OPEN'"
              :class="[
                'px-4 py-2 rounded-md font-medium transition',
                ticketFilter === 'OPEN' ? 'bg-white text-green-600 shadow' : 'text-gray-600 hover:text-gray-900'
              ]"
            >
              Open Tickets
            </button>
            <button
              @click="ticketFilter = 'RESOLVED'"
              :class="[
                'px-4 py-2 rounded-md font-medium transition',
                ticketFilter === 'RESOLVED' ? 'bg-white text-blue-600 shadow' : 'text-gray-600 hover:text-gray-900'
              ]"
            >
              Resolved Tickets
            </button>
          </div>
        </div>
        <AdminTicketList :filter="ticketFilter" />
      </div>
      <FixedDepositDashboard v-else />
    </div>

    <!-- Support Button (fixed bottom right) - Only for non-admin users -->
    <SupportButton v-if="!isAdmin" @open="showSupportMenu = true" />

    <!-- Support Menu Modal -->
    <SupportMenuModal
      v-if="showSupportMenu"
      @close="showSupportMenu = false"
      @create-ticket="openTicketModal"
      @view-tickets="goToSupportPage"
    />

    <!-- Support Ticket Modal -->
    <SupportTicketModal
      v-if="showTicketModal"
      :fdOptions="fdOptions"
      @close="showTicketModal = false"
      @submit="handleTicketSubmit"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProfile } from '../services/authService'
import FixedDepositDashboard from '../components/FixedDepositDashboard.vue'
import AdminTicketList from '../components/AdminTicketList.vue'
import Navbar from '../components/Navbar.vue'
import SupportButton from '../components/SupportButton.vue'
import SupportMenuModal from '../components/SupportMenuModal.vue'
import SupportTicketModal from '../components/SupportTicketModal.vue'
import { createSupportTicket } from '../services/supportService'

const userName = ref('')
const userRole = ref('')
const isAdmin = ref(false)
const ticketFilter = ref('OPEN')
const showSupportMenu = ref(false)
const showTicketModal = ref(false)
const router = useRouter()

// FD options will be loaded from API
const fdOptions = ref([])

onMounted(async () => {
  const res = await getProfile()
  userName.value = res.data.name
  userRole.value = res.data.role
  isAdmin.value = res.data.role === 'ADMIN'

  // Only load FD options for non-admin users
  if (!isAdmin.value) {
    await loadFDOptions()
  }
})

const loadFDOptions = async () => {
  try {
    console.log('Loading FD options...')
    const token = localStorage.getItem('token')
    console.log('Token:', token ? 'Present' : 'Missing')

    const response = await fetch('http://localhost:8080/api/fd', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    console.log('Response status:', response.status)
    console.log('Response ok:', response.ok)

    if (response.ok) {
      const fds = await response.json()
      console.log('Raw FD data received:', fds)
      console.log('FD data type:', typeof fds)
      console.log('FD data length:', Array.isArray(fds) ? fds.length : 'Not an array')

      if (Array.isArray(fds) && fds.length > 0) {
        console.log('First FD item:', fds[0])
        console.log('First FD keys:', Object.keys(fds[0]))
        console.log('First FD stringified:', JSON.stringify(fds[0], null, 2))
      }

      fdOptions.value = fds.filter(fd => fd && fd.id).map(fd => {
        console.log('Processing FD item JSON:', JSON.stringify(fd, null, 2))
        console.log('All FD properties:', Object.keys(fd))

        return {
          id: fd.id,
          label: `₹${fd.amountInvested?.toLocaleString() || 'N/A'} - ${fd.tenureMonths || 'N/A'} months - ${fd.status || 'Unknown'}`
        }
      })

      console.log('Final fdOptions:', fdOptions.value)
    } else {
      console.error('Failed to fetch FDs, status:', response.status)
    }
  } catch (error) {
    console.error('Error loading FD options:', error)
  }
}

const openTicketModal = () => {
  console.log('Opening ticket modal with fdOptions:', fdOptions.value)
  showSupportMenu.value = false
  showTicketModal.value = true
}

const goToSupportPage = () => {
  showSupportMenu.value = false
  router.push('/support')
}

const handleTicketSubmit = async (ticketData) => {
  try {
    const user = await getProfile()
    console.log('User fetched:', user.data)

    await createSupportTicket({
      fdId: ticketData.relatedFD || null,
      subject: ticketData.subject,
      description: ticketData.description
    })
    showTicketModal.value = false
  } catch (e) {
    console.error('Error creating support ticket:', e)
  }
}
</script>