import axiosInstance from './axiosInstance'

export const authApi = {
  register: (payload) => axiosInstance.post('/auth/register', payload),
  login: (payload) => axiosInstance.post('/auth/login', payload),
  switchRole: (role) => axiosInstance.post('/auth/switch-role', { role }),
  getMe: () => axiosInstance.get('/users/me'),
  updateMe: (payload) => axiosInstance.put('/users/me', payload),
  uploadAvatar: (file) => {
    const form = new FormData()
    form.append('file', file)
    return axiosInstance.post('/users/me/avatar', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
}
