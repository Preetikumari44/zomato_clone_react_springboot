import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { cartApi } from '../../api/cartApi'
import { orderApi } from '../../api/orderApi'
import Loader from '../../components/Loader'

export default function Cart() {
  const [cart, setCart] = useState(null)
  const [loading, setLoading] = useState(true)
  const [address, setAddress] = useState('')
  const [placing, setPlacing] = useState(false)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  function refresh() {
    setLoading(true)
    return cartApi.get().then((res) => setCart(res.data.data)).finally(() => setLoading(false))
  }

  useEffect(() => { refresh() }, [])

  async function updateQty(menuItemId, quantity) {
    if (quantity < 1) return removeItem(menuItemId)
    const { data } = await cartApi.updateItem(menuItemId, quantity)
    setCart(data.data)
  }

  async function removeItem(menuItemId) {
    const { data } = await cartApi.removeItem(menuItemId)
    setCart(data.data)
  }

  async function placeOrder(e) {
    e.preventDefault()
    setError('')
    setPlacing(true)
    try {
      const { data } = await orderApi.place({ deliveryAddress: address })
      navigate(`/orders/${data.data.id}`)
    } catch (err) {
      setError(err.response?.data?.message || 'Could not place your order.')
    } finally {
      setPlacing(false)
    }
  }

  if (loading) return <Loader />
  if (!cart || cart.items.length === 0) {
    return <p className="py-12 text-center text-ink/50">Your cart is empty — go find something good.</p>
  }

  return (
    <div className="mx-auto max-w-lg">
      <h1 className="mb-1 text-2xl font-semibold">Your order</h1>
      <p className="mb-6 text-sm text-ink/60">from {cart.restaurantName}</p>

      <div className="card">
        <ul className="divide-y divide-line">
          {cart.items.map((item) => (
            <li key={item.cartItemId} className="flex items-center justify-between gap-3 py-3">
              <div className="flex-1">
                <p className="text-sm font-medium">{item.name}</p>
                <p className="price text-ink/50">₹{item.price} each</p>
              </div>
              <div className="flex items-center gap-2">
                <button className="btn-secondary h-8 w-8 !p-0" onClick={() => updateQty(item.menuItemId, item.quantity - 1)}>−</button>
                <span className="w-6 text-center text-sm">{item.quantity}</span>
                <button className="btn-secondary h-8 w-8 !p-0" onClick={() => updateQty(item.menuItemId, item.quantity + 1)}>+</button>
              </div>
              <span className="price w-16 text-right">₹{item.subtotal}</span>
            </li>
          ))}
        </ul>

        <div className="perforated-divider -mx-4 my-4" />

        <div className="flex justify-between text-base font-semibold">
          <span>Total</span>
          <span className="price">₹{cart.totalAmount}</span>
        </div>
      </div>

      <form onSubmit={placeOrder} className="card mt-4 space-y-3">
        <div>
          <label className="label" htmlFor="address">Delivery address</label>
          <textarea id="address" required rows={2} className="input" value={address}
            onChange={(e) => setAddress(e.target.value)} placeholder="Flat, street, landmark, city" />
        </div>
        {error && <p className="text-sm text-chili">{error}</p>}
        <button type="submit" className="btn-primary w-full" disabled={placing}>
          {placing ? 'Placing order…' : `Place order · ₹${cart.totalAmount}`}
        </button>
      </form>
    </div>
  )
}
