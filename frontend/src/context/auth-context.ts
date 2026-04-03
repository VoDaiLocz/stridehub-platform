import { createContext } from 'react'
import type { AuthResponse, UserProfile } from '../lib/api/types'

export interface AuthContextType {
  user: UserProfile | null
  loading: boolean
  isAuthenticated: boolean
  login: (session: AuthResponse) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined)
