import api from './index'

export function getKnowledgePage(params) {
  return api.get('/admin/knowledge/page', { params })
}

export function createKnowledge(data) {
  return api.post('/admin/knowledge', data)
}

export function updateKnowledge(data) {
  return api.put('/admin/knowledge', data)
}

export function deleteKnowledge(id) {
  return api.delete('/admin/knowledge', { params: { id } })
}

export function updateKnowledgeStatus(id, status) {
  return api.post(`/admin/knowledge/status/${status}`, null, { params: { id } })
}