import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    { path: '/login', component: () => import('@/views/Login.vue') },
    {
        path: '/',
        component: () => import('@/views/Layout.vue'),
        redirect: '/dashboard',
        children: [
            { path: 'dashboard', component: () => import('@/views/Dashboard.vue') },
            { path: 'sessions', component: () => import('@/views/Sessions.vue') },
            { path: 'session/:id', component: () => import('@/views/SessionDetail.vue'), props: true },
            { path: 'users', component: () => import('@/views/Users.vue') },
            { path: 'resumes', component: () => import('@/views/Resumes.vue') },
            { path: 'knowledge', component: () => import('@/views/Knowledge.vue') },
            { path: 'job-positions', component: () => import('@/views/JobPositions.vue') },
            { path: 'settings', component: () => import('@/views/Settings.vue') }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('adminToken')
    if (to.path !== '/login' && !token) {
        next('/login')
    } else {
        next()
    }
})

export default router