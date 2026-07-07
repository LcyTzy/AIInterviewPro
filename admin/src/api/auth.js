import api from './index'

export function login(data) {
    return api.post('/admin/login', data)
}

export function getAdminInfo() {
    return api.get('/admin/info')
}

export function logout() {
    return api.post('/admin/logout')
}

export function changePassword(data) {
    return api.put('/admin/password', data)
}