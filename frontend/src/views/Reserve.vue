<template>
  <div>
    <el-card shadow="never">
      <template #header>
        <span style="font-size: 16px; font-weight: 600">预约会议室</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 600px">
        <el-form-item label="会议室" prop="roomId">
          <el-select v-model="form.roomId" placeholder="请选择会议室" style="width: 100%" @change="onRoomChange">
            <el-option v-for="room in availableRooms" :key="room.id" :label="`${room.name} (${room.location}, 容纳${room.capacity}人)`" :value="room.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="会议标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入会议标题" />
        </el-form-item>
        <el-form-item label="会议描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入会议描述（选填）" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" :disabled-hours="disabledHours" :disabled-minutes="disabledMinutes" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" :disabled-hours="disabledHours" :disabled-minutes="disabledMinutes" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">提交预约</el-button>
          <el-button @click="checkAvailability">查看该时段占用情况</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="roomSchedule.length > 0" shadow="never" style="margin-top: 20px">
      <template #header>
        <span style="font-size: 16px; font-weight: 600">该会议室今日预约情况</span>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="item in roomSchedule"
          :key="item.id"
          :timestamp="`${formatTime(item.startTime)} - ${formatTime(item.endTime)}`"
          :type="statusType(item.status)"
        >
          <div>
            <el-tag :type="statusType(item.status)" size="small" style="margin-right: 8px">{{ statusLabel(item.status) }}</el-tag>
            {{ item.title }} — 预约人：{{ item.userName }}
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAvailableRooms } from '../api/room'
import { createReservation, getRoomReservationsByDate } from '../api/reservation'
import { ElMessage } from 'element-plus'

const formRef = ref(null)
const loading = ref(false)
const availableRooms = ref([])
const roomSchedule = ref([])

const form = reactive({
  roomId: null,
  title: '',
  description: '',
  startTime: null,
  endTime: null
})

const rules = {
  roomId: [{ required: true, message: '请选择会议室', trigger: 'change' }],
  title: [{ required: true, message: '请输入会议标题', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

onMounted(async () => {
  const res = await getAvailableRooms()
  availableRooms.value = res.data
})

function disabledHours() {
  const hours = []
  for (let i = 0; i < 8; i++) hours.push(i)
  for (let i = 21; i < 24; i++) hours.push(i)
  return hours
}

function disabledMinutes(selectedHour) {
  const minutes = []
  for (let i = 1; i < 60; i++) {
    if (i % 5 !== 0) minutes.push(i)
  }
  return minutes
}

async function onRoomChange(roomId) {
  if (roomId) {
    await loadRoomSchedule(roomId)
  } else {
    roomSchedule.value = []
  }
}

async function loadRoomSchedule(roomId) {
  try {
    const now = new Date()
    const dateStr = now.toISOString()
    const res = await getRoomReservationsByDate(roomId, dateStr)
    roomSchedule.value = res.data
  } catch (e) {
    roomSchedule.value = []
  }
}

async function checkAvailability() {
  if (!form.roomId) {
    ElMessage.warning('请先选择会议室')
    return
  }
  await loadRoomSchedule(form.roomId)
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (form.startTime && form.endTime && new Date(form.startTime) >= new Date(form.endTime)) {
    ElMessage.error('结束时间必须晚于开始时间')
    return
  }

  loading.value = true
  try {
    await createReservation({
      roomId: form.roomId,
      title: form.title,
      description: form.description,
      startTime: formatDateTime(form.startTime),
      endTime: formatDateTime(form.endTime)
    })
    ElMessage.success('预约提交成功，请等待管理员审批')
    formRef.value.resetFields()
    roomSchedule.value = []
  } catch (e) {
    // handled
  } finally {
    loading.value = false
  }
}

function formatDateTime(date) {
  if (!date) return null
  const d = new Date(date)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const pad = n => String(n).padStart(2, '0')
  return `${pad(d.getHours())}:${pad(d.getMinutes())}`
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
