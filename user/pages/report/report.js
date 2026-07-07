const app = getApp()

Page({
  data: {
    loading: true,
    report: null,
    scoreLevel: '',
    scoreLevelText: '',
    dimensions: [],
    feedbackItems: []
  },

  onLoad(options) {
    const sessionId = options.sessionId
    if (sessionId) {
      this.loadReport(sessionId)
    }
  },

  async loadReport(sessionId) {
    this.setData({ loading: true })

    try {
      const res = await app.request({
        url: '/user/report/' + sessionId
      })

      const report = res.data
      if (report) {
        const score = report.overallScore || 0
        let level, levelText
        if (score >= 90) { level = 'excellent'; levelText = '优秀' }
        else if (score >= 75) { level = 'good'; levelText = '良好' }
        else if (score >= 60) { level = 'average'; levelText = '一般' }
        else { level = 'poor'; levelText = '待提升' }

        const dimensions = [
          { name: '逻辑清晰度', score: report.scoreLogic || 0 },
          { name: '技术深度', score: report.scoreDepth || 0 },
          { name: '岗位匹配度', score: report.scoreMatch || 0 },
          { name: '表达能力', score: report.scoreCommunication || 0 }
        ]

        const feedbackItems = []
        if (report.detailedFeedback) {
          Object.keys(report.detailedFeedback).forEach(key => {
            feedbackItems.push({
              label: key,
              content: report.detailedFeedback[key]
            })
          })
        }

        this.setData({
          report,
          scoreLevel: level,
          scoreLevelText: levelText,
          dimensions,
          feedbackItems,
          loading: false
        })
      } else {
        this.setData({ loading: false })
      }
    } catch (err) {
      this.setData({ loading: false })
      wx.showToast({ title: '获取报告失败', icon: 'none' })
    }
  }
})
