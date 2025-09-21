<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/10">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-4xl min-h-[600px] mx-auto relative border border-gray-200">
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
        <h2 class="text-2xl font-bold text-gray-900 mb-8">
          Break Fixed Deposit - {{ fd.amountInvested }}
        </h2>

        <!-- Break Preview -->
        <div class="bg-gray-50 rounded-xl border border-gray-200 p-6 mb-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Break Preview</h3>
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <FDOverviewItem label="Current Value" :value="fd.currentValue" />
            <FDOverviewItem label="Penalty" :value="fd.penalty" />
            <FDOverviewItem label="Final Amount" :value="fd.finalAmount" />
            <FDOverviewItem label="Interest Lost" :value="fd.interestLost" />
          </div>
        </div>

        <!-- Important Notice -->
        <div class="flex items-center bg-[#FDE7C3] rounded-lg px-6 py-4 mb-6 text-[#A97B2F] text-base font-medium">
          <img :src="warning2" alt="Warning" class="w-6 h-6 mr-3 flex-shrink-0" />
          <span class="whitespace-normal">
            Important Notice Breaking your FD means you'll lose {{ fd.interestLost }} in potential earnings.
          </span>
        </div>

        <!-- Penalty Explanation -->
        <div class="bg-gray-50 rounded-xl border border-gray-200 p-6 mb-8">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Penalty Explanation</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FDOverviewItem label="Penalty Rate" class="text-sm">
              <span class="text-base font-medium text-gray-900">{{ fd.penaltyRate }}</span>
            </FDOverviewItem>
            <FDOverviewItem label="How it's calculated" class="text-sm">
              <span class="text-base font-medium text-gray-900">Accrued interest * penalty rate</span>
            </FDOverviewItem>
            <FDOverviewItem label="Deducted From" class="text-sm">
              <span class="text-base font-medium text-gray-900">Total receivable<br />amount</span>
            </FDOverviewItem>
          </div>
        </div>

        <!-- Footer Actions -->
        <div class="flex flex-col md:flex-row items-center justify-center gap-4 mt-2">
          <button
            @click="handleAdvisor"
            class="flex items-center gap-2 px-6 py-3 rounded-lg bg-[#28674B] hover:bg-green-900 text-white font-medium text-base transition w-full md:w-auto justify-center"
          >
            <img :src="message" alt="Message" class="w-5 h-5" />
            Talk to Our Advisor First
          </button>
          <button
            @click="handleBreak"
            class="flex items-center gap-2 px-6 py-3 rounded-lg border border-red-600 text-red-600 font-medium text-base transition hover:bg-red-50 w-full md:w-auto justify-center"
          >
            <img :src="warning" alt="Message" class="w-5 h-5" />
            Break Fixed Deposit
          </button>
        </div>

        <!-- Footer Note -->
        <div class="mt-6 text-sm text-gray-400 text-center">
          This action cannot be undone.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import FDOverviewItem from './FDOverviewItem.vue'
import warning2 from '../assets/warning2.png'
import message from '../assets/message.png'
import warning from '../assets/warning.png'



const props = defineProps({
  fd: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'advisor', 'confirm-break'])

const handleAdvisor = () => {
  emit('advisor', props.fd)
}

const handleBreak = () => {
  emit('confirm-break', props.fd)
}
</script>