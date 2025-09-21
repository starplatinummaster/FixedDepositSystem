<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/10">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-lg mx-auto relative border border-gray-200">
      <div class="p-8">
        <h2 class="text-xl font-bold text-gray-900 mb-6 flex items-center gap-2">
          <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2a2 2 0 012-2h2a2 2 0 012 2v2m-6 0h6m-6 0a2 2 0 01-2-2V7a2 2 0 012-2h6a2 2 0 012 2v8a2 2 0 01-2 2h-6z" />
          </svg>
          Create New Ticket
        </h2>
        <form @submit.prevent="handleSubmit">
          <!-- Related FD -->
          <label class="block text-base font-semibold text-gray-900 mb-1">Related FD <span class="text-gray-400 text-sm">(Optional)</span></label>
          <div class="relative">
            <select v-model="relatedFD" class="w-full border border-gray-200 rounded-lg px-4 py-3 pr-10 mb-2 text-base appearance-none">
              <option value="">Select Fixed Deposit (Optional)</option>
              <option v-for="fd in fdOptions" :key="fd.id" :value="fd.id">
                {{ fd.label }}
              </option>
            </select>
            <img src="../assets/arrowDown.png" alt="Arrow Down" class="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 pointer-events-none" />
          </div>
          <!-- Subject -->
          <label class="block text-base font-semibold text-gray-900 mb-1">Subject <span class="text-gray-400 text-sm">(Required)</span></label>
          <input
            v-model="subject"
            type="text"
            maxlength="100"
            placeholder="Brief description of your issue"
            class="w-full border border-gray-200 rounded-lg px-4 py-3 mb-4 text-base"
            required
          />
          <!-- Description -->
          <label class="block text-base font-semibold text-gray-900 mb-1">Description <span class="text-gray-400 text-sm">(Required)</span></label>
          <textarea
            v-model="description"
            maxlength="500"
            placeholder="Please describe your issue in detail..."
            class="w-full border border-gray-200 rounded-lg px-4 py-3 mb-2 text-base min-h-[120px] resize-none"
            required
          ></textarea>
          <div class="text-xs text-gray-400 text-right mb-4">{{ description.length }} / 500</div>
          <!-- Actions -->
          <div class="flex justify-end gap-4 mt-6">
            <button
              type="button"
              @click="$emit('close')"
              class="bg-white border border-gray-300 text-gray-900 px-6 py-3 rounded-lg font-medium text-base transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg font-medium text-base transition"
            >
              Submit Ticket
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  fdOptions: { type: Array, default: () => [] }
})

watch(() => props.fdOptions, (newOptions) => {
  console.log('SupportTicketModal received fdOptions:', newOptions)
  if (newOptions && newOptions.length > 0) {
    console.log('First FD option:', newOptions[0])
  }
}, { immediate: true })

const relatedFD = ref('')
const subject = ref('')
const description = ref('')

const emit = defineEmits(['close', 'submit'])

const handleSubmit = () => {
  emit('submit', {
    relatedFD: relatedFD.value,
    subject: subject.value,
    description: description.value
  })
}
</script>