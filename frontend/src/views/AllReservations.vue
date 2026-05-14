<template>
  <div>
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-size: 16px; font-weight: 600">全部预约记录</span>
          <el-radio-group v-model="statusFilter" size="small">
            <el-radio-button label="ALL">全部</el-radio-button>
            <el-radio-button label="PENDING">待审批</el-radio-button>
            <el-radio-button label="APPROVED">已通过</el-radio-button>
            <el-radio-button label="REJECTED">已拒绝</el-radio-button>
            <el-radio-button label="CANCELLED">已取消</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="filteredReservations" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="会议标题" width="160" />
        <el-table-column prop="userName" label="预约人" width="90" />
        <el-table-column prop="roomName" label="会议室" width="130" />
        <el-table-column label="开始时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approveUserName" label="审批人" width="90" />
        <el-table-column prop="cancelReason" label="取消原因" min-width="140" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAllReservations } from '../api/reservation'

const reservations = ref([])
const statusFilter = ref('ALL')

const filteredReservations = computed(() => {
  if (statusFilter.value === 'ALL') return reservations.value
  return reservations.value.filter(r => r.status === statusFilter.value)
})

onMounted(async () => {
  const res = await getAllReservations()
  reservations.value = res.data
})

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
