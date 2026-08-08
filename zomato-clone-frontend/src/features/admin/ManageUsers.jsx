import { useEffect, useState } from 'react'
import { adminApi } from '../../api/adminApi'
import Loader from '../../components/Loader'
import Pagination from '../../components/Pagination'

export default function ManageUsers() {
  const [keyword, setKeyword] = useState('')
  const [page, setPage] = useState(0)
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [busyId, setBusyId] = useState(null)

  function refresh() {
    setLoading(true)
    adminApi.listUsers({ keyword: keyword || undefined, page, size: 15 })
      .then((res) => setData(res.data.data)).finally(() => setLoading(false))
  }

  useEffect(() => { refresh() }, [keyword, page])

  async function deactivate(id) {
    if (!window.confirm('Deactivate this user?')) return
    setBusyId(id)
    try {
      await adminApi.deactivateUser(id)
      refresh()
    } finally {
      setBusyId(null)
    }
  }

  return (
    <div>
      <h1 className="mb-6 text-2xl font-semibold">Users</h1>
      <input className="input mb-4 max-w-xs" placeholder="Search name or email" value={keyword}
        onChange={(e) => { setPage(0); setKeyword(e.target.value) }} />

      {loading && <Loader />}

      <ul className="space-y-2">
        {data?.content.map((u) => (
          <li key={u.id} className="card flex items-center justify-between">
            <div>
              <p className="font-medium">{u.fullName} {!u.active && <span className="text-xs text-chili">(inactive)</span>}</p>
              <p className="text-sm text-ink/50">{u.email} · {u.roles.join(', ')}</p>
            </div>
            {u.active && (
              <button className="btn-danger" disabled={busyId === u.id} onClick={() => deactivate(u.id)}>
                Deactivate
              </button>
            )}
          </li>
        ))}
      </ul>
      {data && <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />}
    </div>
  )
}
