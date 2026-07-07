const app = getApp()

Page({
  data: {
    userInfo: null,
    greeting: '',
    recentSessions: []
  },

  onShow() {
    this.loadUserInfo()
    this.loadRecentSessions()
  },

  loadUserInfo() {
    const userId = app.globalData.userId
    if (!userId) {
      this.setData({ userInfo: null })
      return
    }
    app.request({
      url: '/user/center/info',
      data: { userId }
    }).then(data => {
      const hour = new Date().getHours()
      let greeting = '早上好'
      if (hour >= 12 && hour < 18) greeting = '下午好'
      else if (hour >= 18) greeting = '晚上好'
      this.setData({ userInfo: data.data, greeting })
    }).catch(() => {})
  },

  loadRecentSessions() {
    const userId = app.globalData.userId
    if (!userId) return
    app.request({
      url: '/user/interview/sessions',
      data: { userId, page: 1, pageSize: 5 }
    }).then(data => {
      this.setData({ recentSessions: data.data.records || [] })
    }).catch(() => {})
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  goResume() {
    if (!app.globalData.userId) return this.goLogin()
    wx.navigateTo({ url: '/pages/resume/resume' })
  },

  goInterview() {
    if (!app.globalData.userId) return this.goLogin()
    wx.navigateTo({ url: '/pages/interview/interview' })
  },

  goHistory() {
    if (!app.globalData.userId) return this.goLogin()
    wx.switchTab({ url: '/pages/history/history' })
  },

  goReport(e) {
    wx.navigateTo({ url: '/pages/report/report?sessionId=' + e.currentTarget.dataset.id })
  }
})
