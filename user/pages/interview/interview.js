const app = getApp()

Page({
  data: {
    step: 'prepare',
    jobPosition: '',
    jobIndex: -1,
    jobOptions: ['Java开发工程师', '前端开发工程师', '后端开发工程师', '全栈工程师', '产品经理', '测试工程师', 'UI设计师', '运维工程师'],
    difficulty: 'medium',
    resumeText: '',
    resumeId: null,
    messages: [],
    answer: '',
    isTyping: false,
    sessionId: null
  },

  onJobChange(e) {
    const index = e.detail.value
    this.setData({
      jobIndex: index,
      jobPosition: this.data.jobOptions[index]
    })
  },

  selectDifficulty(e) {
    this.setData({ difficulty: e.currentTarget.dataset.val })
  },

  chooseResume() {
    wx.chooseMessageFile({
      count: 1,
      type: 'file',
      extension: ['pdf', 'docx', 'doc'],
      success: (res) => {
        const file = res.tempFiles[0]
        wx.showLoading({ title: '上传中...' })
        app.uploadFile(file.path).then(data => {
          wx.hideLoading()
          this.setData({
            resumeText: data.data.parsedTextPreview || '简历已上传',
            resumeId: data.data.resumeId
          })
          wx.showToast({ title: '简历上传成功', icon: 'success' })
        }).catch(() => {
          wx.hideLoading()
        })
      }
    })
  },

  async startInterview() {
    const { jobPosition, difficulty, resumeText } = this.data
    if (!jobPosition) {
      wx.showToast({ title: '请输入应聘岗位', icon: 'none' })
      return
    }

    wx.showLoading({ title: '准备面试...' })

    try {
      // 创建会话
      const createRes = await app.request({
        url: '/user/interview/create',
        method: 'POST',
        data: {
          userId: app.globalData.userId,
          jobPosition,
          difficulty
        }
      })

      const sessionId = createRes.data.sessionId
      this.setData({ sessionId })

      // 获取第一个问题
      const startRes = await app.request({
        url: '/user/interview/start',
        method: 'POST',
        data: {
          sessionId,
          resumeText: resumeText || '',
          jobPosition
        }
      })

      wx.hideLoading()

      const firstQuestion = startRes.data.firstQuestion
      this.setData({
        step: 'interviewing',
        messages: [{ role: 'assistant', content: firstQuestion }]
      })
    } catch (err) {
      wx.hideLoading()
      wx.showToast({ title: '启动面试失败', icon: 'none' })
    }
  },

  onAnswerInput(e) {
    this.setData({ answer: e.detail.value })
  },

  async sendAnswer() {
    const { answer, sessionId, messages, isTyping } = this.data
    if (!answer || isTyping) return

    this.setData({
      messages: [...messages, { role: 'user', content: answer }],
      answer: '',
      isTyping: true
    })

    try {
      // 使用 SSE 流式获取回复
      const res = await new Promise((resolve, reject) => {
        const requestTask = wx.request({
          url: app.globalData.baseUrl + '/user/interview/stream',
          data: { sessionId, answer },
          header: { 'Authorization': 'Bearer ' + app.globalData.token },
          success: (res) => resolve(res),
          fail: (err) => reject(err)
        })
      })

      // 解析 SSE 数据
      const data = res.data
      let fullContent = ''
      if (typeof data === 'string') {
        const lines = data.split('\n')
        for (const line of lines) {
          if (line.startsWith('data: ') && !line.includes('[DONE]')) {
            fullContent += line.substring(6)
          }
        }
      }

      if (fullContent) {
        this.setData({
          messages: [...this.data.messages, { role: 'assistant', content: fullContent }],
          isTyping: false
        })
      } else {
        // 降级：使用普通请求
        const fallbackRes = await app.request({
          url: '/user/interview/next',
          method: 'POST',
          data: { sessionId, answer }
        })
        this.setData({
          messages: [...this.data.messages, { role: 'assistant', content: fallbackRes.data.question }],
          isTyping: false
        })
      }
    } catch (err) {
      this.setData({ isTyping: false })
      wx.showToast({ title: '获取回复失败', icon: 'none' })
    }
  },

  endInterview() {
    wx.showModal({
      title: '结束面试',
      content: '确定要结束本次面试吗？',
      success: (res) => {
        if (res.confirm) {
          const { sessionId } = this.data
          app.request({
            url: '/user/interview/end',
            method: 'POST',
            data: { sessionId }
          }).then(() => {
            wx.showToast({ title: '面试已结束', icon: 'success' })
            setTimeout(() => {
              wx.navigateTo({ url: '/pages/report/report?sessionId=' + sessionId })
            }, 1500)
          })
        }
      }
    })
  }
})
