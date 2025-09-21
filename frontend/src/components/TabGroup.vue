<template>
  <div class="w-[528px] h-[48px]">
    <div class="bg-[#f4f8f9] rounded-xl border border-solid border-[#f3f3f3] w-[528px] h-[48px] flex items-center">
      <div class="inline-flex gap-1 ml-[3px]">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          :class="tabClasses(tab)"
          @click="$emit('tab-change', tab.key)"
        >
          <span class="relative w-fit font-normal text-[#111111] text-base leading-7 whitespace-nowrap font-inter">
            {{ tab.name }}
          </span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  tabs: {
    type: Array,
    required: true
  },
  activeTab: {
    type: String,
    required: true
  }
})

const tabClasses = (tab) => {
  const base = 'flex w-[127px] h-[40px] justify-center gap-2.5 p-2.5 rounded-xl items-center relative font-normal text-base leading-7 whitespace-nowrap'
  if (tab.key === props.activeTab) {
    // Active tab style
    return `${base} bg-white border border-solid border-[#eff1f3]`
  }
  // Inactive tab style
  return `${base} bg-transparent text-[#111111]`
}
</script>