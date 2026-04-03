import React, { useEffect, useState } from 'react'
import { AuthContext } from './auth-context'
import { getCurrentUser } from '../lib/api/identity'
import type { AuthResponse, UserProfile } from '../lib/api/types'

const ACCESS_TOKEN_KEY = 'stridehub_access_token'
const REFRESH_TOKEN_KEY = 'stridehub_refresh_token'

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)

  const persistSession = (session: AuthResponse) => {
    window.localStorage.setItem(ACCESS_TOKEN_KEY, session.accessToken)
    window.localStorage.setItem(REFRESH_TOKEN_KEY, session.refreshToken)
    setUser(session.user)
  }

  const clearSession = () => {
    window.localStorage.removeItem(ACCESS_TOKEN_KEY)
    window.localStorage.removeItem(REFRESH_TOKEN_KEY)
    setUser(null)
  }

  const login = async (session: AuthResponse) => {
    persistSession(session)

    try {
      const profile = await getCurrentUser()
      setUser(profile)
    } catch {
      setUser(session.user)
    }
  }

  const logout = () => {
    clearSession()
    setLoading(false)
  }

  useEffect(() => {
    const accessToken = window.localStorage.getItem(ACCESS_TOKEN_KEY)

    if (!accessToken) {
      setLoading(false)
      return
    }

    void (async () => {
      try {
        const profile = await getCurrentUser()
        setUser(profile)
      } catch {
        clearSession()
      } finally {
        setLoading(false)
      }
    })()
  }, [])

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        isAuthenticated: Boolean(user),
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
