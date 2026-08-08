import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { orderApi } from '../../api/orderApi'
import Loader from '../../components/Loader'
import StatusBadge from '../../components/StatusBadge'
import Navbar from '../../components/Navbar'

export default function OrderDetail() {
  const { id } = useParams()
  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    orderApi.getById(id).then((res) => setOrder(res.data.data)).finally(() => setLoading(false))
  }, [id])

  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="mx-auto w-full max-w-lg flex-1 px-4 py-8">
        {loading && <Loader />}
        {!loading && !order && <p className="text-ink/60">Order not found, or you don't have access to it.</p>}
        {order && (
          <>
            <div className="mb-6 flex items-center justify-between">
              <h1 className="text-2xl font-semibold">Order #{order.id}</h1>
              <StatusBadge status={order.status} />
            </div>

            <div className="card">
              <p className="text-sm text-ink/60">{order.restaurantName}</p>
              <p className="mt-1 text-sm text-ink/60">Delivering to: {order.deliveryAddress}</p>

              <ul className="mt-4 divide-y divide-line">
                {order.items.map((item, i) => (
                  <li key={i} className="flex justify-between py-2 text-sm">
                    <span>{item.quantity} × {item.name}</span>
                    <span className="price">₹{item.subtotal}</span>
                  </li>
                ))}
              </ul>

              <div className="perforated-divider -mx-4 my-4" />

              <div className="flex justify-between text-base font-semibold">
                <span>Total</span>
                <span className="price">₹{order.totalAmount}</span>
              </div>
            </div>

            <div className="card mt-4">
              <h2 className="mb-3 text-sm font-semibold text-ink/70">Timeline</h2>
              <ol className="space-y-2">
                {order.statusHistory?.map((h, i) => (
                  <li key={i} className="flex items-center justify-between text-sm">
                    <StatusBadge status={h.status} />
                    <span className="text-ink/50">{new Date(h.changedAt).toLocaleString()}</span>
                  </li>
                ))}
              </ol>
            </div>
          </>
        )}
      </main>
    </div>
  )
}
