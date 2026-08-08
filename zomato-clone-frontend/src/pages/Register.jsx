import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'
import Navbar from '../components/Navbar'

const ROLE_OPTIONS = [
  { value: 'CUSTOMER', label: 'Order food' },
  { value: 'RESTAURANT_OWNER', label: 'List my restaurant' },
  { value: 'DELIVERY_PARTNER', label: 'Deliver orders' },
]

const LANDING_PATH = {
  CUSTOMER: '/',
  RESTAURANT_OWNER: '/owner/restaurants',
  DELIVERY_PARTNER: '/delivery/available',
}

export default function Register() {
  const { register, loading } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ fullName: '', email: '', phone: '', password: '' })
  const [roles, setRoles] = useState(['CUSTOMER'])
  const [error, setError] = useState('')

  function toggleRole(role) {
    setRoles((prev) => (prev.includes(role) ? prev.filter((r) => r !== role) : [...prev, role]))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    if (roles.length === 0) {
      setError('Pick at least one way you want to use the app.')
      return
    }
    try {
      const result = await register({ ...form, roles })
      navigate(LANDING_PATH[result.activeRole] || '/')
    } catch (err) {
      setError(err.response?.data?.message || 'Could not create your account.')
    }
  }

  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="mx-auto flex w-full max-w-sm flex-1 flex-col justify-center px-4 py-12">
        <h1 className="mb-1 text-2xl font-semibold">Create an account</h1>
        <p className="mb-6 text-sm text-ink/60">You can hold more than one role and switch anytime after signing up.</p>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="label" htmlFor="fullName">Full name</label>
            <input id="fullName" required className="input" value={form.fullName}
              onChange={(e) => setForm({ ...form, fullName: e.target.value })} />
          </div>
          <div>
            <label className="label" htmlFor="email">Email</label>
            <input id="email" type="email" required className="input" value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })} />
          </div>
          <div>
            <label className="label" htmlFor="phone">Phone</label>
            <input id="phone" className="input" value={form.phone}
              onChange={(e) => setForm({ ...form, phone: e.target.value })} />
          </div>
          <div>
            <label className="label" htmlFor="password">Password</label>
            <input id="password" type="password" required minLength={8} className="input" value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })} />
          </div>
          <div>
            <span className="label">I want to</span>
            <div className="space-y-2">
              {ROLE_OPTIONS.map((opt) => (
                <label key={opt.value} className="flex items-center gap-2 text-sm">
                  <input type="checkbox" checked={roles.includes(opt.value)} onChange={() => toggleRole(opt.value)} />
                  {opt.label}
                </label>
              ))}
            </div>
          </div>
          {error && <p className="text-sm text-chili">{error}</p>}
          <button type="submit" className="btn-primary w-full" disabled={loading}>
            {loading ? 'Creating account…' : 'Create account'}
          </button>
        </form>
        <p className="mt-6 text-center text-sm text-ink/60">
          Already have an account?{' '}
          <Link to="/login" className="font-medium text-ink underline">Log in</Link>
        </p>
      </main>
    </div>
  )
}
