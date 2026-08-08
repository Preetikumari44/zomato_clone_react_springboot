import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'
import Navbar from '../components/Navbar'

const LANDING_PATH = {
  CUSTOMER: '/',
  RESTAURANT_OWNER: '/owner/restaurants',
  DELIVERY_PARTNER: '/delivery/available',
  ADMIN: '/admin/dashboard',
}

export default function Login() {
  const { login, loading } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    try {
      const result = await login(email, password)
      navigate(LANDING_PATH[result.activeRole] || '/')
    } catch (err) {
      setError(err.response?.data?.message || 'Could not log in. Check your email and password.')
    }
  }

  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="mx-auto flex w-full max-w-sm flex-1 flex-col justify-center px-4 py-12">
        <h1 className="mb-1 text-2xl font-semibold">Log in</h1>
        <p className="mb-6 text-sm text-ink/60">Welcome back — order something good.</p>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label" htmlFor="email">Email</label>
            <input id="email" type="email" required className="input" value={email} onChange={(e) => setEmail(e.target.value)} />
          </div>
          <div>
            <label className="label" htmlFor="password">Password</label>
            <input id="password" type="password" required className="input" value={password} onChange={(e) => setPassword(e.target.value)} />
          </div>
          {error && <p className="text-sm text-chili">{error}</p>}
          <button type="submit" className="btn-primary w-full" disabled={loading}>
            {loading ? 'Logging in…' : 'Log in'}
          </button>
        </form>
        <p className="mt-6 text-center text-sm text-ink/60">
          New here?{' '}
          <Link to="/register" className="font-medium text-ink underline">
            Create an account
          </Link>
        </p>
      </main>
    </div>
  )
}
