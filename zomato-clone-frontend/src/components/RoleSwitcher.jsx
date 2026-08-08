import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'

const LANDING_PATH = {
  CUSTOMER: '/',
  RESTAURANT_OWNER: '/owner/restaurants',
  DELIVERY_PARTNER: '/delivery/available',
  ADMIN: '/admin/dashboard',
}

export default function RoleSwitcher() {
  const { user, switchRole } = useAuth()
  const [busy, setBusy] = useState(false)
  const navigate = useNavigate()

  if (!user || user.availableRoles.length < 2) return null

  async function handleChange(e) {
    const role = e.target.value
    if (role === user.activeRole) return
    setBusy(true)
    try {
      await switchRole(role)
      navigate(LANDING_PATH[role] || '/')
    } finally {
      setBusy(false)
    }
  }

  return (
    <select
      className="rounded-md border border-line bg-white px-2 py-1 text-sm"
      value={user.activeRole}
      onChange={handleChange}
      disabled={busy}
      aria-label="Switch active role"
    >
      {user.availableRoles.map((role) => (
        <option key={role} value={role}>
          {role.replaceAll('_', ' ')}
        </option>
      ))}
    </select>
  )
}
