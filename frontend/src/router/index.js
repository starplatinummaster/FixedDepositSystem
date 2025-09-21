import { createRouter, createWebHistory } from 'vue-router';
import Landing from '../pages/Landing.vue';
import Home from '../pages/Home.vue';
import Login from '../pages/Login.vue';
import Register from '../pages/Register.vue';
import Profile from '../pages/Profile.vue';
import SupportPage from '../components/SupportPage.vue';
import { useAuthStore } from '../stores/auth';

const routes = [
  {
    path: '/',
    name: 'root',
    beforeEnter: (to, from, next) => {
      const authStore = useAuthStore();
      if (authStore.isAuthenticated()) {
        next('/home'); // logged in → go to Home
      } else {
        next('/landing'); // logged out → go to Landing
      }
    }
  },
  { path: '/landing', name: 'landing', component: Landing },
  { path: '/home', name: 'home', component: Home },
  { path: '/login', name: 'login', component: Login },
  { path: '/register', name: 'register', component: Register },
  { path: '/profile', name: 'profile', component: Profile },
  { 
    path: '/support', 
    name: 'support', 
    component: SupportPage,
    beforeEnter: (to, from, next) => {
      const authStore = useAuthStore();
      if (authStore.isAuthenticated()) {
        next();
      } else {
        next('/login');
      }
    }
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
