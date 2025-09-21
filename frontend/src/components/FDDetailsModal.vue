<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/10">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-3xl mx-auto relative border border-gray-200">
      <!-- Close Button -->
      <button
        @click="$emit('close')"
        class="absolute top-6 right-6 text-gray-500 hover:text-gray-700 flex items-center gap-1 px-3 py-1 rounded transition"
      >
        <span class="text-base font-medium">×</span>
        <span class="text-sm font-medium">Close</span>
      </button>

      <!-- Modal Content -->
      <div class="p-8">
        <!-- Title -->
        <h2 class="text-2xl font-bold text-gray-900 mb-6">{{ fd.amountInvested }} Fixed Deposit</h2>

        <!-- Investment Overview -->
        <FDSectionCard class="mb-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Investment Overview</h3>
          <div class="grid grid-cols-4 gap-4">
            <FDOverviewItem label="Amount Invested" :value="fd.amountInvested" />
            <FDOverviewItem label="Interest Rate" :value="fd.interestRate" />
            <FDOverviewItem label="Tenure" :value="fd.tenure" />
            <FDOverviewItem label="Status">
              <StatusBadge :status="fd.status" />
            </FDOverviewItem>
          </div>
        </FDSectionCard>

        <!-- Timeline -->
        <FDSectionCard class="mb-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Timeline</h3>
          <div class="grid grid-cols-2 gap-4 mb-2">
            <FDOverviewItem label="Started On" :value="fd.startedOn" />
            <FDOverviewItem label="Tenure" :value="fd.maturityDate" />
          </div>
          <div class="flex items-center gap-2 mb-2">
            <CalendarIcon class="w-4 h-4 text-gray-400" />
            <span class="text-sm text-gray-600">{{ fd.monthsCompleted }}/{{ fd.totalMonths }} months completed</span>
            <span class="ml-auto text-xs text-gray-500">{{ fd.progress }}% complete</span>
          </div>
          <FDProgressBar :progress="fd.progress" />
        </FDSectionCard>

        <!-- Financial Summary -->
        <FDSectionCard>
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Financial Summary</h3>
          <div class="grid grid-cols-4 gap-4">
            <FDOverviewItem label="Current Value" :value="fd.currentValue" />
            <FDOverviewItem label="Maturity Value" :value="fd.maturityValue" />
            <FDOverviewItem label="Interest Earned" :value="fd.interestEarned" />
            <FDOverviewItem label="Interest Remaining" :value="fd.interestRemaining" />
          </div>
        </FDSectionCard>
      </div>
    </div>
  </div>
</template>

<script setup>
import FDSectionCard from './FDSectionCard.vue'
import FDOverviewItem from './FDOverviewItem.vue'
import StatusBadge from './StatusBadge.vue'
import FDProgressBar from './FDProgressBar.vue'
import { CalendarIcon } from '@heroicons/vue/24/outline'

const props = defineProps({
  fd: {
    type: Object,
    required: true
  }
})

defineEmits(['close'])
</script>