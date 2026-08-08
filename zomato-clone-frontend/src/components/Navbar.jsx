import { Link } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'
import RoleSwitcher from './RoleSwitcher'

export default function Navbar() {
  const { user, logout } = useAuth()

  return (
    <header className="border-b border-line bg-white">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
        <Link to="/" className="font-display text-xl font-semibold">
          Zomato Clone
        </Link>
        <div className="flex items-center gap-4">
          {user?.activeRole === 'CUSTOMER' && (
            <>
              <Link to="/my-orders" className="text-sm font-medium text-ink/70 hover:text-ink">Orders</Link>
              <Link to="/cart" className="text-sm font-medium text-ink/70 hover:text-ink">Cart</Link>
              <Link to="/profile" className="text-sm font-medium text-ink/70 hover:text-ink">Profile</Link>
            </>
          )}
          {user ? (
            <>
              <RoleSwitcher />
              <span className="hidden text-sm text-ink/60 sm:inline">{user.fullName}</span>
              <button className="btn-secondary" onClick={logout}>
                Log out
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn-secondary">
                Log in
              </Link>
              <Link to="/register" className="btn-primary">
                Sign up
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  )
}
