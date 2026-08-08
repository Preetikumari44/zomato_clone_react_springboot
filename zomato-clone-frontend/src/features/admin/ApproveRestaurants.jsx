import { useEffect, useState } from 'react'
import { adminApi } from '../../api/adminApi'
import { restaurantApi } from '../../api/restaurantApi'
import Loader from '../../components/Loader'

export default function ApproveRestaurants() {
  const [pending, setPending] = useState(null)
  const [loading, setLoading] = useState(true)
  const [busyId, setBusyId] = useState(null)

  function refresh() {
    setLoading(true)
    adminApi.listPendingRestaurants({ size: 30 }).then((res) => setPending(res.data.data.content)).finally(() => setLoading(false))
  }

  useEffect(() => { refresh() }, [])

  async function approve(id) {
    setBusyId(id)
    try {
      await restaurantApi.approve(id)
      refresh()
    } finally {
      setBusyId(null)
    }
  }

  async function reject(id) {
    const reason = window.prompt('Reason for rejecting this restaurant (shown to the owner):')
    if (reason === null) return
    setBusyId(id)
    try {
      await restaurantApi.reject(id, reason)
      refresh()
    } finally {
      setBusyId(null)
    }
  }

  if (loading) return <Loader />

  return (
    <div>
      <h1 className="mb-6 text-2xl font-semibold">Pending restaurant approvals</h1>
      {pending?.length === 0 && <p className="py-12 text-center text-ink/50">Nothing pending review.</p>}
      <ul className="space-y-3">
        {pending?.map((r) => (
          <li key={r.id} className="card flex items-center justify-between">
            <div>
              <p className="font-medium">{r.name}</p>
              <p className="text-sm text-ink/50">{r.ownerName} · {r.cuisineType} · {r.city}</p>
            </div>
            <div className="flex gap-2">
              <button className="btn-danger" disabled={busyId === r.id} onClick={() => reject(r.id)}>Reject</button>
              <button className="btn-primary" disabled={busyId === r.id} onClick={() => approve(r.id)}>Approve</button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  )
}
