<template>
  <div class="my-reservations">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的预约记录</span>
          <el-button type="primary" @click="$router.push('/reservation')">新增预约</el-button>
        </div>
      </template>

      <el-table :data="reservationList" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="会议主题" min-width="150" show-overflow-tooltip />
        <el-table-column prop="roomName" label="会议室" width="120" />
        <el-table-column prop="attendees" label="参会人数" width="100" />
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="canCancel(row)" 
              type="danger" 
              link 
              @click="handleCancel(row)"
            >
              取消
            </el-button>
            <span v-else-if="row.status === 'CANCELLED'" style="color: #999;">已取消</span>
            <span v-else-if="row.status === 'COMPLETED'" style="color: #999;">已完成</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyReservations, cancelReservation } from '@/api/reservation'
import dayjs from 'dayjs'

const loading = ref(false)
const reservationList = ref([])

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

const formatDateTime = (datetime) => {
  return dayjs(datetime).format('MM-DD HH:mm')
}

const canCancel = (row) => {
  return ['PENDING', 'APPROVED'].includes(row.status)
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getMyReservations()
    reservationList.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelReservation(row.id)
    ElMessage.success('取消成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
