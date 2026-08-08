import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { restaurantApi } from '../../api/restaurantApi'
import Loader from '../../components/Loader'
import Pagination from '../../components/Pagination'

export default function RestaurantList() {
  const [keyword, setKeyword] = useState('')
  const [city, setCity] = useState('')
  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    restaurantApi
      .search({ keyword: keyword || undefined, city: city || undefined, page, size: 12 })
      .then((res) => setData(res.data.data))
      .finally(() => setLoading(false))
  }, [keyword, city, page])

  return (
    <div>
      <div className="mb-6 flex flex-col gap-3 sm:flex-row">
        <input
          className="input sm:max-w-xs"
          placeholder="Search restaurants or cuisine"
          value={keyword}
          onChange={(e) => { setPage(0); setKeyword(e.target.value) }}
        />
        <input
          className="input sm:max-w-xs"
          placeholder="City"
          value={city}
          onChange={(e) => { setPage(0); setCity(e.target.value) }}
        />
      </div>

      {loading && <Loader label="Finding restaurants…" />}

      {!loading && data?.content.length === 0 && (
        <p className="py-12 text-center text-ink/50">No restaurants match yet — try a different search.</p>
      )}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {data?.content.map((r) => (
          <Link key={r.id} to={`/restaurants/${r.id}`} className="card block transition-shadow hover:shadow-md">
            <div className="mb-3 aspect-video overflow-hidden rounded-md bg-line">
              {r.logoUrl && <img src={r.logoUrl} alt={r.name} className="h-full w-full object-cover" />}
            </div>
            <h3 className="font-display text-lg font-semibold">{r.name}</h3>
            <p className="text-sm text-ink/60">{r.cuisineType} · {r.city}</p>
          </Link>
        ))}
      </div>

      {data && <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />}
    </div>
  )
}
