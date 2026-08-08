import { useEffect, useState } from 'react'
import { deliveryApi } from '../../api/deliveryApi'
import Loader from '../../components/Loader'
import StatusBadge from '../../components/StatusBadge'

const NEXT_ACTION = {
  ASSIGNED: { label: 'Mark picked up', call: (id) => deliveryApi.markPickedUp(id) },
  PICKED_UP: { label: 'Mark delivered', call: (id) => deliveryApi.markDelivered(id) },
}

export default function AssignedOrders() {
  const [deliveries, setDeliveries] = useState(null)
  const [loading, setLoading] = useState(true)
  const [busyId, setBusyId] = useState(null)

  function refresh() {
    setLoading(true)
    deliveryApi.listAssigned({ size: 30 }).then((res) => setDeliveries(res.data.data.content)).finally(() => setLoading(false))
  }

  useEffect(() => { refresh() }, [])

  async function advance(orderId) {
    const action = NEXT_ACTION[deliveries.find((d) => d.orderId === orderId).status]
    if (!action) return
    setBusyId(orderId)
    try {
      await action.call(orderId)
      refresh()
    } finally {
      setBusyId(null)
    }
  }

  if (loading) return <Loader />

  return (
    <div>
      <h1 className="mb-6 text-2xl font-semibold">My deliveries</h1>
      {deliveries?.length === 0 && <p className="py-12 text-center text-ink/50">No active deliveries — check Available.</p>}
      <ul className="space-y-3">
        {deliveries?.map((d) => (
          <li key={d.assignmentId} className="card">
            <div className="mb-2 flex items-center justify-between">
              <p className="font-medium">{d.restaurantName}</p>
              <StatusBadge status={d.status} />
            </div>
            <p className="mb-3 text-sm text-ink/60">{d.restaurantAddress} → {d.deliveryAddress}</p>
            <div className="flex items-center justify-between">
              <span className="price">₹{d.totalAmount}</span>
              {NEXT_ACTION[d.status] && (
                <button className="btn-primary" disabled={busyId === d.orderId} onClick={() => advance(d.orderId)}>
                  {busyId === d.orderId ? 'Updating…' : NEXT_ACTION[d.status].label}
                </button>
              )}
            </div>
          </li>
        ))}
      </ul>
    </div>
  )
}
