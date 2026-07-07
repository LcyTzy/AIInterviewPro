const app = getApp()

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    stats: {
      totalSessions: 0,
      completedSessions: 0,
      avgScore: 0
    }
  },

  onShow() {
    this.loadProfile()
  },

  async loadProfile() {
    const userId = app.globalData.userId
    if (!userId) {
      this.setData({ isLoggedIn: false, userInfo: null })
      return
    }

    this.setData({ isLoggedIn: true })

    try {
      const res = await app.request({
        url: '/user/center/info',
        data: { userId }
      })
      this.setData({ userInfo: res.data })
    } catch (err) {}

    // 加载统计
    try {
      const res = await app.request({
        url: '/user/interview/sessions',
        data: { userId, page: 1, pageSize: 100 }
      })
      const records = res.data.records || []
      const completed = records.filter(r => r.status === 'completed')
      const scores = completed.map(r => r.overallScore).filter(s => s > 0)
      const avg = scores.length > 0 ? (scores.reduce((a, b) => a + b, 0) / scores.length).toFixed(1) : 0

      this.setData({
        stats: {
          totalSessions: records.length,
          completedSessions: completed.length,
          avgScore: avg
        }
      })
    } catch (err) {}
  },

  chooseAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      success: (res) => {
        const tempPath = res.tempFiles[0].tempFilePath
        wx.showLoading({ title: '上传中...' })
        wx.uploadFile({
          url: app.globalData.baseUrl + '/user/center/avatar',
          filePath: tempPath,
          name: 'file',
          formData: { userId: String(app.globalData.userId) },
          header: { 'Authorization': 'Bearer ' + app.globalData.token },
          success: (uploadRes) => {
            wx.hideLoading()
            const data = JSON.parse(uploadRes.data)
            if (data.code === 200) {
              this.setData({ 'userInfo.avatarUrl': data.data })
              wx.showToast({ title: '头像更新成功', icon: 'success' })
            }
          },
          fail: () => {
            wx.hideLoading()
            wx.showToast({ title: '上传失败', icon: 'none' })
          }
        })
      }
    })
  },

  editNickname() {
    wx.showModal({
      title: '修改昵称',
      editable: true,
      placeholderText: '请输入新昵称',
      success: (res) => {
        if (res.confirm && res.content) {
          app.request({
            url: '/user/center/info',
            method: 'PUT',
            data: {
              userId: app.globalData.userId,
              nickname: res.content
            }
          }).then(() => {
            this.setData({ 'userInfo.nickname': res.content })
            wx.showToast({ title: '修改成功', icon: 'success' })
          })
        }
      }
    })
  },

  goResume() {
    wx.navigateTo({ url: '/pages/resume/resume' })
  },

  goHistory() {
    wx.switchTab({ url: '/pages/history/history' })
  },

  goSettings() {
    wx.showToast({ title: '设置功能开发中', icon: 'none' })
  },

  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.clearToken()
          this.setData({ isLoggedIn: false, userInfo: null })
          wx.showToast({ title: '已退出', icon: 'success' })
        }
      }
    })
  }
})
