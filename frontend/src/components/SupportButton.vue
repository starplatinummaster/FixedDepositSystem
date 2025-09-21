<template>
  <button
    @click="$emit('open')"
    class="fixed bottom-6 right-6 z-50 bg-white shadow-lg rounded-full flex items-center justify-center hover:bg-gray-100 transition"
  >
    <img :src="support" alt="Support" class="w-[48px] h-[48px]" />
  </button>
</template>

<script setup>
import support from '../assets/support.png'
defineEmits(['open'])
</script>