import axiosInstance from './axiosInstance'

export const adminApi = {
  dashboard: () => axiosInstance.get('/admin/dashboard'),
  listUsers: (params) => axiosInstance.get('/admin/users', { params }),
  deactivateUser: (id) => axiosInstance.patch(`/admin/users/${id}/deactivate`),
  listPendingRestaurants: (params) => axiosInstance.get('/admin/restaurants/pending', { params }),
  listOrders: (params) => axiosInstance.get('/admin/orders', { params }),
}
