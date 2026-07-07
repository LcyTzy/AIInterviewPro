const app = getApp()

Page({
  data: {
    resume: null
  },

  onShow() {
    this.loadResume()
  },

  loadResume() {
    const userId = app.globalData.userId
    if (!userId) return
    app.request({
      url: '/user/resume/latest',
      data: { userId }
    }).then(data => {
      const r = data.data
      if (r) {
        r.fileSizeText = this.formatSize(r.fileSize)
        r.parsedTextPreview = r.parsedText ? r.parsedText.substring(0, 200) + '...' : ''
        r.createTime = r.createTime ? r.createTime.substring(0, 19).replace('T', ' ') : ''
      }
      this.setData({ resume: r })
    }).catch(() => {})
  },

  formatSize(bytes) {
    if (!bytes) return '未知'
    if (bytes < 1024) return bytes + 'B'
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + 'KB'
    return (bytes / (1024 * 1024)).toFixed(1) + 'MB'
  },

  chooseFile() {
    wx.chooseMessageFile({
      count: 1,
      type: 'file',
      extension: ['pdf', 'docx', 'doc'],
      success: (res) => {
        const file = res.tempFiles[0]
        wx.showLoading({ title: '上传中...' })
        app.uploadFile(file.path).then(data => {
          wx.hideLoading()
          wx.showToast({ title: '上传成功', icon: 'success' })
          this.loadResume()
        }).catch(() => {
          wx.hideLoading()
        })
      }
    })
  }
})
