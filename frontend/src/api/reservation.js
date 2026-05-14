import request from '../utils/request'

export function createReservation(data) {
    return request.post('/reservation', data)
}

export function getMyReservations() {
    return request.get('/reservation/my')
}

export function getPendingReservations() {
    return request.get('/reservation/pending')
}

export function getAllReservations() {
    return request.get('/reservation/all')
}

export function getRoomReservations(roomId) {
    return request.get(`/reservation/room/${roomId}`)
}

export function getRoomReservationsByDate(roomId, date) {
    return request.get(`/reservation/room/${roomId}/date`, { params: { date } })
}

export function approveReservation(id) {
    return request.put(`/reservation/${id}/approve`)
}

export function rejectReservation(id) {
    return request.put(`/reservation/${id}/reject`)
}

export function cancelReservation(id, cancelReason) {
    return request.put(`/reservation/${id}/cancel`, { cancelReason })
}
