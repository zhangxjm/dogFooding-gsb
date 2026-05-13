import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'rooms',
        name: 'Rooms',
        component: () => import('@/views/RoomList.vue'),
        meta: { title: '会议室管理', icon: 'OfficeBuilding', admin: true }
      },
      {
        path: 'reservation',
        name: 'Reservation',
        component: () => import('@/views/Reservation.vue'),
        meta: { title: '预约会议室', icon: 'Calendar' }
      },
      {
        path: 'my-reservations',
        name: 'MyReservations',
        component: () => import('@/views/MyReservations.vue'),
        meta: { title: '我的预约', icon: 'Document' }
      },
      {
        path: 'approvals',
        name: 'Approvals',
        component: () => import('@/views/Approvals.vue'),
        meta: { title: '预约审批', icon: 'Check', admin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (!to.meta.public && !userStore.isLoggedIn) {
    next('/login')
    return
  }
  
  if (to.meta.admin && !userStore.isAdmin) {
    next('/')
    return
  }
  
  next()
})

export default router
