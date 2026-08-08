import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { restaurantApi } from '../../api/restaurantApi'
import Loader from '../../components/Loader'
import StatusBadge from '../../components/StatusBadge'

const EMPTY_FORM = { name: '', description: '', cuisineType: '', address: '', city: '' }

export default function MyRestaurants() {
  const [restaurants, setRestaurants] = useState(null)
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(EMPTY_FORM)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  function refresh() {
    setLoading(true)
    return restaurantApi.listMine({ size: 50 }).then((res) => setRestaurants(res.data.data.content)).finally(() => setLoading(false))
  }

  useEffect(() => { refresh() }, [])

  async function handleCreate(e) {
    e.preventDefault()
    setSaving(true)
    setError('')
    try {
      await restaurantApi.create(form)
      setForm(EMPTY_FORM)
      setShowForm(false)
      await refresh()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not create restaurant.')
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <Loader />

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-semibold">My restaurants</h1>
        <button className="btn-primary" onClick={() => setShowForm((v) => !v)}>
          {showForm ? 'Cancel' : 'Register a restaurant'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleCreate} className="card mb-6 max-w-lg space-y-3">
          <div>
            <label className="label">Name</label>
            <input required className="input" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          </div>
          <div>
            <label className="label">Cuisine type</label>
            <input className="input" value={form.cuisineType} onChange={(e) => setForm({ ...form, cuisineType: e.target.value })} />
          </div>
          <div>
            <label className="label">Description</label>
            <textarea className="input" rows={2} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          </div>
          <div>
            <label className="label">Address</label>
            <input required className="input" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} />
          </div>
          <div>
            <label className="label">City</label>
            <input required className="input" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} />
          </div>
          {error && <p className="text-sm text-chili">{error}</p>}
          <button type="submit" className="btn-primary w-full" disabled={saving}>
            {saving ? 'Submitting…' : 'Submit for approval'}
          </button>
        </form>
      )}

      {restaurants?.length === 0 && !showForm && (
        <p className="py-12 text-center text-ink/50">You haven't registered a restaurant yet.</p>
      )}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {restaurants?.map((r) => (
          <div key={r.id} className="card">
            <div className="mb-2 flex items-center justify-between">
              <h3 className="font-display text-lg font-semibold">{r.name}</h3>
              <StatusBadge status={r.status} />
            </div>
            <p className="text-sm text-ink/60">{r.cuisineType} · {r.city}</p>
            {r.status === 'REJECTED' && r.rejectionReason && (
              <p className="mt-2 text-sm text-chili">Rejected: {r.rejectionReason}</p>
            )}
            <Link to={`/owner/restaurants/${r.id}/menu`} className="btn-secondary mt-3 inline-block">
              Manage menu
            </Link>
          </div>
        ))}
      </div>
    </div>
  )
}
