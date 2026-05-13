<template>
  <div class="approvals">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预约审批</span>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="待审批" name="pending">
          <el-table :data="pendingList" v-loading="loading" style="width: 100%">
            <el-table-column prop="title" label="会议主题" min-width="150" show-overflow-tooltip />
            <el-table-column prop="roomName" label="会议室" width="120" />
            <el-table-column prop="userName" label="预约人" width="100" />
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
            <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="success" link @click="handleApprove(row, 'APPROVED')">通过</el-button>
                <el-button type="danger" link @click="handleApprove(row, 'REJECTED')">拒绝</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="全部预约" name="all">
          <el-table :data="allList" v-loading="loading" style="width: 100%">
            <el-table-column prop="title" label="会议主题" min-width="150" show-overflow-tooltip />
            <el-table-column prop="roomName" label="会议室" width="120" />
            <el-table-column prop="userName" label="预约人" width="100" />
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
            <el-table-column prop="approverName" label="审批人" width="100" />
            <el-table-column prop="approvalRemark" label="审批备注" min-width="150" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dialogVisible" title="审批" width="500px">
      <el-form :model="approvalForm" label-width="80px">
        <el-form-item label="会议主题">
          <el-input :model-value="currentRow?.title" disabled />
        </el-form-item>
        <el-form-item label="审批结果">
          <el-radio-group v-model="approvalForm.status">
            <el-radio label="APPROVED">通过</el-radio>
            <el-radio label="REJECTED">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="approvalForm.remark" type="textarea" rows="3" placeholder="请输入审批备注（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApproval" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllReservations, approveReservation } from '@/api/reservation'
import dayjs from 'dayjs'

const loading = ref(false)
const activeTab = ref('pending')
const allList = ref([])
const dialogVisible = ref(false)
const submitLoading = ref(false)
const currentRow = ref(null)

const approvalForm = ref({
  reservationId: null,
  status: 'APPROVED',
  remark: ''
})

const pendingList = computed(() => {
  return allList.value.filter(item => item.status === 'PENDING')
})

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

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAllReservations()
    allList.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleApprove = (row, status) => {
  currentRow.value = row
  approvalForm.value = {
    reservationId: row.id,
    status: status,
    remark: ''
  }
  dialogVisible.value = true
}

const submitApproval = async () => {
  submitLoading.value = true
  try {
    await approveReservation(approvalForm.value)
    ElMessage.success('审批成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
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
