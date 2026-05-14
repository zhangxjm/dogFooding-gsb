import request from '../utils/request'

export function getAllRooms() {
    return request.get('/room/list')
}

export function getAvailableRooms() {
    return request.get('/room/available')
}

export function getRoomById(id) {
    return request.get(`/room/${id}`)
}

export function createRoom(data) {
    return request.post('/room', data)
}

export function updateRoom(id, data) {
    return request.put(`/room/${id}`, data)
}

export function deleteRoom(id) {
    return request.delete(`/room/${id}`)
}
