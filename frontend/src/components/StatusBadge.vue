<template>
  <span :class="badgeClasses">
    {{ status }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: {
    type: String,
    required: true
  }
})

const badgeClasses = computed(() => {
  const base = 'inline-flex items-center justify-center gap-2.5 px-2.5 py-0 relative rounded border border-solid font-medium text-sm leading-7 whitespace-nowrap'
  switch (props.status) {
    case 'Active':
      return `${base} bg-[#28a7453d] border-[#28a745] text-[#28a745]`
    case 'Matured':
      return `${base} bg-[#8888881a] border-[#888888] text-[#888888]`
    case 'Broken':
      return `${base} bg-[#dc35451a] border-[#dc3545] text-[#dc3545]`
    default:
      return `${base} bg-gray-100 border-gray-300 text-gray-800`
  }
})
</script>