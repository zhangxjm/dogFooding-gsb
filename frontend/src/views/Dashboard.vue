<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #409EFF;">
            <el-icon size="32"><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.roomCount }}</div>
            <div class="stat-label">会议室总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #67C23A;">
            <el-icon size="32"><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todayReservationCount }}</div>
            <div class="stat-label">今日预约</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #E6A23C;">
            <el-icon size="32"><Timer /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingCount }}</div>
            <div class="stat-label">待审批</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #F56C6C;">
            <el-icon size="32"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.myReservationCount }}</div>
            <div class="stat-label">我的预约</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>会议室列表</span>
              <el-button type="primary" size="small" @click="$router.push('/reservation')">
                立即预约
              </el-button>
            </div>
          </template>
          <el-table :data="roomList" style="width: 100%">
            <el-table-column prop="name" label="会议室" min-width="120" />
            <el-table-column prop="capacity" label="容量" width="80" />
            <el-table-column prop="location" label="位置" min-width="150" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.status === 1" type="success">可用</el-tag>
                <el-tag v-else type="danger">不可用</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>我的最近预约</span>
              <el-button type="primary" size="small" @click="$router.push('/my-reservations')">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="recentReservations" style="width: 100%">
            <el-table-column prop="title" label="会议主题" min-width="150" show-overflow-tooltip />
            <el-table-column prop="roomName" label="会议室" width="120" />
            <el-table-column prop="startTime" label="开始时间" width="160" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRoomList } from '@/api/room'
import { getMyReservations } from '@/api/reservation'
import dayjs from 'dayjs'

const stats = ref({
  roomCount: 0,
  todayReservationCount: 0,
  pendingCount: 0,
  myReservationCount: 0
})

const roomList = ref([])
const recentReservations = ref([])

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'CANCELLED': 'info',
    'COMPLETED': ''
  }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待审批',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    'CANCELLED': '已取消',
    'COMPLETED': '已完成'
  }
  return map[status] || status
}

const fetchData = async () => {
  try {
    const [roomRes, reservationRes] = await Promise.all([
      getRoomList(),
      getMyReservations()
    ])
    
    roomList.value = roomRes.data.slice(0, 5)
    stats.value.roomCount = roomRes.data.length
    
    const allReservations = reservationRes.data
    stats.value.myReservationCount = allReservations.length
    stats.value.pendingCount = allReservations.filter(r => r.status === 'PENDING').length
    
    const today = dayjs().format('YYYY-MM-DD')
    stats.value.todayReservationCount = allReservations.filter(r => 
      dayjs(r.startTime).format('YYYY-MM-DD') === today
    ).length
    
    recentReservations.value = allReservations.slice(0, 5).map(r => ({
      ...r,
      startTime: dayjs(r.startTime).format('MM-DD HH:mm')
    }))
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
