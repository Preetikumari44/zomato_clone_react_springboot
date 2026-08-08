import axiosInstance from './axiosInstance'

export const restaurantApi = {
  search: (params) => axiosInstance.get('/restaurants', { params }),
  getById: (id) => axiosInstance.get(`/restaurants/${id}`),
  listMine: (params) => axiosInstance.get('/restaurants/owner/mine', { params }),
  create: (payload) => axiosInstance.post('/restaurants', payload),
  update: (id, payload) => axiosInstance.put(`/restaurants/${id}`, payload),
  uploadLogo: (id, file) => {
    const form = new FormData()
    form.append('file', file)
    return axiosInstance.post(`/restaurants/${id}/logo`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
  approve: (id) => axiosInstance.patch(`/restaurants/${id}/approve`),
  reject: (id, reason) => axiosInstance.patch(`/restaurants/${id}/reject`, { reason }),
}
