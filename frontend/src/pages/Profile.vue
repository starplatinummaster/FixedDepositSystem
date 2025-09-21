<template>
  <div class="p-6 max-w-lg mx-auto">
    <h1 class="text-2xl font-bold mb-4">Profile</h1>
    <p><strong>Id:</strong> {{ profile.id }}</p>
    <p><strong>Name:</strong> {{ profile.name }}</p>
    <p><strong>Email:</strong> {{ profile.email }}</p>
    <p><strong>Role:</strong> {{ profile.role }}</p>
    <p><strong>Created At:</strong> {{ formattedCreatedAt }}</p>
    <button @click="logout" class="mt-4 px-4 py-2 bg-red-500 text-white rounded">
      Logout
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getProfile } from '../services/authService';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const router = useRouter();
const profile = ref({});

onMounted(async () => {
  const { data } = await getProfile();
  profile.value = data;
});

const formattedCreatedAt = computed(() => {
  if (!profile.value.createdAt) return '';
  return new Date(profile.value.createdAt + 'Z').toLocaleString('en-IN', {
    dateStyle: 'medium',
    timeStyle: 'short'
  });
});

const logout = () => {
  authStore.setToken(null);
  router.push('/');
};
</script>
