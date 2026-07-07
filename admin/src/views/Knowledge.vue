<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="search" size="default">
        <el-form-item label="标题">
          <el-input v-model="search.title" placeholder="标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="search.category" placeholder="全部" clearable>
            <el-option label="Java" value="java" />
            <el-option label="计算机基础" value="computer_basics" />
            <el-option label="项目经验" value="project" />
            <el-option label="数据库" value="database" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="showCreate = true">新增文档</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:20px;">
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="category" label="分类">
          <template #default="{ row }">
            <el-tag size="small">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tags" label="标签">
          <template #default="{ row }">
            <el-tag v-for="tag in row.tags" :key="tag" size="small" style="margin:2px;">{{ tag }}</el-tag>
          </template>
        </el-table-column>
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
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category">
            <el-option label="Java" value="java" />
            <el-option label="计算机基础" value="computer_basics" />
            <el-option label="项目经验" value="project" />
            <el-option label="数据库" value="database" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-select v-model="form.tags" multiple filterable allow-create placeholder="输入标签">
            <el-option v-for="tag in form.tags" :key="tag" :value="tag" />
          </el-select>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getKnowledgePage, createKnowledge, updateKnowledge, deleteKnowledge, updateKnowledgeStatus } from '@/api/knowledge.js'

const search = reactive({ title: '', category: '' })
const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showDialog = ref(false)
const showCreate = ref(false)
const dialogTitle = ref('新增知识文档')
const submitting = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  id: null,
  title: '',
  content: '',
  category: '',
  tags: []
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const fetch = async () => {
  const res = await getKnowledgePage({
    ...search,
    page: page.value,
    pageSize: pageSize.value
  })
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const resetSearch = () => {
  Object.assign(search, { title: '', category: '' })
  page.value = 1
  fetch()
}

const editRow = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑知识文档'
  Object.assign(form, {
    id: row.id,
    title: row.title,
    content: row.content,
    category: row.category,
    tags: row.tags || []
  })
  showDialog.value = true
}

const toggleStatus = async (row) => {
  const action = row.isActive ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}该文档吗？`, '提示', { type: 'warning' })
    const newStatus = row.isActive ? 0 : 1
    await updateKnowledgeStatus(row.id, newStatus)
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
    await ElMessageBox.confirm('确定要删除该文档吗？', '提示', { type: 'warning' })
    await deleteKnowledge(id)
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
      await updateKnowledge(form)
      ElMessage.success('更新成功')
    } else {
      await createKnowledge(form)
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

// 监听 showCreate 变化
import { watch } from 'vue'
watch(showCreate, (val) => {
  if (val) {
    isEdit.value = false
    dialogTitle.value = '新增知识文档'
    Object.assign(form, { id: null, title: '', content: '', category: '', tags: [] })
    showDialog.value = true
    showCreate.value = false
  }
})

onMounted(fetch)
</script>