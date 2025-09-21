<template>
  <div class="flex items-center justify-between px-4 py-3 bg-white border border-gray-200 rounded-lg">
    <!-- Items info -->
    <div class="flex items-center text-sm text-gray-500">
      <span>
        Showing {{ startItem }} to {{ endItem }} of {{ totalItems }} results
      </span>
    </div>

    <!-- Pagination controls -->
    <div class="flex items-center space-x-2">
      <!-- Previous button -->
      <button
        @click="goToPreviousPage"
        :disabled="currentPage === 1"
        :class="[
          'px-3 py-2 text-sm font-medium rounded-md transition-colors duration-200',
          currentPage === 1
            ? 'text-gray-400 cursor-not-allowed'
            : 'text-gray-700 hover:bg-gray-100'
        ]"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
      </button>

    <!-- Page numbers -->
    <div class="flex space-x-1">
    <button
        v-for="page in visiblePages"
        :key="page"
        @click="goToPage(page)"
        :class="[
        'px-3 py-2 text-sm font-medium rounded-md transition-colors duration-200',
        page === currentPage
            ? 'text-white'
            : 'text-gray-700 hover:bg-gray-100'
        ]"
        :style="page === currentPage 
        ? 'background: linear-gradient(32deg,rgba(12,31,22,1) 2%,rgba(28,69,50,1) 30%,rgba(69,171,124,1) 100%)' 
        : ''"
    >
        {{ page }}
    </button>
    </div>

      <!-- Next button -->
      <button
        @click="goToNextPage"
        :disabled="currentPage === totalPages"
        :class="[
          'px-3 py-2 text-sm font-medium rounded-md transition-colors duration-200',
          currentPage === totalPages
            ? 'text-gray-400 cursor-not-allowed'
            : 'text-gray-700 hover:bg-gray-100'
        ]"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  totalItems: {
    type: Number,
    required: true
  },
  itemsPerPage: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['page-change'])

const startItem = computed(() => {
  return (props.currentPage - 1) * props.itemsPerPage + 1
})

const endItem = computed(() => {
  return Math.min(props.currentPage * props.itemsPerPage, props.totalItems)
})

const visiblePages = computed(() => {
  const delta = 2
  const range = []
  const rangeWithDots = []

  for (let i = Math.max(2, props.currentPage - delta); 
       i <= Math.min(props.totalPages - 1, props.currentPage + delta); 
       i++) {
    range.push(i)
  }

  if (props.currentPage - delta > 2) {
    rangeWithDots.push(1, '...')
  } else {
    rangeWithDots.push(1)
  }

  rangeWithDots.push(...range)

  if (props.currentPage + delta < props.totalPages - 1) {
    rangeWithDots.push('...', props.totalPages)
  } else if (props.totalPages > 1) {
    rangeWithDots.push(props.totalPages)
  }

  return rangeWithDots.filter((item, index, array) => array.indexOf(item) === index)
})

const goToPage = (page) => {
  if (page !== '...' && page !== props.currentPage) {
    emit('page-change', page)
  }
}

const goToPreviousPage = () => {
  if (props.currentPage > 1) {
    emit('page-change', props.currentPage - 1)
  }
}

const goToNextPage = () => {
  if (props.currentPage < props.totalPages) {
    emit('page-change', props.currentPage + 1)
  }
}
</script>