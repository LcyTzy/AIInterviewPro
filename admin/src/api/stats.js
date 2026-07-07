import api from './index'

export function getHotPositions(days = 30) {
    return api.get('/admin/stats/hot-positions', { params: { days } })
}

export function getDailyInterviews(begin, end) {
    return api.get('/admin/stats/daily-interviews', { params: { begin, end } })
}

export function getScoreDistribution(timeRange = 'all') {
    return api.get('/admin/stats/score-distribution', { params: { timeRange } })
}