<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/10">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md mx-auto relative border border-gray-200 p-8">
      <h2 class="text-xl font-bold mb-4">Confirm Break Fixed Deposit</h2>
      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-2">Reason (optional)</label>
        <textarea
          v-model="reason"
          rows="3"
          class="w-full border border-gray-300 rounded-lg px-3 py-2"
          placeholder="Why do you want to break this FD?"
        ></textarea>
      </div>
      <div class="flex justify-end gap-3 mt-6">
        <button @click="$emit('cancel')" class="px-4 py-2 rounded bg-gray-100 text-gray-700 hover:bg-gray-200">Cancel</button>
        <button @click="confirm" class="px-4 py-2 rounded bg-red-600 text-white hover:bg-red-700">Confirm</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  fd: Object,
  reason: String
})

const emit = defineEmits(['cancel', 'confirm'])

const reason = ref(props.reason || '')

watch(() => props.reason, (val) => {
  reason.value = val || ''
})

function confirm() {
  emit('confirm', reason.value)
}
</script>