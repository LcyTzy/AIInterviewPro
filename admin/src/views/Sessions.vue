<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search" size="default">
        <el-form-item label="用户">
          <el-input v-model="search.userName" placeholder="昵称" />
        </el-form-item>
        <el-form-item label="岗位">
          <el-input v-model="search.jobPosition" placeholder="岗位" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="search.status" placeholder="全部" clearable>
            <el-option label="进行中" value="ongoing" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:20px;">
      <el-table :data="tableData" border style="width:100%;">
        <el-table-column prop="id" label="会话ID" width="90" />
        <el-table-column prop="userName" label="用户" />
        <el-table-column prop="jobPosition" label="岗位" />
        <el-table-column prop="difficulty" label="难度" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'warning'">
              {{ row.status === 'completed' ? '已完成' : '进行中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="questionCount" label="问答数" width="80" />
        <el-table-column prop="overallScore" label="总分" width="80" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="viewDetail(row.id)">详情</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSessionPage, deleteSession } from '@/api/session'

const router = useRouter()
const search = reactive({ userName: '', jobPosition: '', status: '' })
const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetch = async () => {
  const res = await getSessionPage({
    ...search,
    page: page.value,
    pageSize: pageSize.value
  })
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const resetSearch = () => {
  Object.assign(search, { userName: '', jobPosition: '', status: '' })
  page.value = 1
  fetch()
}

const viewDetail = (id) => {
  router.push(`/session/${id}`)
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条面试记录吗？', '提示', { type: 'warning' })
    await deleteSession(id)
    ElMessage.success('删除成功')
    fetch()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(fetch)
</script>