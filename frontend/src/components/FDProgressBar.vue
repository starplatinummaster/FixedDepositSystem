<template>
  <div class="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
    <div
      class="h-2 bg-green-600 rounded-full transition-all duration-300"
      :style="{ width: `${progress}%` }"
    ></div>
  </div>
</template>

<script setup>
const props = defineProps({
  progress: {
    type: Number,
    required: true
  }
})
</script>