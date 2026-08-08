import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { menuApi } from '../../api/menuApi'
import { restaurantApi } from '../../api/restaurantApi'
import Loader from '../../components/Loader'

const EMPTY_ITEM = { name: '', description: '', price: '', categoryId: '', veg: true, available: true }

export default function MenuManager() {
  const { restaurantId } = useParams()
  const [restaurant, setRestaurant] = useState(null)
  const [categories, setCategories] = useState([])
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [newCategory, setNewCategory] = useState('')
  const [itemForm, setItemForm] = useState(EMPTY_ITEM)
  const [error, setError] = useState('')
  const [logoFile, setLogoFile] = useState(null)

  function refresh() {
    setLoading(true)
    return Promise.all([
      restaurantApi.listMine({ size: 50 }),
      menuApi.listCategories(restaurantId),
      menuApi.search(restaurantId, { size: 100 }),
    ]).then(([rRes, cRes, mRes]) => {
      setRestaurant(rRes.data.data.content.find((r) => String(r.id) === restaurantId))
      setCategories(cRes.data.data)
      setItems(mRes.data.data.content)
    }).finally(() => setLoading(false))
  }

  useEffect(() => { refresh() }, [restaurantId])

  async function addCategory(e) {
    e.preventDefault()
    if (!newCategory.trim()) return
    await menuApi.createCategory(restaurantId, { name: newCategory.trim() })
    setNewCategory('')
    refresh()
  }

  async function addItem(e) {
    e.preventDefault()
    setError('')
    try {
      await menuApi.createItem(restaurantId, {
        ...itemForm,
        price: Number(itemForm.price),
        categoryId: itemForm.categoryId || null,
      })
      setItemForm(EMPTY_ITEM)
      refresh()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not add item.')
    }
  }

  async function toggleAvailable(item) {
    await menuApi.updateItem(restaurantId, item.id, {
      name: item.name, description: item.description, price: item.price,
      categoryId: item.categoryId, veg: item.veg, available: !item.available,
    })
    refresh()
  }

  async function deleteItem(itemId) {
    if (!window.confirm('Remove this item from the menu?')) return
    await menuApi.deleteItem(restaurantId, itemId)
    refresh()
  }

  async function uploadLogo(e) {
    e.preventDefault()
    if (!logoFile) return
    await restaurantApi.uploadLogo(restaurantId, logoFile)
    setLogoFile(null)
    refresh()
  }

  if (loading) return <Loader />

  return (
    <div>
      <h1 className="mb-1 text-2xl font-semibold">{restaurant?.name}</h1>
      <p className="mb-6 text-sm text-ink/60">Manage categories, menu items, and your restaurant logo.</p>

      <form onSubmit={uploadLogo} className="card mb-6 flex items-center gap-3">
        {restaurant?.logoUrl && <img src={restaurant.logoUrl} alt="Logo" className="h-12 w-12 rounded-md object-cover" />}
        <input type="file" accept="image/*" onChange={(e) => setLogoFile(e.target.files[0])} className="text-sm" />
        <button type="submit" className="btn-secondary" disabled={!logoFile}>Upload logo</button>
      </form>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <div>
          <h2 className="mb-3 font-semibold">Categories</h2>
          <form onSubmit={addCategory} className="mb-4 flex gap-2">
            <input className="input" placeholder="e.g. Starters" value={newCategory} onChange={(e) => setNewCategory(e.target.value)} />
            <button type="submit" className="btn-secondary shrink-0">Add</button>
          </form>
          <ul className="flex flex-wrap gap-2">
            {categories.map((c) => (
              <li key={c.id} className="rounded-full bg-line px-3 py-1 text-sm">{c.name}</li>
            ))}
          </ul>
        </div>

        <div>
          <h2 className="mb-3 font-semibold">Add a menu item</h2>
          <form onSubmit={addItem} className="card space-y-3">
            <input required className="input" placeholder="Name" value={itemForm.name}
              onChange={(e) => setItemForm({ ...itemForm, name: e.target.value })} />
            <textarea className="input" rows={2} placeholder="Description" value={itemForm.description}
              onChange={(e) => setItemForm({ ...itemForm, description: e.target.value })} />
            <div className="flex gap-3">
              <input required type="number" step="0.01" min="0.01" className="input" placeholder="Price" value={itemForm.price}
                onChange={(e) => setItemForm({ ...itemForm, price: e.target.value })} />
              <select className="input" value={itemForm.categoryId} onChange={(e) => setItemForm({ ...itemForm, categoryId: e.target.value })}>
                <option value="">No category</option>
                {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
            </div>
            <label className="flex items-center gap-2 text-sm">
              <input type="checkbox" checked={itemForm.veg} onChange={(e) => setItemForm({ ...itemForm, veg: e.target.checked })} />
              Vegetarian
            </label>
            {error && <p className="text-sm text-chili">{error}</p>}
            <button type="submit" className="btn-primary w-full">Add item</button>
          </form>
        </div>
      </div>

      <h2 className="mb-3 mt-8 font-semibold">Menu items</h2>
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {items.map((item) => (
          <div key={item.id} className="card flex items-center justify-between">
            <div>
              <p className="font-medium">{item.name}</p>
              <p className="price text-ink/50">₹{item.price} · {item.categoryName || 'Uncategorized'}</p>
            </div>
            <div className="flex gap-2">
              <button className="btn-secondary" onClick={() => toggleAvailable(item)}>
                {item.available ? 'Mark unavailable' : 'Mark available'}
              </button>
              <button className="btn-danger" onClick={() => deleteItem(item.id)}>Delete</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
