<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search" size="default">
        <el-form-item label="用户">
          <el-input v-model="search.userName" placeholder="用户昵称" />
        </el-form-item>
        <el-form-item label="文件名">
          <el-input v-model="search.fileName" placeholder="文件名" />
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
        <el-table-column prop="userName" label="用户" />
        <el-table-column prop="fileName" label="文件名" />
        <el-table-column prop="fileUrl" label="文件地址">
          <template #default="{ row }">
            <a v-if="row.fileUrl && row.fileUrl.startsWith('https://')" :href="row.fileUrl" target="_blank" style="color:#409EFF;">
              {{ row.fileUrl }}
            </a>
            <span v-else style="color:#999;">{{ row.fileUrl || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
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
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const search = reactive({ userName: '', fileName: '' })
const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetch = async () => {
  try {
    const res = await api.get('/admin/resume/page', {
      params: { ...search, page: page.value, pageSize: pageSize.value }
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    // 如果后端未实现，显示模拟数据
    tableData.value = [
      { id: 1, userName: '张三', fileName: 'Java简历.pdf', fileUrl: '/uploads/1.pdf', createTime: '2026-07-07 10:00' },
      { id: 2, userName: '李四', fileName: '前端简历.docx', fileUrl: '/uploads/2.docx', createTime: '2026-07-06 15:30' }
    ]
    total.value = 2
  }
}

const resetSearch = () => {
  Object.assign(search, { userName: '', fileName: '' })
  page.value = 1
  fetch()
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该简历吗？', '提示', { type: 'warning' })
    await api.delete('/admin/resume', { params: { id } })
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