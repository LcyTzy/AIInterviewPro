<template>
  <el-container style="height:100vh">
    <el-aside width="220px" style="background:#304156;overflow:hidden;">
      <div class="logo">
        <el-icon><Monitor /></el-icon>
        <span>AI面试后台</span>
      </div>
      <el-menu
          :default-active="activeMenu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
          style="border-right: none;"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item index="/sessions">
          <el-icon><Document /></el-icon>
          <span>面试记录</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/resumes">
          <el-icon><Files /></el-icon>
          <span>简历管理</span>
        </el-menu-item>
        <el-menu-item index="/knowledge">
          <el-icon><Notebook /></el-icon>
          <span>知识库管理</span>
        </el-menu-item>
        <el-menu-item index="/job-positions">
          <el-icon><Briefcase /></el-icon>
          <span>岗位管理</span>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <span>个人设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-right">
          <span>{{ adminName }}</span>
          <el-button type="text" @click="handleLogout" style="margin-left:20px;">退出</el-button>
        </div>
      </el-header>
      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { logout } from '@/api/auth'
const router = useRouter()
const route = useRoute()
const activeMenu = computed(() => route.path)
const adminName = ref(localStorage.getItem('adminName') || '管理员')
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
    await logout()
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminName')
    router.push('/login')
    ElMessage.success('已退出')
  } catch (e) {
    // 取消退出
  }
}
</script>
<style scoped>
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2a3a;
}
.logo .el-icon {
  font-size: 24px;
  margin-right: 10px;
}
.header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
}
.header-right {
  display: flex;
  align-items: center;
}
/* 修复重点：上下左右padding全部清零 */
.main-content {
  padding: 0 !important;
  background: #f0f2f5;
  width: 100%;
  box-sizing: border-box;
}
</style>