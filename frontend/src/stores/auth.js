import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getProfile } from '../services/authService';

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null);
  const user = ref(null);
  const userLoading = ref(false);

  const setToken = (newToken) => {
    token.value = newToken;
    if (newToken) {
      localStorage.setItem('token', newToken);
      fetchUser(); // Fetch user info when token is set
    } else {
      localStorage.removeItem('token');
      user.value = null; // Clear user info when token is removed
    }
  };

  const fetchUser = async () => {
    if (!token.value) return;
    
    userLoading.value = true;
    try {
      const response = await getProfile();
      user.value = response.data;
      console.log('User fetched:', response.data);
    } catch (error) {
      console.error('Failed to fetch user profile:', error);
      // If profile fetch fails, clear token
      setToken(null);
    } finally {
      userLoading.value = false;
    }
  };

  const isAuthenticated = () => !!token.value;

  // Initialize user data if token exists
  if (token.value && !user.value) {
    fetchUser();
  }

  return { token, user, userLoading, setToken, fetchUser, isAuthenticated };
});
