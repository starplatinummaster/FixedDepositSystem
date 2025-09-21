<template>
  <div class="relative w-full">
    <button
      @click="open = !open"
      class="w-full flex justify-between items-center border border-gray-200 rounded-lg px-4 py-3 bg-white text-base text-gray-900 focus:outline-none"
      :aria-expanded="open"
      type="button"
    >
      <span>
        {{ selected ? selected.label : placeholder }}
      </span>
      <img src="../assets/arrowDown.png" alt="Arrow Down" class="w-5 h-5 ml-2" />
    </button>
    <div
      v-if="open"
      class="absolute left-0 right-0 mt-2 bg-white border border-gray-200 rounded-lg shadow-lg z-10"
    >
      <ul>
        <li
          v-for="option in options"
          :key="option.value"
          @click="select(option)"
          class="px-4 py-3 cursor-pointer hover:bg-gray-100 text-base text-gray-900"
        >
          {{ option.label }} ({{ option.rate }}% p.a.)
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  options: Array,
  modelValue: [Object, null],
  placeholder: { type: String, default: 'Select Tenure' }
})

const emit = defineEmits(['update:modelValue'])

const open = ref(false)
const selected = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
  selected.value = val
})

const select = (option) => {
  selected.value = option
  emit('update:modelValue', option)
  open.value = false
}

// Optional: close dropdown on click outside
const handleClickOutside = (event) => {
  if (!event.target.closest('.relative')) {
    open.value = false
  }
}
window.addEventListener('click', handleClickOutside)
</script>