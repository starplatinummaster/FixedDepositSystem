<template>
  <nav class="px-4 py-2 flex-shrink-0">
    <div class="flex items-center justify-between">
      <!-- Logo -->
      <router-link
        to="/home"
        class="flex items-center space-x-3 hover:opacity-80 transition-opacity duration-200"
      >
        <div class="w-[48px] h-[48px] rounded-lg flex items-center justify-center">
          <img :src="logo" alt="Logo" class="w-[48px] h-[48px] object-contain" />
        </div>
      </router-link>

      <!-- User Profile -->
      <div v-if="authStore.isAuthenticated()" class="relative">
        <button
          @click="toggleProfileMenu"
          @blur="handleBlur"
          class="p-2 rounded-full hover:bg-gray-100 transition-colors duration-200"
        >
          <div class="w-[48px] h-[48px] rounded-lg flex items-center justify-center">
            <img :src="profile" alt="Logo" class="w-[48px] h-[48px] object-contain" />
          </div>
        </button>

        <!-- Profile Dropdown -->
        <Transition
          enter-active-class="transition ease-out duration-100"
          enter-from-class="transform opacity-0 scale-95"
          enter-to-class="transform opacity-100 scale-100"
          leave-active-class="transition ease-in duration-75"
          leave-from-class="transform opacity-100 scale-100"
          leave-to-class="transform opacity-0 scale-95"
        >
          <div
            v-if="isProfileMenuOpen"
            class="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg ring-1 ring-black ring-opacity-5 z-50"
          >
            <UserProfileModal
              :user="userProfile"
              @sign-out="handleSignOut"
              @close="closeProfileMenu"
            />
          </div>
        </Transition>
      </div>

      <!-- Auth Links for non-authenticated users -->
      <div v-else class="flex items-center space-x-4">
        <router-link
          to="/login"
          class="text-gray-600 hover:text-gray-900 font-medium transition-colors duration-200"
        >
          Login
        </router-link>
        <router-link
          to="/register"
          class="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200"
        >
          Register
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getProfile } from '../services/authService'
import { fetchFDs } from '../services/fdService'
import UserProfileModal from './UserProfileModal.vue'
import logo from '../assets/logo.png'
import profile from '../assets/profile.png'

const router = useRouter()
const authStore = useAuthStore()
const isProfileMenuOpen = ref(false)
const userProfile = ref({
  name: '',
  email: '',
  totalFDs: 0
})

onMounted(async () => {
  if (authStore.isAuthenticated()) {
    try {
      const profileRes = await getProfile()
      let fdCount = 0
      try {
        const fdsRes = await fetchFDs()
        fdCount = Array.isArray(fdsRes.data) ? fdsRes.data.length : 0
      } catch (fdErr) {
        console.error('Failed to fetch FDs:', fdErr)
      }
      userProfile.value = {
        name: profileRes.data.name,
        email: profileRes.data.email,
        totalFDs: fdCount
      }
    } catch (error) {
      console.error('Failed to load profile:', error)
    }
  }
})

const toggleProfileMenu = () => {
  isProfileMenuOpen.value = !isProfileMenuOpen.value
}

const closeProfileMenu = () => {
  isProfileMenuOpen.value = false
}

const handleBlur = (event) => {
  setTimeout(() => {
    if (!event.relatedTarget || !event.currentTarget || !event.currentTarget.contains(event.relatedTarget)) {
      isProfileMenuOpen.value = false
    }
  }, 150)
}

const handleSignOut = () => {
  authStore.setToken(null)
  router.push('/')
  closeProfileMenu()
}
</script>