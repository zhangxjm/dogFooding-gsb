import request from '@/utils/request'

export const createReservation = (data) => request.post('/reservations', data)
export const getMyReservations = () => request.get('/reservations/my')
export const getAllReservations = () => request.get('/reservations')
export const getReservationById = (id) => request.get(`/reservations/${id}`)
export const cancelReservation = (id) => request.put(`/reservations/${id}/cancel`)
export const approveReservation = (data) => request.post('/reservations/approve', data)
export const getRoomTimeSlots = (roomId, date) => request.get(`/reservations/room/${roomId}/timeslots`, { params: { date } })
export const checkConflict = (roomId, startTime, endTime) => request.get('/reservations/check-conflict', { params: { roomId, startTime, endTime } })
