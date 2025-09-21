<template>
  <button
    @click="$emit('click')"
    class="box-border flex w-56 h-[48px] items-center justify-center gap-2 px-6 py-0 relative rounded-3xl aspect-[3.73] bg-gradient-to-r from-[#0c1f16] via-[#1c4532] to-[#45ab7c]"
    style="background: linear-gradient(32deg,rgba(12,31,22,1) 2%,rgba(28,69,50,1) 30%,rgba(69,171,124,1) 100%);"
  >
    <!-- <svg class="w-5 h-5 text-[#f7fafc]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
    </svg> -->
    <span class="relative w-fit font-normal text-[#f7fafc] text-lg leading-7 whitespace-nowrap font-inter">
      Create New
    </span>
  </button>
</template>

<script setup>
defineEmits(['click'])
</script>