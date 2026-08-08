import { useEffect, useState } from 'react'
import { deliveryApi } from '../../api/deliveryApi'
import Loader from '../../components/Loader'

export default function AvailableDeliveries() {
  const [deliveries, setDeliveries] = useState(null)
  const [loading, setLoading] = useState(true)
  const [busyId, setBusyId] = useState(null)
  const [notice, setNotice] = useState('')

  function refresh() {
    setLoading(true)
    deliveryApi.listAvailable({ size: 30 }).then((res) => setDeliveries(res.data.data.content)).finally(() => setLoading(false))
  }

  useEffect(() => { refresh() }, [])

  async function claim(orderId) {
    setBusyId(orderId)
    setNotice('')
    try {
      await deliveryApi.accept(orderId)
      refresh()
    } catch (err) {
      setNotice(err.response?.data?.message || 'Could not claim this delivery.')
    } finally {
      setBusyId(null)
    }
  }

  if (loading) return <Loader />

  return (
    <div>
      <h1 className="mb-6 text-2xl font-semibold">Available for pickup</h1>
      {notice && <p className="mb-4 text-sm text-chili">{notice}</p>}
      {deliveries?.length === 0 && <p className="py-12 text-center text-ink/50">Nothing ready for pickup right now.</p>}
      <ul className="space-y-3">
        {deliveries?.map((d) => (
          <li key={d.assignmentId} className="card flex items-center justify-between">
            <div>
              <p className="font-medium">{d.restaurantName}</p>
              <p className="text-sm text-ink/50">{d.restaurantAddress} → {d.deliveryAddress}</p>
            </div>
            <div className="flex items-center gap-3">
              <span className="price">₹{d.totalAmount}</span>
              <button className="btn-primary" disabled={busyId === d.orderId} onClick={() => claim(d.orderId)}>
                {busyId === d.orderId ? 'Claiming…' : 'Accept delivery'}
              </button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  )
}
