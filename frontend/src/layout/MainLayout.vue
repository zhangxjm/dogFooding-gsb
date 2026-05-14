<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background-color: #304156">
      <div style="height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: bold; border-bottom: 1px solid #3a4a5e">
        <el-icon style="margin-right: 8px"><OfficeBuilding /></el-icon>
        会议室预约
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/rooms">
          <el-icon><House /></el-icon>
          <span>会议室管理</span>
        </el-menu-item>
        <el-menu-item index="/reserve">
          <el-icon><Calendar /></el-icon>
          <span>预约会议室</span>
        </el-menu-item>
        <el-menu-item index="/my-reservations">
          <el-icon><Tickets /></el-icon>
          <span>我的预约</span>
        </el-menu-item>
        <el-menu-item v-if="userStore.isAdmin()" index="/approval">
          <el-icon><Stamp /></el-icon>
          <span>预约审批</span>
        </el-menu-item>
        <el-menu-item v-if="userStore.isAdmin()" index="/all-reservations">
          <el-icon><Document /></el-icon>
          <span>全部预约</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="background: #fff; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 1px 4px rgba(0,21,41,.08); padding: 0 20px">
        <span style="font-size: 16px; font-weight: 600; color: #303133">{{ currentTitle }}</span>
        <div style="display: flex; align-items: center; gap: 16px">
          <el-tag :type="userStore.isAdmin() ? 'danger' : 'info'" size="small">
            {{ userStore.isAdmin() ? '管理员' : '普通用户' }}
          </el-tag>
          <span style="color: #606266">{{ userStore.userInfo?.realName }}</span>
          <el-button type="danger" text @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>

      <el-main style="background: #f0f2f5; padding: 20px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { logout } from '../api/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '会议室预约系统')

onMounted(async () => {
  await userStore.fetchUserInfo()
})

async function handleLogout() {
  try {
    await logout()
  } catch (e) {
    // ignore
  }
  userStore.clearUser()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>
