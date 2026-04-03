import axios from 'axios'

export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export const apiClient = axios.create({
  baseURL: apiBaseUrl,
  headers: {
    'Content-Type': 'application/json',
  },
})

apiClient.interceptors.request.use((config) => {
  const token = window.localStorage.getItem('stridehub_access_token')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})
