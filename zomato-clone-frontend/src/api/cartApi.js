import axiosInstance from './axiosInstance'

export const cartApi = {
  get: () => axiosInstance.get('/cart'),
  addItem: (payload) => axiosInstance.post('/cart/items', payload),
  updateItem: (menuItemId, quantity) => axiosInstance.put(`/cart/items/${menuItemId}`, { quantity }),
  removeItem: (menuItemId) => axiosInstance.delete(`/cart/items/${menuItemId}`),
  clear: () => axiosInstance.delete('/cart'),
}
