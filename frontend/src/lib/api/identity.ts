import { apiClient } from './client'
import type { AuthResponse, UserProfile } from './types'

export interface LoginPayload {
  email: string
  password: string
}

export interface RegisterPayload {
  name: string
  email: string
  password: string
}

export interface RefreshPayload {
  refreshToken: string
}

export async function register(payload: RegisterPayload): Promise<AuthResponse> {
  const response = await apiClient.post<AuthResponse>('/api/v1/identity/register', payload)
  return response.data
}

export async function login(payload: LoginPayload): Promise<AuthResponse> {
  const response = await apiClient.post<AuthResponse>('/api/v1/identity/login', payload)
  return response.data
}

export async function refreshSession(payload: RefreshPayload): Promise<AuthResponse> {
  const response = await apiClient.post<AuthResponse>('/api/v1/identity/refresh', payload)
  return response.data
}

export async function getCurrentUser(): Promise<UserProfile> {
  const response = await apiClient.get<UserProfile>('/api/v1/identity/me')
  return response.data
}
