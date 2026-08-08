import { useState } from 'react'
import { authApi } from '../../api/authApi'
import { useAuth } from '../../auth/useAuth'

export default function Profile() {
  const { user } = useAuth()
  const [fullName, setFullName] = useState(user?.fullName || '')
  const [phone, setPhone] = useState('')
  const [saved, setSaved] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    await authApi.updateMe({ fullName, phone })
    setSaved(true)
  }

  return (
    <div className="mx-auto max-w-sm">
      <h1 className="mb-6 text-2xl font-semibold">Your profile</h1>
      <form onSubmit={handleSubmit} className="card space-y-4">
        <div>
          <label className="label">Full name</label>
          <input className="input" value={fullName} onChange={(e) => setFullName(e.target.value)} />
        </div>
        <div>
          <label className="label">Phone</label>
          <input className="input" value={phone} onChange={(e) => setPhone(e.target.value)} />
        </div>
        <div>
          <label className="label">Email</label>
          <input className="input bg-base" value={user?.email || ''} disabled />
        </div>
        {saved && <p className="text-sm text-basil">Saved.</p>}
        <button type="submit" className="btn-primary w-full">Save changes</button>
      </form>
    </div>
  )
}
