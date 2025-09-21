<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/10">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-2xl mx-auto relative border border-gray-200">
      <button
        @click="$emit('close')"
        class="absolute top-6 right-6 text-gray-500 hover:text-gray-700 flex items-center gap-1 px-3 py-1 rounded transition"
      >
        <span class="text-base font-medium">×</span>
        <span class="text-sm font-medium">Close</span>
      </button>
      <div ref="modalContent" class="p-8 max-h-[90vh] overflow-y-auto">
        <h2 class="text-2xl font-bold text-gray-900 mb-6">Book New Fixed Deposit</h2>
        <FDCreateStepForm
          v-if="step === 1"
          :tenureOptions="tenureOptions"
          :initialFormData="formData"
          :initialCalculation="calculation"
          @next="handleNext"
          @close="$emit('close')"
        />
        <FDCreateStepReview
          v-else
          :formData="formData"
          :calculation="calculation"
          @edit="step = 1"
          @confirm="handleConfirm"
          @close="$emit('close')"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import FDCreateStepForm from './FDCreateStepForm.vue'
import FDCreateStepReview from './FDCreateStepReview.vue'

const modalContent = ref(null)

onMounted(() => {
  document.body.classList.add('overflow-hidden')
})
onUnmounted(() => {
  document.body.classList.remove('overflow-hidden')
})


const tenureOptions = [
  { key: 1, label: '7-45 days (~1 month)', rate: 3.05, months: 1 },
  { key: 2, label: '46-179 days (~6 months)', rate: 5.05, months: 6 },
  { key: 3, label: '180-210 days (~7 months)', rate: 5.80, months: 7 },
  { key: 4, label: '211-364 days (~12 months)', rate: 6.05, months: 12 },
  { key: 5, label: '1-2 years (12-24 months)', rate: 6.25, months: 24 },
  { key: 6, label: '2-3 years (24-36 months)', rate: 6.45, months: 36 },
  { key: 7, label: '3-5 years (36-60 months)', rate: 6.30, months: 60 },
  { key: 8, label: '5-10 years (60-120 months)', rate: 6.05, months: 120 }
]

const step = ref(1)
const formData = ref({})
const calculation = ref({})

watch(step, () => {
  if (modalContent.value) {
    modalContent.value.scrollTop = 0
  }
})

const handleNext = (data, calc) => {
  formData.value = data
  calculation.value = calc
  step.value = 2
}

const emit = defineEmits(['close','confirm'])

const handleConfirm = () => {
    console.log(formData.value.amount, formData.value.tenureKey)
  emit('confirm', {
    amount: formData.value.amount,
    tenureKey: formData.value.tenureKey
  })
}
</script>