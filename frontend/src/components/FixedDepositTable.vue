<template>
  <div class="rounded-xl border border-gray-200">
    <div class="grid grid-cols-6 gap-4 px-6 py-4 rounded-t-xl border-b border-gray-200 font-semibold text-gray-700 text-sm">
      <div>Amount Invested</div>
      <div>Interest Rate</div>
      <div>Maturity Date</div>
      <div>Progress</div>
      <div>Interest Accrued</div>
      <div>Status</div>
    </div>
    <div>
      <FixedDepositTableRow
        v-for="(deposit, idx) in deposits"
        :key="deposit.id"
        :deposit="deposit"
        :isLast="idx === deposits.length - 1"
        @view-details="handleViewDetails"
        @break-fd="handleBreakFD"
      />
    </div>
  </div>
</template>

<script setup>
import FixedDepositTableRow from './FixedDepositTableRow.vue'

const props = defineProps({
  deposits: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['view-details', 'break-fd'])

const handleViewDetails = (depositId) => {
  emit('view-details', depositId)
}

const handleBreakFD = (depositId) => {
  emit('break-fd', depositId)
}
</script>