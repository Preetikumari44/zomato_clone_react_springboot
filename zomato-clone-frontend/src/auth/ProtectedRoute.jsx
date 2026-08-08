import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from './useAuth'

/**
 * Gates a route subtree by authentication and, optionally, by active role.
 * Role checks here are a UX convenience only — the backend enforces the
 * real RBAC via SecurityConfig + service-layer ownership checks, so this
 * component just avoids flashing a screen the API would reject anyway.
 */
export default function ProtectedRoute({ allowedRoles }) {
  const { user } = useAuth()

  if (!user) return <Navigate to="/login" replace />

  if (allowedRoles && !allowedRoles.includes(user.activeRole)) {
    return <Navigate to="/unauthorized" replace />
  }

  return <Outlet />
}
