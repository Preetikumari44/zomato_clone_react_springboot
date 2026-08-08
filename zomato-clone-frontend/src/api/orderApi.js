import axiosInstance from './axiosInstance'

export const orderApi = {
  place: (payload) => axiosInstance.post('/orders', payload),
  listMine: (params) => axiosInstance.get('/orders/mine', { params }),
  getById: (id) => axiosInstance.get(`/orders/${id}`),
  listForRestaurant: (restaurantId, params) => axiosInstance.get(`/orders/restaurant/${restaurantId}`, { params }),
  accept: (id) => axiosInstance.patch(`/orders/${id}/accept`),
  reject: (id) => axiosInstance.patch(`/orders/${id}/reject`),
  updateStatus: (id, status) => axiosInstance.patch(`/orders/${id}/status`, { status }),
}
