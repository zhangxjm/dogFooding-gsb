<template>
  <div>
    <el-card shadow="never">
      <template #header>
        <span style="font-size: 16px; font-weight: 600">待审批预约</span>
      </template>

      <el-empty v-if="reservations.length === 0" description="暂无待审批预约" />
      <el-table v-else :data="reservations" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="会议标题" width="180" />
        <el-table-column prop="userName" label="预约人" width="100" />
        <el-table-column prop="roomName" label="会议室" width="140" />
        <el-table-column label="开始时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="success" text size="small" @click="handleApprove(row)">通过</el-button>
            <el-button type="danger" text size="small" @click="handleReject(row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPendingReservations, approveReservation, rejectReservation } from '../api/reservation'
import { ElMessage, ElMessageBox } from 'element-plus'

const reservations = ref([])

onMounted(() => {
  loadReservations()
})

async function loadReservations() {
  const res = await getPendingReservations()
  reservations.value = res.data
}

async function handleApprove(row) {
  await ElMessageBox.confirm(`确定通过「${row.title}」的预约申请？`, '审批确认', { type: 'success' })
  await approveReservation(row.id)
  ElMessage.success('已通过审批')
  loadReservations()
}

async function handleReject(row) {
  await ElMessageBox.confirm(`确定拒绝「${row.title}」的预约申请？`, '审批确认', { type: 'warning' })
  await rejectReservation(row.id)
  ElMessage.success('已拒绝审批')
  loadReservations()
}

function formatDateTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>
