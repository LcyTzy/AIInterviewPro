import api from './index'

export function getUserPage(params) {
    return api.get('/admin/user/page', { params })
}

export function updateUserStatus(id, status) {
    return api.post(`/admin/user/status/${status}`, null, { params: { id } })
}