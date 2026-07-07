const app = getApp()

Page({
  data: {
    phone: '',
    smsCode: '',
    smsDisabled: false,
    smsText: '获取验证码',
    smsCountdown: 0
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  onCodeInput(e) {
    this.setData({ smsCode: e.detail.value })
  },

  sendSmsCode() {
    const { phone } = this.data
    if (!phone || phone.length !== 11) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    this.setData({ smsDisabled: true, smsCountdown: 60 })
    this.countdown()
    wx.showToast({ title: '验证码已发送(测试:123456)', icon: 'none' })
  },

  countdown() {
    if (this.data.smsCountdown <= 0) {
      this.setData({ smsDisabled: false, smsText: '获取验证码' })
      return
    }
    this.setData({
      smsCountdown: this.data.smsCountdown - 1,
      smsText: this.data.smsCountdown + 's'
    })
    setTimeout(() => this.countdown(), 1000)
  },

  wxLogin() {
    wx.login({
      success: (res) => {
        if (res.code) {
          wx.showLoading({ title: '登录中...' })
          app.request({
            url: '/user/auth/login',
            method: 'POST',
            data: { code: res.code }
          }).then(data => {
            wx.hideLoading()
            // 从token中解析userId（简化处理，实际应后端返回userId）
            app.setToken(data.data.token, data.data.userId || 1)
            wx.reLaunch({ url: '/pages/index/index' })
          }).catch(() => {
            wx.hideLoading()
          })
        }
      }
    })
  },

  phoneLogin() {
    const { phone, smsCode } = this.data
    if (!phone || phone.length !== 11) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    if (!smsCode) {
      wx.showToast({ title: '请输入验证码', icon: 'none' })
      return
    }
    wx.showLoading({ title: '登录中...' })
    app.request({
      url: '/user/auth/loginByPhone',
      method: 'POST',
      data: { phone, code: smsCode }
    }).then(data => {
      wx.hideLoading()
      app.setToken(data.data.token, data.data.userId || 1)
      wx.reLaunch({ url: '/pages/index/index' })
    }).catch(() => {
      wx.hideLoading()
    })
  }
})
