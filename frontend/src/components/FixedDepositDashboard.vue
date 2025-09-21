<template>
  <div class="min-h-screen bg-gray-50 rounded">
    <div class="px-6 py-6">
      <div class="flex items-center justify-between mb-6">
        <TabGroup :tabs="tabs" :active-tab="activeTab" @tab-change="handleTabChange" />
        <CreateButton @click="handleCreateNew" />
        <FDCreateModal
          v-if="showCreateModal"
          @close="showCreateModal = false"
          @confirm="handleBookFD"
        />
      </div>
      <FixedDepositTable
        :deposits="paginatedDeposits"
        @view-details="handleViewDetails"
        @break-fd="handleBreakFD"
      />
      <div class="mt-4">
        <Pagination
          :current-page="currentPage"
          :total-pages="totalPages"
          :total-items="filteredDeposits.length"
          :items-per-page="itemsPerPage"
          @page-change="handlePageChange"
        />
      </div>
    </div>
    <FDDetailsModal v-if="selectedFD" :fd="selectedFD" @close="closeModal" />
    <BreakFixedDeposit
      v-if="breakFDModalData"
      :fd="breakFDModalData"
      @close="breakFDModalData = null"
      @advisor="handleAdvisor"
      @confirm-break="showBreakConfirmModal = true"
    />
    <BreakConfirmModal
      v-if="showBreakConfirmModal"
      :fd="breakFDModalData"
      :reason="''"
      @cancel="showBreakConfirmModal = false"
      @confirm="confirmBreakFD"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import TabGroup from './TabGroup.vue'
import CreateButton from './CreateButton.vue'
import FixedDepositTable from './FixedDepositTable.vue'
import Pagination from './Pagination.vue'
import FDDetailsModal from './FDDetailsModal.vue'
import BreakFixedDeposit from './BreakFixedDeposit.vue'
import FDCreateModal from './FDCreateModal.vue'
import { fetchFDs, createFD, breakFD, fetchBreakPreview } from '../services/fdService'
import { createSupportTicket } from '../services/supportService'
import { useRouter } from 'vue-router'
import BreakConfirmModal from './BreakConfirmModal.vue'

const activeTab = ref('All')
const currentPage = ref(1)
const itemsPerPage = 9
const selectedFD = ref(null)
const breakFDModalData = ref(null)
const showCreateModal = ref(false)
const deposits = ref([])
const rawFDs = ref([])
const router = useRouter()
const showBreakConfirmModal = ref(false)
const breakReason = ref('')
const breakFDData = ref(null)

const confirmBreakFD = async (reason) => {
  await breakFD(breakFDModalData.value.fdId, reason)
  showBreakConfirmModal.value = false      // Close BreakConfirmModal
  breakFDModalData.value = null            // Close BreakFixedDeposit modal
  await loadFDs()                          // Refresh FD list
  router.push('/')                         // Go to home page
}

const handleAdvisor = async (fd) => {
  await createSupportTicket({
    fdId: fd.fdId,
    subject: 'Break FD - Advisor Consultation',
    description: 'I want to break this FD and want to understand penalty and implications.'
  })
  breakFD.value = null
  router.push('/support')
}

const loadFDs = async () => {
  try {
    const res = await fetchFDs()
    rawFDs.value = res.data
    deposits.value = res.data.map(fd => ({
      id: fd.id,
      amountInvested: `₹${Number(fd.amountInvested).toLocaleString()}`,
      interestRate: `${fd.interestRate}%`,
      maturityDate: formatDate(fd.maturityDate),
      progress: `${fd.progress}`,
      interestAccrued: `₹${Number(fd.interestAccrued).toLocaleString()}`,
      status: mapStatus(fd.status)
    }))
  } catch (e) {
    console.error('Error fetching FDs:', e)
  }
}

function formatDate(dateStr) {
  const d = new Date(dateStr)
  return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
}
function mapStatus(status) {
  if (status === 'ACTIVE') return 'Active'
  if (status === 'BROKEN') return 'Broken'
  if (status === 'MATURED') return 'Matured'
  return status
}

onMounted(loadFDs)

