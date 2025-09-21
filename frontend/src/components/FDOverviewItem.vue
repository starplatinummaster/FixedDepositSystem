<template>
  <div class="bg-white rounded-lg border border-gray-200 px-4 py-3 flex flex-col justify-center h-full min-h-[80px]">
    <span class="text-sm text-gray-500 font-regular mb-1">{{ label }}</span>
    <span class="text-base text-gray-900 font-medium leading-tight break-words">
      <slot>{{ value }}</slot>
    </span>
  </div>
</template>

<script setup>
const props = defineProps({
  label: String,
  value: String
})
</script>