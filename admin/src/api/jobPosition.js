import api from './index'

export function getJobPositionPage(params) {
    return api.get('/admin/job-position/page', { params })
}

export function getJobPositionList() {
    return api.get('/admin/job-position/list')
}

export function createJobPosition(data) {
    return api.post('/admin/job-position', data)
}

export function updateJobPosition(data) {
    return api.put('/admin/job-position', data)
}

export function deleteJobPosition(id) {
    return api.delete(`/admin/job-position/${id}`)
}

export function updateJobPositionStatus(id, status) {
    return api.put(`/admin/job-position/${id}/status`, { status })
}
