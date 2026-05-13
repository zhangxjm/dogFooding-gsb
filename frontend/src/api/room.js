import request from '@/utils/request'

export const getRoomList = () => request.get('/rooms')
export const getRoomById = (id) => request.get(`/rooms/${id}`)
export const addRoom = (data) => request.post('/rooms', data)
export const updateRoom = (id, data) => request.put(`/rooms/${id}`, data)
export const deleteRoom = (id) => request.delete(`/rooms/${id}`)
