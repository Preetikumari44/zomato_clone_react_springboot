import { useEffect, useState } from 'react'
import { deliveryApi } from '../../api/deliveryApi'
import Loader from '../../components/Loader'
import Pagination from '../../components/Pagination'

export default function DeliveryHistory() {
  const [data, setData] = useState(null)
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    deliveryApi.listHistory({ page, size: 10 }).then((res) => setData(res.data.data)).finally(() => setLoading(false))
  }, [page])

  if (loading) return <Loader />
  if (!data || data.content.length === 0) return <p className="py-12 text-center text-ink/50">No completed deliveries yet.</p>

  return (
    <div>
      <h1 className="mb-6 text-2xl font-semibold">Delivery history</h1>
      <ul className="space-y-3">
        {data.content.map((d) => (
          <li key={d.assignmentId} className="card flex items-center justify-between">
            <div>
              <p className="font-medium">{d.restaurantName}</p>
              <p className="text-sm text-ink/50">Delivered {new Date(d.deliveredAt).toLocaleString()}</p>
            </div>
            <span className="price">₹{d.totalAmount}</span>
          </li>
        ))}
      </ul>
      <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />
    </div>
  )
}
