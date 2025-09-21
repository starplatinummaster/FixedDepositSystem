<template>
  <div class="relative">
    <!-- Menu Button -->
    <button
      @click="isOpen = !isOpen"
      @blur="handleBlur"
      class="p-1 rounded-full hover:bg-gray-100 transition-colors duration-150"
    >
      <svg class="w-5 h-5 text-gray-400" fill="currentColor" viewBox="0 0 24 24">
        <path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/>
      </svg>
    </button>

    <!-- Dropdown Menu -->
    <Transition
      enter-active-class="transition ease-out duration-100"
      enter-from-class="transform opacity-0 scale-95"
      enter-to-class="transform opacity-100 scale-100"
      leave-active-class="transition ease-in duration-75"
      leave-from-class="transform opacity-100 scale-100"
      leave-to-class="transform opacity-0 scale-95"
    >
      <div
        v-if="isOpen"
        class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none z-10"
      >
        <div class="py-1">
          <button
            @click="handleViewDetails"
            class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors duration-150"
          >
            View Details
          </button>
          <button
            v-if="canBreak"
            @click="handleBreakFD"
            class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors duration-150"
          >
            Break FD
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { fetchBreakPreview } from '../services/fdService'

const isOpen = ref(false)
const breakFDData = ref(null)
const breakFDModalOpen = ref(false)

const props = defineProps({
  depositId: {
    type: [String, Number],
    required: true
  },
  canBreak: {
    type: Boolean,
    default: false
  }
})

const handleBlur = (event) => {
  const currentTarget = event.currentTarget
  const relatedTarget = event.relatedTarget
  setTimeout(() => {
    if (!relatedTarget) {
      isOpen.value = false
    } else if (currentTarget && !currentTarget.contains(relatedTarget)) {
      isOpen.value = false
    }
  }, 150)
}

const emit = defineEmits(['view-details', 'break-fd'])

const handleViewDetails = () => {
  isOpen.value = false
  emit('view-details', props.depositId)
}

const handleBreakFD = () => {
  isOpen.value = false
  emit('break-fd', props.depositId)
}
</script>