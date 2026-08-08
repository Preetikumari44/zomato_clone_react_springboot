import axiosInstance from './axiosInstance'

export const deliveryApi = {
  listAvailable: (params) => axiosInstance.get('/delivery/available', { params }),
  listAssigned: (params) => axiosInstance.get('/delivery/assigned', { params }),
  listHistory: (params) => axiosInstance.get('/delivery/history', { params }),
  accept: (orderId) => axiosInstance.patch(`/delivery/${orderId}/accept`),
  markPickedUp: (orderId) => axiosInstance.patch(`/delivery/${orderId}/picked-up`),
  markDelivered: (orderId) => axiosInstance.patch(`/delivery/${orderId}/delivered`),
}
