<template>
  <div>
    <FDSectionCard class="mb-6">
      <h3 class="text-lg font-semibold text-gray-900 mb-4">Investment Summary</h3>
      <div class="grid grid-cols-2 md:grid-cols-2 gap-4">
        <FDOverviewItem label="Amount Invested" :value="`₹${formData.amount.toLocaleString()}`" />
        <FDOverviewItem label="Interest Rate" :value="`${formData.rate}% p.a.`" />
        <FDOverviewItem label="Tenure" :value="formData.tenureLabel" />
        <FDOverviewItem label="Start Date" :value="startDate" />
        <FDOverviewItem label="Maturity Date" :value="maturityDate" />
        <FDOverviewItem label="Maturity Amount" :value="`₹${calculation.maturityAmount.toLocaleString()}`" />
        <FDOverviewItem label="Interest Earned" :value="`₹${calculation.interestEarned.toLocaleString()}`" />
      </div>
    </FDSectionCard>

    <FDSectionCard class="mb-6">
      <h3 class="text-lg font-semibold text-gray-900 mb-2">Terms & Conditions</h3>
      <ul class="text-sm text-gray-700 ml-4 list-decimal">
        <li>Interest is compounded quarterly</li>
        <li>Early withdrawal subject to penalty charges</li>
        <li>Minimum lock-in of 7 day period applies</li>
      </ul>
    </FDSectionCard>

    <div class="bg-[#FDE7C3] rounded-lg px-6 py-4 mb-6 text-[#A97B2F] text-base font-medium">
      <span class="font-bold">Early Breakdown Information</span>
      <ul class="mt-2 text-sm font-normal text-[#A97B2F] list-decimal ml-6">
        <li>Different interest rates apply based on time held</li>
        <li>Additional penalty of 1% on accrued interest</li>
        <li>Example: Breaking after 6 months = 4.5% interest rate + penalty</li>
      </ul>
    </div>

    <div class="flex justify-end gap-4 mt-6">
      <button
        @click="$emit('edit')"
        class="bg-white border border-gray-300 text-gray-900 px-6 py-3 rounded-lg font-medium text-base transition"
      >
        Edit Details
      </button>
      <button
        @click="$emit('confirm')"
        class="bg-green-700 hover:bg-green-800 text-white px-6 py-3 rounded-lg font-medium text-base transition"
      >
        Confirm & Book FD
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import FDSectionCard from './FDSectionCard.vue'
import FDOverviewItem from './FDOverviewItem.vue'

const props = defineProps({
  formData: Object,
  calculation: Object
})

const startDate = computed(() => {
  // Today, formatted
  const today = new Date()
  return `Today (${today.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })})`
})

const maturityDate = computed(() => {
  // Add tenure months to today
  const today = new Date()
  const maturity = new Date(today.setMonth(today.getMonth() + Number(props.formData.tenure)))
  return maturity.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
})
</script>