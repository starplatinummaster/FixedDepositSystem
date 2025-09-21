import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080', // Adjust if needed for Docker network
});

export const register = (user) => API.post('/auth/register', user);
export const login = (credentials) => API.post('/auth/login', credentials);
export const getProfile = () => {
  const token = localStorage.getItem("token");
  return API.get('/auth/me', {
    headers: {
      Authorization: `Bearer ${token}`
    }
  });
};
