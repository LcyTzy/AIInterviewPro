import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
    baseURL: '/api',
    timeout: 30000
})

// 请求拦截器
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('adminToken')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器
api.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code !== 200) {
            ElMessage.error(res.msg || '请求失败')
            return Promise.reject(new Error(res.msg || 'Error'))
        }
        return res
    },
    error => {
        if (error.response?.status === 401) {
            localStorage.removeItem('adminToken')
            window.location.href = '/login'
        }
        ElMessage.error(error.message || '网络错误')
        return Promise.reject(error)
    }
)

export default api