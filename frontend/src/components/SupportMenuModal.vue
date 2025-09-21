<template>
  <div>
    <!-- Overlay -->
    <div
      class="fixed inset-0 z-40 bg-transparent"
      @click="$emit('close')"
    ></div>
    <!-- Modal -->
    <div class="fixed bottom-20 right-8 z-50">
      <div class="bg-white rounded-xl shadow-xl w-80 p-6 border border-gray-200">
        <div class="flex flex-col gap-3">
          <button
            @click="$emit('create-ticket')"
            class="w-full px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white font-medium transition"
          >
            Create Support Ticket
          </button>
          <button
            @click="$emit('view-tickets')"
            class="w-full px-4 py-2 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-900 font-medium transition"
          >
            View Old Tickets
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>

defineEmits(['close', 'create-ticket', 'view-tickets'])
</script>