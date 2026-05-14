import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/Register.vue')
    },
    {
        path: '/',
        component: () => import('../layout/MainLayout.vue'),
        redirect: '/rooms',
        children: [
            {
                path: 'rooms',
                name: 'Rooms',
                component: () => import('../views/RoomList.vue'),
                meta: { title: '会议室管理' }
            },
            {
                path: 'reserve',
                name: 'Reserve',
                component: () => import('../views/Reserve.vue'),
                meta: { title: '预约会议室' }
            },
            {
                path: 'my-reservations',
                name: 'MyReservations',
                component: () => import('../views/MyReservations.vue'),
                meta: { title: '我的预约' }
            },
            {
                path: 'approval',
                name: 'Approval',
                component: () => import('../views/Approval.vue'),
                meta: { title: '预约审批', requireAdmin: true }
            },
            {
                path: 'all-reservations',
                name: 'AllReservations',
                component: () => import('../views/AllReservations.vue'),
                meta: { title: '全部预约', requireAdmin: true }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach(async (to, from, next) => {
    if (to.path === '/login' || to.path === '/register') {
        next()
        return
    }
    next()
})

export default router
