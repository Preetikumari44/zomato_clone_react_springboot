import { useEffect, useState } from 'react'
import { adminApi } from '../../api/adminApi'
import Loader from '../../components/Loader'

function StatCard({ label, value }) {
  return (
    <div className="card">
      <p className="text-sm text-ink/50">{label}</p>
      <p className="font-display text-2xl font-semibold">{value}</p>
    </div>
  )
}

export default function Dashboard() {
  const [stats, setStats] = useState(null)

  useEffect(() => { adminApi.dashboard().then((res) => setStats(res.data.data)) }, [])

  if (!stats) return <Loader />

  return (
    <div>
      <h1 className="mb-6 text-2xl font-semibold">Dashboard</h1>
      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
        <StatCard label="Total users" value={stats.totalUsers} />
        <StatCard label="Customers" value={stats.totalCustomers} />
        <StatCard label="Restaurant owners" value={stats.totalRestaurantOwners} />
        <StatCard label="Delivery partners" value={stats.totalDeliveryPartners} />
        <StatCard label="Restaurants" value={stats.totalRestaurants} />
        <StatCard label="Pending approval" value={stats.pendingRestaurants} />
        <StatCard label="Total orders" value={stats.totalOrders} />
        <StatCard label="Revenue (delivered)" value={`₹${stats.totalRevenue}`} />
      </div>

      <h2 className="mb-3 mt-8 font-semibold">Orders by status</h2>
      <div className="card">
        <ul className="divide-y divide-line">
          {Object.entries(stats.ordersByStatus).map(([status, count]) => (
            <li key={status} className="flex justify-between py-2 text-sm">
              <span>{status.replaceAll('_', ' ')}</span>
              <span className="price">{count}</span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  )
}