const handleBookFD = async (fdData) => {
  try {
    // console.log(fdData.amount, fdData.tenureKey)
    await createFD({
      amount: fdData.amount,
      tenureMonths: fdData.tenureKey
    })
    showCreateModal.value = false
    await loadFDs() // Refresh the FD list
  } catch (e) {
    console.error('Error creating FD:', e)
  }
}

const handleViewDetails = (depositId) => {
  const fdRaw = rawFDs.value.find(fd => fd.id === depositId)
  if (!fdRaw) return

  const startDate = new Date(fdRaw.startDate)
  const now = new Date()
  const totalMonths = fdRaw.tenureMonths
  const monthsCompleted = Math.max(0, Math.min(totalMonths, Math.round((now - startDate) / (1000 * 60 * 60 * 24 * 30.44))))
  const progress = fdRaw.progress ?? Math.round((monthsCompleted / totalMonths) * 100)

  // Calculate current value based on progress
  const currentValue = fdRaw.amountInvested + ((fdRaw.maturityValue - fdRaw.amountInvested) * (progress / 100))
  const interestEarned = currentValue - fdRaw.amountInvested
  const interestRemaining = fdRaw.maturityValue - currentValue

  selectedFD.value = {
    ...fdRaw,
    amountInvested: `₹${Number(fdRaw.amountInvested).toLocaleString()}`,
    interestRate: `${fdRaw.interestRate}%`,
    tenure: `${fdRaw.tenureMonths} months`,
    startedOn: startDate.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }),
    maturityDate: new Date(fdRaw.maturityDate).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }),
    monthsCompleted,
    totalMonths,
    progress,
    currentValue: `₹${currentValue.toLocaleString(undefined, { maximumFractionDigits: 2 })}`,
    maturityValue: `₹${fdRaw.maturityValue.toLocaleString(undefined, { maximumFractionDigits: 2 })}`,
    interestEarned: `₹${interestEarned.toLocaleString(undefined, { maximumFractionDigits: 2 })}`,
    interestRemaining: `₹${interestRemaining.toLocaleString(undefined, { maximumFractionDigits: 2 })}`
  }
}

const handleBreakFD = async (depositId) => {
  try {
    const res = await fetchBreakPreview(depositId)
    const data = res.data
    breakFDModalData.value = {
      fdId: data.fdId,
      amountInvested: `₹${Number(data.originalAmount).toLocaleString()}`,
      currentValue: `₹${Number(data.computedPayout).toLocaleString()}`,
      penalty: `₹${Number(data.penaltyAmount).toLocaleString()}`,
      finalAmount: `₹${Number(data.computedPayout).toLocaleString()}`,
      interestLost: `₹${Number(data.interestLost).toLocaleString()}`,
      interestEarned: `₹${Number(data.interestEarned).toLocaleString()}`,
      breakDate: data.breakDate,
      penaltyRate: '1% of accrued interest'
    }
  } catch (e) {
    console.error('Error fetching break preview:', e)
  }
}

const closeModal = () => {
  selectedFD.value = null
}

const closeBreakFDModal = () => {
  breakFDModalData.value = null
}

const tabs = [
  { name: 'All', key: 'All' },
  { name: 'Active', key: 'Active' },
  { name: 'Matured', key: 'Matured' },
  { name: 'Broken', key: 'Broken' }
]

const filteredDeposits = computed(() => {
  if (activeTab.value === 'All') {
    return deposits.value
  }
  return deposits.value.filter(deposit => deposit.status === activeTab.value)
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredDeposits.value.length / itemsPerPage))
})

const paginatedDeposits = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredDeposits.value.slice(start, end)
})

const handleTabChange = (tabKey) => {
  activeTab.value = tabKey
  currentPage.value = 1 // Reset to first page when tab changes
}

const handlePageChange = (page) => {
  currentPage.value = page
}

const handleCreateNew = () => {
  console.log('Create new FD clicked')
  showCreateModal.value = true
}

const handleConfirmBreak = (fd) => {
  breakFDData.value = fd
  showBreakConfirmModal.value = true
}

// If filteredDeposits changes (tab switch), reset to page 1 if current page is out of bounds
watch(filteredDeposits, (newVal) => {
  if ((currentPage.value - 1) * itemsPerPage >= newVal.length) {
    currentPage.value = 1
  }
})
</script>