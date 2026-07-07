import api from './index'

export function getSessionPage(params) {
    return api.get('/admin/session/page', { params })
}

export function getSessionDetail(id) {
    return api.get(`/admin/session/${id}`)
}

export function deleteSession(id) {
    return api.delete('/admin/session', { params: { id } })
}