import { useEffect, useState } from 'react'
import { restaurantApi } from '../../api/restaurantApi'
import { orderApi } from '../../api/orderApi'
import Loader from '../../components/Loader'
import StatusBadge from '../../components/StatusBadge'

const NEXT_STATUS = { ACCEPTED: 'PREPARING', PREPARING: 'READY_FOR_PICKUP' }

export default function IncomingOrders() {
  const [restaurants, setRestaurants] = useState([])
  const [restaurantId, setRestaurantId] = useState('')
  const [orders, setOrders] = useState(null)
  const [loading, setLoading] = useState(true)
  const [busyId, setBusyId] = useState(null)

  useEffect(() => {
    restaurantApi.listMine({ size: 50 }).then((res) => {
      const list = res.data.data.content
      setRestaurants(list)
      if (list.length > 0) setRestaurantId(String(list[0].id))
    })
  }, [])

  function refresh() {
    if (!restaurantId) return
    setLoading(true)
    orderApi.listForRestaurant(restaurantId, { size: 30 }).then((res) => setOrders(res.data.data.content)).finally(() => setLoading(false))
  }

  useEffect(() => { refresh() }, [restaurantId])

  async function act(action, orderId, ...args) {
    setBusyId(orderId)
    try {
      await action(orderId, ...args)
      refresh()
    } finally {
      setBusyId(null)
    }
  }

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Incoming orders</h1>
        {restaurants.length > 1 && (
          <select className="input max-w-xs" value={restaurantId} onChange={(e) => setRestaurantId(e.target.value)}>
            {restaurants.map((r) => <option key={r.id} value={r.id}>{r.name}</option>)}
          </select>
        )}
      </div>

      {loading && <Loader />}
      {!loading && orders?.length === 0 && <p className="py-12 text-center text-ink/50">No orders yet.</p>}

      <ul className="space-y-3">
        {orders?.map((o) => (
          <li key={o.id} className="card">
            <div className="mb-2 flex items-center justify-between">
              <div>
                <p className="font-medium">Order #{o.id} · {o.customerName}</p>
                <p className="text-sm text-ink/50">{new Date(o.placedAt).toLocaleString()}</p>
              </div>
              <StatusBadge status={o.status} />
            </div>
            <ul className="mb-3 text-sm text-ink/70">
              {o.items.map((item, i) => (
                <li key={i}>{item.quantity} × {item.name}</li>
              ))}
            </ul>
            <div className="flex items-center justify-between">
              <span className="price font-semibold">₹{o.totalAmount}</span>
              <div className="flex gap-2">
                {o.status === 'PLACED' && (
                  <>
                    <button className="btn-danger" disabled={busyId === o.id} onClick={() => act(orderApi.reject, o.id)}>Reject</button>
                    <button className="btn-primary" disabled={busyId === o.id} onClick={() => act(orderApi.accept, o.id)}>Accept</button>
                  </>
                )}
                {NEXT_STATUS[o.status] && (
                  <button className="btn-primary" disabled={busyId === o.id}
                    onClick={() => act(orderApi.updateStatus, o.id, NEXT_STATUS[o.status])}>
                    Mark {NEXT_STATUS[o.status].replaceAll('_', ' ').toLowerCase()}
                  </button>
                )}
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  )
}
