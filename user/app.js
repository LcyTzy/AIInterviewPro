// app.js
App({
  globalData: {
    token: '',
    userId: null,
    userInfo: null,
    baseUrl: 'http://localhost:8080'
  },

  onLaunch() {
    const token = wx.getStorageSync('token')
    const userId = wx.getStorageSync('userId')
    if (token) {
      this.globalData.token = token
      this.globalData.userId = userId
    }
  },

  setToken(token, userId) {
    this.globalData.token = token
    this.globalData.userId = userId
    wx.setStorageSync('token', token)
    wx.setStorageSync('userId', userId)
  },

  clearToken() {
    this.globalData.token = ''
    this.globalData.userId = null
    this.globalData.userInfo = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('userId')
    wx.removeStorageSync('userInfo')
  },

  request(options) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: this.globalData.baseUrl + options.url,
        method: options.method || 'GET',
        data: options.data || {},
        header: {
          'Authorization': 'Bearer ' + this.globalData.token,
          'Content-Type': 'application/json'
        },
        success(res) {
          if (res.data.code === 200) {
            resolve(res.data)
          } else if (res.data.code === 401) {
            this.clearToken()
            wx.redirectTo({ url: '/pages/login/login' })
            reject(new Error('登录已过期'))
          } else {
            wx.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
            reject(new Error(res.data.msg || '请求失败'))
          }
        },
        fail(err) {
          wx.showToast({ title: '网络错误', icon: 'none' })
          reject(err)
        }
      })
    })
  },

  uploadFile(filePath, extraData) {
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: this.globalData.baseUrl + '/user/resume/upload',
        filePath: filePath,
        name: 'file',
        formData: {
          userId: String(this.globalData.userId),
          ...extraData
        },
        header: {
          'Authorization': 'Bearer ' + this.globalData.token
        },
        success(res) {
          const data = JSON.parse(res.data)
          if (data.code === 200) {
            resolve(data)
          } else {
            wx.showToast({ title: data.msg || '上传失败', icon: 'none' })
            reject(new Error(data.msg))
          }
        },
        fail(err) {
          wx.showToast({ title: '网络错误', icon: 'none' })
          reject(err)
        }
      })
    })
  }
})
