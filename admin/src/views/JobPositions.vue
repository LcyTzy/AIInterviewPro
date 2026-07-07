<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search" size="default">
        <el-form-item label="岗位名称">
          <el-input v-model="search.name" placeholder="岗位名称" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="search.category" placeholder="分类" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="showCreate = true">新增岗位</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:20px;">
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="岗位名称" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="description" label="岗位描述" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'">
              {{ row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button size="small" @click="editRow(row)">编辑</el-button>
            <el-button size="small" :type="row.isActive ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.isActive ? '禁用' : '启用' }}
            </el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="showDialog" width="60%">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="岗位名称" prop="name">
          <el-input v-model="form.name" placeholder="如：Java开发工程师" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="如：技术研发" />
        </el-form-item>
        <el-form-item label="岗位描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="岗位描述信息" />
        </el-form-item>
        <el-form-item label="任职要求" prop="requirements">
          <el-input v-model="form.requirements" type="textarea" :rows="4" placeholder="任职要求，每行一条" />
        </el-form-item>
        <el-form-item label="面试题库" prop="interviewQuestions">
          <el-input v-model="form.interviewQuestions" type="textarea" :rows="4" placeholder="面试参考问题，每行一条" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getJobPositionPage, createJobPosition, updateJobPosition, deleteJobPosition, updateJobPositionStatus } from '@/api/jobPosition.js'

const search = reactive({ name: '', category: '' })
const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showDialog = ref(false)
const showCreate = ref(false)
const dialogTitle = ref('新增岗位')
const submitting = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  id: null,
  name: '',
  category: '',
  description: '',
  requirements: '',
  interviewQuestions: '',
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  category: [{ required: true, message: '请输入分类', trigger: 'blur' }]
}

const fetch = async () => {
  const res = await getJobPositionPage({
    ...search,
    page: page.value,
    pageSize: pageSize.value
  })
  tableData.value = res.data.content || res.data.records || []
  total.value = res.data.totalElements || res.data.total || 0
}

const resetSearch = () => {
  Object.assign(search, { name: '', category: '' })
  page.value = 1
  fetch()
}

const editRow = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑岗位'
  Object.assign(form, {
    id: row.id,
    name: row.name,
    category: row.category || '',
    description: row.description || '',
    requirements: row.requirements || '',
    interviewQuestions: row.interviewQuestions || '',
    sort: row.sort || 0
  })
  showDialog.value = true
}

const toggleStatus = async (row) => {
  const action = row.isActive ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}该岗位吗？`, '提示', { type: 'warning' })
    const newStatus = row.isActive ? 0 : 1
    await updateJobPositionStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetch()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该岗位吗？', '提示', { type: 'warning' })
    await deleteJobPosition(id)
    ElMessage.success('删除成功')
    fetch()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const submitForm = async () => {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateJobPosition(form)
      ElMessage.success('更新成功')
    } else {
      await createJobPosition(form)
      ElMessage.success('创建成功')
    }
    showDialog.value = false
    fetch()
  } catch (e) {
    // error handled in interceptor
  } finally {
    submitting.value = false
  }
}

watch(showCreate, (val) => {
  if (val) {
    isEdit.value = false
    dialogTitle.value = '新增岗位'
    Object.assign(form, { id: null, name: '', category: '', description: '', requirements: '', interviewQuestions: '', sort: 0 })
    showDialog.value = true
    showCreate.value = false
  }
})

onMounted(fetch)
</script>
