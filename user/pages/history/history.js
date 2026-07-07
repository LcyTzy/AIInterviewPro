const app = getApp()

Page({
  data: {
    sessions: [],
    statusFilter: '',
    page: 1,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadSessions()
  },

  onShow() {
    this.setData({ page: 1, sessions: [], hasMore: true })
    this.loadSessions()
  },

  filterStatus(e) {
    const status = e.currentTarget.dataset.status
    this.setData({
      statusFilter: status,
      page: 1,
      sessions: [],
      hasMore: true
    })
    this.loadSessions()
  },

  async loadSessions() {
    if (this.data.loading || !this.data.hasMore) return
    this.setData({ loading: true })

    const userId = app.globalData.userId
    if (!userId) {
      this.setData({ loading: false })
      return
    }

    try {
      const res = await app.request({
        url: '/user/interview/sessions',
        data: {
          userId,
          status: this.data.statusFilter,
          page: this.data.page,
          pageSize: 10
        }
      })

      const records = (res.data.records || []).map(s => ({
        ...s,
        difficultyText: s.difficulty === 'easy' ? '简单' : s.difficulty === 'hard' ? '困难' : '中等',
        startTimeText: s.startTime ? s.startTime.substring(0, 16).replace('T', ' ') : ''
      }))

      this.setData({
        sessions: [...this.data.sessions, ...records],
        hasMore: records.length >= 10,
        page: this.data.page + 1,
        loading: false
      })
    } catch (err) {
      this.setData({ loading: false })
    }
  },

  loadMore() {
    this.loadSessions()
  },

  goReport(e) {
    wx.navigateTo({ url: '/pages/report/report?sessionId=' + e.currentTarget.dataset.id })
  }
})
