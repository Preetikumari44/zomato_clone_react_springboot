import axiosInstance from './axiosInstance'

export const menuApi = {
  listCategories: (restaurantId) => axiosInstance.get(`/restaurants/${restaurantId}/categories`),
  createCategory: (restaurantId, payload) => axiosInstance.post(`/restaurants/${restaurantId}/categories`, payload),
  search: (restaurantId, params) => axiosInstance.get(`/restaurants/${restaurantId}/menu`, { params }),
  createItem: (restaurantId, payload) => axiosInstance.post(`/restaurants/${restaurantId}/menu`, payload),
  updateItem: (restaurantId, itemId, payload) => axiosInstance.put(`/restaurants/${restaurantId}/menu/${itemId}`, payload),
  deleteItem: (restaurantId, itemId) => axiosInstance.delete(`/restaurants/${restaurantId}/menu/${itemId}`),
  uploadItemImage: (restaurantId, itemId, file) => {
    const form = new FormData()
    form.append('file', file)
    return axiosInstance.post(`/restaurants/${restaurantId}/menu/${itemId}/image`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}
