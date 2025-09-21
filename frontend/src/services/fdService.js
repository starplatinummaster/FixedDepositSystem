import axios from 'axios'

const API = axios.create({
  baseURL: 'http://localhost:8080'
})

export const fetchFDs = () => {
  const token = localStorage.getItem('token')
  return API.get('/api/fd', {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
}

export const createFD = (fdData) => {
  const token = localStorage.getItem('token')
  return API.post('/api/fd', fdData, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
}

export const fetchBreakPreview = (fdId) => {
  const token = localStorage.getItem('token')
  return API.get(`/api/fd/${fdId}/break-preview`, {
    headers: { Authorization: `Bearer ${token}` }
  })
}

export const breakFD = (fdId, reason) => {
  const token = localStorage.getItem('token')
  return API.post(`/api/fd/${fdId}/break`, { reason }, {
    headers: { Authorization: `Bearer ${token}` }
  })
}