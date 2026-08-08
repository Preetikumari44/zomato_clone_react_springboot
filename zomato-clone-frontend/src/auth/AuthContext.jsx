import { createContext, useEffect, useState } from 'react'
import { authApi } from '../api/authApi'

export const AuthContext = createContext(null)

function loadStoredUser() {
  try {
    const raw = localStorage.getItem('authUser')
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

function persist(authResponse) {
  localStorage.setItem('token', authResponse.token)
  const user = {
    userId: authResponse.userId,
    fullName: authResponse.fullName,
    email: authResponse.email,
    activeRole: authResponse.activeRole,
    availableRoles: authResponse.availableRoles,
  }
  localStorage.setItem('authUser', JSON.stringify(user))
  return user
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(loadStoredUser)
  const [loading, setLoading] = useState(false)

  // Keep tabs in sync if the token changes elsewhere (e.g. logout in another tab).
  useEffect(() => {
    const onStorage = (e) => {
      if (e.key === 'authUser') setUser(loadStoredUser())
    }
    window.addEventListener('storage', onStorage)
    return () => window.removeEventListener('storage', onStorage)
  }, [])

  async function login(email, password) {
    setLoading(true)
    try {
      const { data } = await authApi.login({ email, password })
      setUser(persist(data.data))
      return data.data
    } finally {
      setLoading(false)
    }
  }

  async function register(payload) {
    setLoading(true)
    try {
      const { data } = await authApi.register(payload)
      setUser(persist(data.data))
      return data.data
    } finally {
      setLoading(false)
    }
  }

  async function switchRole(role) {
    const { data } = await authApi.switchRole(role)
    setUser(persist(data.data))
    return data.data
  }

  function logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('authUser')
    setUser(null)
  }

  const value = { user, loading, login, register, switchRole, logout }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
