<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search" size="default">
        <el-form-item label="昵称">
          <el-input v-model="search.nickname" placeholder="昵称" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="search.phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="search.isActive" placeholder="全部" clearable>
            <el-option label="启用" value="true" />
            <el-option label="禁用" value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:20px;">
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="avatarUrl" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatarUrl" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" />
        <el-table-column prop="createTime" label="注册时间" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" :type="row.isActive ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.isActive ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          @current-change="fetch"
          @size-change="fetch"
          layout="total, sizes, prev, pager, next"
          style="margin-top:20px;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, updateUserStatus } from '@/api/user'

const search = reactive({ nickname: '', phone: '', isActive: '' })
const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetch = async () => {
  const res = await getUserPage({
    ...search,
    page: page.value,
    pageSize: pageSize.value
  })
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const resetSearch = () => {
  Object.assign(search, { nickname: '', phone: '', isActive: '' })
  page.value = 1
  fetch()
}

const toggleStatus = async (row) => {
  const action = row.isActive ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', { type: 'warning' })
    const newStatus = row.isActive ? 0 : 1
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetch()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

onMounted(fetch)
</script>