import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { orderApi } from '../../api/orderApi'
import Loader from '../../components/Loader'
import StatusBadge from '../../components/StatusBadge'
import Pagination from '../../components/Pagination'

export default function OrderHistory() {
  const [data, setData] = useState(null)
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    orderApi.listMine({ page, size: 10 }).then((res) => setData(res.data.data)).finally(() => setLoading(false))
  }, [page])

  if (loading) return <Loader />
  if (!data || data.content.length === 0) return <p className="py-12 text-center text-ink/50">No orders yet.</p>

  return (
    <div className="mx-auto max-w-2xl">
      <h1 className="mb-6 text-2xl font-semibold">Your orders</h1>
      <ul className="space-y-3">
        {data.content.map((o) => (
          <li key={o.id}>
            <Link to={`/orders/${o.id}`} className="card flex items-center justify-between hover:shadow-md">
              <div>
                <p className="font-medium">{o.restaurantName}</p>
                <p className="text-sm text-ink/50">{new Date(o.placedAt).toLocaleString()}</p>
              </div>
              <div className="flex items-center gap-3">
                <span className="price">₹{o.totalAmount}</span>
                <StatusBadge status={o.status} />
              </div>
            </Link>
          </li>
        ))}
      </ul>
      <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />
    </div>
  )
}
