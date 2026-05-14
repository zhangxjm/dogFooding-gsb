<template>
  <div>
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-size: 16px; font-weight: 600">我的预约</span>
          <el-radio-group v-model="statusFilter" size="small" @change="filterReservations">
            <el-radio-button label="ALL">全部</el-radio-button>
            <el-radio-button label="PENDING">待审批</el-radio-button>
            <el-radio-button label="APPROVED">已通过</el-radio-button>
            <el-radio-button label="REJECTED">已拒绝</el-radio-button>
            <el-radio-button label="CANCELLED">已取消</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-empty v-if="filteredReservations.length === 0" description="暂无预约记录" />
      <el-table v-else :data="filteredReservations" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="会议标题" width="180" />
        <el-table-column prop="roomName" label="会议室" width="140" />
        <el-table-column label="开始时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PENDING' || row.status === 'APPROVED'"
              type="danger"
              text
              size="small"
              @click="handleCancel(row)"
            >取消预约</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyReservations, cancelReservation } from '../api/reservation'
import { ElMessage, ElMessageBox } from 'element-plus'

const reservations = ref([])
const statusFilter = ref('ALL')

const filteredReservations = computed(() => {
  if (statusFilter.value === 'ALL') return reservations.value
  return reservations.value.filter(r => r.status === statusFilter.value)
})

onMounted(() => {
  loadReservations()
})

async function loadReservations() {
  const res = await getMyReservations()
  reservations.value = res.data
}

function filterReservations() {
  // computed handles it
}

async function handleCancel(row) {
  const { value: reason } = await ElMessageBox.prompt('请输入取消原因', '取消预约', {
    confirmButtonText: '确定取消',
    cancelButtonText: '返回',
    inputPlaceholder: '请输入取消原因（选填）',
    type: 'warning'
  }).catch(() => ({ value: null }))

  if (reason === null) return

  await cancelReservation(row.id, reason || '用户主动取消')
  ElMessage.success('预约已取消')
  loadReservations()
}

function formatDateTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function statusType(status) {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', CANCELLED: 'info' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { PENDING: '待审批', APPROVED: '已通过', REJECTED: '已拒绝', CANCELLED: '已取消' }
  return map[status] || status
}
</script>
