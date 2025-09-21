<template>
  <div class="min-h-screen flex">
    <!-- Logo -->
    <div class="absolute top-6 left-6 z-10">
      <router-link to="/" class="flex items-center">
        <img
          src="/src/assets/logo.png"
          alt="Logo"
          class="h-16 w-auto rounded-lg cursor-pointer hover:opacity-80 transition-opacity"
        />
      </router-link>
    </div>

    <!-- Left side - Login Form -->
    <div class="w-1/2 flex items-center justify-center bg-gray-50 px-4 sm:px-6 lg:px-20 xl:px-24">
      <div class="mx-auto w-full max-w-sm lg:w-96">
        <div class="mb-8">
          <h2 class="text-3xl font-bold text-gray-900">Sign In</h2>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-6">
          <!-- Email -->
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 mb-2">E-mail</label>
            <input
              id="email"
              type="email"
              v-model="email"
              placeholder="example@gmail.com"
              required
              class="w-full px-3 py-3 border border-gray-300 rounded-md placeholder-gray-400 
                     focus:outline-none focus:ring-2 focus:border-gradient focus:ring-gradient"
            />
          </div>

          <!-- Password -->
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-2">Password</label>
            <div class="relative">
              <input
                id="password"
                :type="showPassword ? 'text' : 'password'"
                v-model="password"
                placeholder="@#*%"
                required
                class="w-full px-3 py-3 border border-gray-300 rounded-md placeholder-gray-400 
                       focus:outline-none focus:ring-2 focus:border-gradient focus:ring-gradient"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 pr-3 flex items-center"
              >
                <svg v-if="!showPassword" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                        d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                        d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 
                           9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 
                           0-8.268-2.943-9.542-7z" />
                </svg>
                <svg v-else class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                        d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 
                           0-8.268-2.943-9.543-7a9.97 9.97 0 
                           011.563-3.029m5.858.908a3 3 0 
                           114.243 4.243M9.878 9.878l4.242 
                           4.242M9.878 9.878L3 3m6.878 
                           6.878L21 21" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Submit -->
          <div>
            <button
              type="submit"
              :disabled="loading"
              class="w-full flex justify-center py-3 px-4 border border-transparent rounded-md 
                     shadow-sm text-sm font-medium text-white focus:outline-none focus:ring-2 
                     focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed"
              :style="{
                background: 'linear-gradient(32deg,rgba(12,31,22,1) 2%,rgba(28,69,50,1) 30%,rgba(69,171,124,1) 100%)'
              }"
            >
              <span v-if="loading">Signing in...</span>
              <span v-else>Sign in</span>
            </button>
          </div>

          <!-- Error -->
          <div v-if="error" class="text-red-600 text-sm">
            {{ error }}
          </div>

          <!-- Register link -->
          <div class="text-center">
            <p class="text-sm text-gray-600">
              Don't have an account?
              <router-link to="/register" class="font-medium text-green-700 hover:text-green-600 underline">
                Create now
              </router-link>
            </p>
          </div>
        </form>
      </div>
    </div>

    <!-- Right side - Image -->
    <div class="hidden lg:block relative w-1/2">
      <video
        src="/src/assets/bg.mp4"
        autoplay
        loop
        muted
        playsinline
        class="absolute inset-0 w-full h-full object-cover rounded-[2.5rem] border-8 border-white"
      ></video>
      <div class="absolute inset-0 rounded-[2.5rem]"></div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { login } from '../services/authService';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const email = ref('');
const password = ref('');
const showPassword = ref(false);
const loading = ref(false);
const error = ref(null);

const handleLogin = async () => {
  loading.value = true;
  error.value = null;

  try {
    const response = await login({ email: email.value, password: password.value });
    authStore.setToken(response.data.token);
    router.push('/home');
  } catch (err) {
    error.value = err.response?.data?.details || err.response?.data?.error || err.response?.data?.message || 'Invalid email or password';
  } finally {
    loading.value = false;
  }
};
</script>
