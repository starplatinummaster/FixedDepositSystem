<template>
  <div :class="rowClass">
    <div class="flex-1 grid grid-cols-6 gap-4 items-center">
      <div class="text-sm text-gray-900 flex items-center h-full">{{ deposit.amountInvested }}</div>
      <div class="text-sm text-gray-900 flex items-center h-full">{{ deposit.interestRate }}</div>
      <div class="text-sm text-gray-900 flex items-center h-full">{{ deposit.maturityDate }}</div>
      <div class="text-sm text-gray-900 flex items-center h-full">{{ deposit.progress }}% Complete</div>
      <div class="text-sm text-gray-900 flex items-center h-full">{{ deposit.interestAccrued }}</div>
      <div class="flex items-center h-full">
        <StatusBadge :status="deposit.status" />
      </div>
    </div>
    <div class="flex-shrink-0 flex items-center h-full">
      <KebabMenu 
        :depositId="deposit.id"
        :canBreak="deposit.status === 'Active'"
        @view-details="$emit('view-details', $event)"
        @break-fd="$emit('break-fd', deposit.id)"
      />
    </div>
  </div>
</template>

<script setup>

import StatusBadge from './StatusBadge.vue'
import KebabMenu from './KebabMenu.vue'

const props = defineProps({
  deposit: {
    type: Object,
    required: true
  },
  isLast: {
    type: Boolean,
    default: false
  }
})

const rowClass = props.isLast
  ? 'flex items-center px-6 py-4 gap-6 bg-white rounded-b-xl'
  : 'flex items-center px-6 py-4 gap-6 border-b border-gray-200 bg-white'

defineEmits(['view-details', 'break-fd'])
</script>