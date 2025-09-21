import axios from 'axios'

const API = axios.create({
  baseURL: 'http://localhost:8080'
})

export const createSupportTicket = (ticketData) => {
  const token = localStorage.getItem('token')
  return API.post('/api/support', ticketData, {
    headers: {
      Authorization: `Bearer ${token}`
    }
  })
}