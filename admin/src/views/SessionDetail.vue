<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center;">
          <span>面试详情 - 会话 #{{ sessionId }}</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="用户">{{ detail.userName }}</el-descriptions-item>
        <el-descriptions-item label="岗位">{{ detail.jobPosition }}</el-descriptions-item>
        <el-descriptions-item label="难度">{{ detail.difficulty }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 'completed' ? 'success' : 'warning'">
            {{ detail.status === 'completed' ? '已完成' : '进行中' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总分">{{ detail.overallScore || '-' }}</el-descriptions-item>
        <el-descriptions-item label="问答数">{{ detail.messages?.length || 0 }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top:20px;">
        <h4>对话记录</h4>
        <div class="chat-box">
          <div v-for="(msg, idx) in detail.messages" :key="idx" class="chat-item" :class="msg.role">
            <div class="chat-role">{{ msg.role === 'user' ? '候选人' : '面试官' }}</div>
            <div class="chat-content">{{ msg.content }}</div>
            <div class="chat-time">{{ msg.createTime }}</div>
          </div>
        </div>
      </div>

      <div v-if="detail.overallScore" style="margin-top:20px;">
        <h4>评估报告</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="逻辑清晰度">{{ detail.scoreLogic }}</el-descriptions-item>
          <el-descriptions-item label="技术深度">{{ detail.scoreDepth }}</el-descriptions-item>
          <el-descriptions-item label="岗位匹配度">{{ detail.scoreMatch }}</el-descriptions-item>
          <el-descriptions-item label="表达能力">{{ detail.scoreCommunication }}</el-descriptions-item>
        </el-descriptions>
        <div style="margin-top:10px;">
          <h5>改进建议</h5>
          <p>{{ detail.improvementSuggestions }}</p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getSessionDetail } from '@/api/session'

const route = useRoute()
const sessionId = ref(route.params.id)
const detail = ref(null)

onMounted(async () => {
  const res = await getSessionDetail(sessionId.value)
  detail.value = res.data
})
</script>

<style scoped>
.chat-box {
  max-height: 500px;
  overflow-y: auto;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  padding: 15px;
}
.chat-item {
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 8px;
}
.chat-item.assistant {
  background: #f0f9ff;
  border-left: 3px solid #409EFF;
}
.chat-item.user {
  background: #f5f7fa;
  border-left: 3px solid #67C23A;
}
.chat-role {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 5px;
}
.chat-content {
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
}
.chat-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style>
