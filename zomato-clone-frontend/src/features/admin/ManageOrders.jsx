import { useEffect, useState } from 'react'
import { adminApi } from '../../api/adminApi'
import Loader from '../../components/Loader'
import StatusBadge from '../../components/StatusBadge'
import Pagination from '../../components/Pagination'

const STATUSES = ['', 'PLACED', 'ACCEPTED', 'REJECTED', 'PREPARING', 'READY_FOR_PICKUP', 'PICKED_UP', 'DELIVERED', 'CANCELLED']

export default function ManageOrders() {
  const [status, setStatus] = useState('')
  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    adminApi.listOrders({ status: status || undefined, page, size: 15 })
      .then((res) => setData(res.data.data)).finally(() => setLoading(false))
  }, [status, page])

  return (
    <div>
      <h1 className="mb-6 text-2xl font-semibold">All orders</h1>
      <select className="input mb-4 max-w-xs" value={status} onChange={(e) => { setPage(0); setStatus(e.target.value) }}>
        {STATUSES.map((s) => <option key={s} value={s}>{s ? s.replaceAll('_', ' ') : 'All statuses'}</option>)}
      </select>

      {loading && <Loader />}

      <ul className="space-y-2">
        {data?.content.map((o) => (
          <li key={o.id} className="card flex items-center justify-between">
            <div>
              <p className="font-medium">Order #{o.id} · {o.restaurantName}</p>
              <p className="text-sm text-ink/50">{o.customerName} · {new Date(o.placedAt).toLocaleString()}</p>
            </div>
            <div className="flex items-center gap-3">
              <span className="price">₹{o.totalAmount}</span>
              <StatusBadge status={o.status} />
            </div>
          </li>
        ))}
      </ul>
      {data && <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />}
    </div>
  )
}
