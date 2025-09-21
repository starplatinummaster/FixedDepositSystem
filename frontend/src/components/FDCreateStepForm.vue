<template>
  <div>
    <FDSectionCard class="mb-6">
      <h3 class="text-lg font-semibold text-gray-900 mb-4">Investment Amount *</h3>
      <div class="relative">
        <input
          v-model="amount"
          type="number"
          min="1000"
          max="1000000"
          placeholder="Enter amount (min: 1,000 & max: 10,00,000)"
          class="w-full bg-white border border-gray-200 rounded-lg px-4 py-3 text-base mb-2"
          @blur="validateAmount"
        />
        <span v-if="amountError" class="text-xs text-red-600 absolute left-0 -bottom-5">{{ amountError }}</span>
      </div>

      <h3 class="text-lg font-semibold text-gray-900 mb-4 mt-6">Investment Tenure *</h3>
      <div class="relative">
        <CustomDropdown
            :options="tenureOptions"
            v-model="selectedTenure"
            placeholder="Select Tenure"
        />
      </div>
      <span v-if="tenureError" class="text-xs text-red-600">{{ tenureError }}</span>
    </FDSectionCard>

    <FDSectionCard class="mb-6">
      <h3 class="text-lg font-semibold text-gray-900 mb-4">Preview Calculations</h3>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <FDOverviewItem label="Interest Rate" :value="preview.interestRate ? `${preview.interestRate}% p.a.` : '—'" />
        <FDOverviewItem label="Maturity Amount" :value="preview.maturityAmount ? `₹${preview.maturityAmount}` : '—'" />
        <FDOverviewItem label="Interest Earned" :value="preview.interestEarned ? `₹${preview.interestEarned}` : '—'" />
        <FDOverviewItem label="Compounding" value="Quarterly" />
      </div>
    </FDSectionCard>

    <div class="bg-[#FDE7C3] rounded-lg px-6 py-4 mb-6 text-[#A97B2F] text-base font-medium">
      <span class="font-bold">Early Breakdown Information</span>
      <ul class="mt-2 text-sm font-normal text-[#A97B2F] list-decimal ml-6">
        <li>Different interest rates apply based on time held</li>
        <li>Additional penalty of 1% on accrued interest</li>
        <li>Example: Breaking after 6 months = 4.5% interest rate + penalty</li>
      </ul>
    </div>

    <div class="flex justify-end mt-6">
      <button
        :disabled="!canProceed"
        @click="nextStep"
        class="bg-green-700 hover:bg-green-800 text-white px-6 py-3 rounded-lg font-medium text-base transition"
      >
        Next: Review Details
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import FDSectionCard from './FDSectionCard.vue'
import FDOverviewItem from './FDOverviewItem.vue'
import arrowDown from '../assets/arrowDown.png'
import CustomDropdown from './CustomDropdown.vue'

const props = defineProps({
  tenureOptions: { type: Array, required: true },
  initialFormData: { type: Object, default: () => ({}) },
  initialCalculation: { type: Object, default: () => ({}) }
})

const amount = ref(props.initialFormData.amount ?? '')

const preview = ref({
  interestRate: props.initialCalculation.interestRate ?? null,
  maturityAmount: props.initialCalculation.maturityAmount ?? null,
  interestEarned: props.initialCalculation.interestEarned ?? null
})

const amountError = ref('')
const tenureError = ref('')

const validateAmount = () => {
  const amt = Number(amount.value)
  if (!amt) {
    amountError.value = 'Amount is required'
  } else if (amt < 1000) {
    amountError.value = 'Minimum amount is ₹1,000'
  } else if (amt > 1000000) {
    amountError.value = 'Maximum amount is ₹10,00,000'
  } else {
    amountError.value = ''
  }
}

const selectedTenure = ref(null)

watch(amount, validateAmount)

watch(selectedTenure, () => {
  tenureError.value = selectedTenure.value ? '' : 'Tenure is required'
})

const canProceed = computed(() => {
  return !amountError.value && !tenureError.value && amount.value && selectedTenure.value
})

// Calculation logic (simple compound interest, quarterly)
watch([amount, selectedTenure], () => {
  const amt = Number(amount.value)
  const tenure = selectedTenure.value
  if (!amt || !tenure) {
    preview.value = { interestRate: null, maturityAmount: null, interestEarned: null }
    return
  }
  const rate = tenure.rate
  const months = tenure.months 
  const years = months / 12
  const n = 4 * years
  const r = rate / 4 / 100
  const maturityAmount = Math.round(amt * Math.pow(1 + r, n))
  const interestEarned = maturityAmount - amt
  preview.value = {
    interestRate: rate,
    maturityAmount,
    interestEarned
  }
})

const nextStep = () => {
  const tenure = selectedTenure.value
  const formData = {
    amount: Number(amount.value),
    tenureKey: tenure.key,
    rate: tenure.rate,
    tenureLabel: tenure.label
  }
  const calc = { ...preview.value }
  emit('next', formData, calc)
}

const emit = defineEmits(['next', 'close'])
</script>