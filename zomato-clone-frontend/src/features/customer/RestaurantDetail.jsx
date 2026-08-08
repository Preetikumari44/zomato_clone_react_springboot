import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { restaurantApi } from '../../api/restaurantApi'
import { menuApi } from '../../api/menuApi'
import { cartApi } from '../../api/cartApi'
import Loader from '../../components/Loader'

export default function RestaurantDetail() {
  const { id } = useParams()
  const [restaurant, setRestaurant] = useState(null)
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [busyItemId, setBusyItemId] = useState(null)
  const [notice, setNotice] = useState('')

  useEffect(() => {
    setLoading(true)
    Promise.all([restaurantApi.getById(id), menuApi.search(id, { size: 50 })])
      .then(([rRes, mRes]) => {
        setRestaurant(rRes.data.data)
        setItems(mRes.data.data.content)
      })
      .finally(() => setLoading(false))
  }, [id])

  async function addToCart(menuItemId, replaceCart = false) {
    setBusyItemId(menuItemId)
    setNotice('')
    try {
      await cartApi.addItem({ menuItemId, quantity: 1, replaceCart })
      setNotice('Added to cart.')
    } catch (err) {
      if (err.response?.status === 400 && !replaceCart && err.response?.data?.message?.includes('replaceCart')) {
        const confirmed = window.confirm(err.response.data.message)
        if (confirmed) return addToCart(menuItemId, true)
      } else {
        setNotice(err.response?.data?.message || 'Could not add item.')
      }
    } finally {
      setBusyItemId(null)
    }
  }

  if (loading) return <Loader />
  if (!restaurant) return <p className="text-ink/60">Restaurant not found.</p>

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-3xl font-semibold">{restaurant.name}</h1>
        <p className="text-ink/60">{restaurant.cuisineType} · {restaurant.address}, {restaurant.city}</p>
        {restaurant.description && <p className="mt-2 max-w-2xl text-sm text-ink/70">{restaurant.description}</p>}
      </div>

      {notice && <p className="mb-4 text-sm text-basil">{notice}</p>}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {items.map((item) => (
          <div key={item.id} className="card flex items-start justify-between gap-4">
            <div>
              <h3 className="font-medium">
                <span className={`mr-2 inline-block h-2.5 w-2.5 rounded-full align-middle ${item.veg ? 'bg-basil' : 'bg-chili'}`} />
                {item.name}
              </h3>
              {item.description && <p className="mt-1 text-sm text-ink/60">{item.description}</p>}
              <p className="price mt-2">₹{item.price}</p>
            </div>
            <button
              className="btn-secondary shrink-0"
              disabled={!item.available || busyItemId === item.id}
              onClick={() => addToCart(item.id)}
            >
              {!item.available ? 'Unavailable' : busyItemId === item.id ? 'Adding…' : 'Add'}
            </button>
          </div>
        ))}
      </div>
    </div>
  )
}
