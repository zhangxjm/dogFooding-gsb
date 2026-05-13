<template>
  <div class="reservation">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预约会议室</span>
        </div>
      </template>
      
      <el-steps :active="activeStep" finish-status="success" simple style="margin-bottom: 30px;">
        <el-step title="选择会议室" />
        <el-step title="选择时间" />
        <el-step title="填写信息" />
      </el-steps>

      <!-- 步骤1: 选择会议室 -->
      <div v-if="activeStep === 0">
        <el-row :gutter="20">
          <el-col :span="8" v-for="room in roomList" :key="room.id">
            <el-card 
              :class="['room-card', { selected: selectedRoom?.id === room.id }]"
              @click="selectRoom(room)"
              shadow="hover"
            >
              <div class="room-header">
                <h3>{{ room.name }}</h3>
                <el-tag size="small" type="success">容纳{{ room.capacity }}人</el-tag>
              </div>
              <p class="room-location">
                <el-icon><Location /></el-icon>
                {{ room.location }}
              </p>
              <p class="room-facilities">
                <el-icon><Collection /></el-icon>
                {{ formatFacilities(room.facilities) }}
              </p>
              <p class="room-description">{{ room.description }}</p>
            </el-card>
          </el-col>
        </el-row>
        <div class="step-actions">
          <el-button type="primary" @click="nextStep" :disabled="!selectedRoom">下一步</el-button>
        </div>
      </div>

      <!-- 步骤2: 选择时间 -->
      <div v-if="activeStep === 1">
        <el-form :inline="true">
          <el-form-item label="选择日期">
            <el-date-picker
              v-model="selectedDate"
              type="date"
              placeholder="选择日期"
              :disabled-date="disabledDate"
              @change="handleDateChange"
            />
          </el-form-item>
        </el-form>
        
        <div class="time-slots" v-loading="timeSlotsLoading">
          <h4>可用时间段（{{ selectedRoom?.name }}）</h4>
          <div class="slots-grid">
            <div
              v-for="slot in timeSlots"
              :key="slot.startTime"
              :class="['slot-item', { 
                available: slot.available, 
                unavailable: !slot.available,
                selected: isTimeSlotSelected(slot)
              }]"
              @click="selectTimeSlot(slot)"
            >
              <span class="time">{{ slot.startTime }} - {{ slot.endTime }}</span>
              <el-tag v-if="!slot.available" type="danger" size="small">已占用</el-tag>
              <el-tag v-else type="success" size="small">可预约</el-tag>
            </div>
          </div>
        </div>
        
        <div class="selected-time" v-if="selectedStartTime && selectedEndTime">
          <p>已选择时间：<strong>{{ selectedDateStr }} {{ selectedStartTime }} - {{ selectedEndTime }}</strong></p>
        </div>
        
        <div class="step-actions">
          <el-button @click="prevStep">上一步</el-button>
          <el-button type="primary" @click="nextStep" :disabled="!selectedStartTime">下一步</el-button>
        </div>
      </div>

      <!-- 步骤3: 填写信息 -->
      <div v-if="activeStep === 2">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 600px;">
          <el-form-item label="会议室">
            <el-input :model-value="selectedRoom?.name" disabled />
          </el-form-item>
          <el-form-item label="预约时间">
            <el-input :model-value="`${selectedDateStr} ${selectedStartTime} - ${selectedEndTime}`" disabled />
          </el-form-item>
          <el-form-item label="会议主题" prop="title">
            <el-input v-model="form.title" placeholder="请输入会议主题" />
          </el-form-item>
          <el-form-item label="参会人数" prop="attendees">
            <el-input-number v-model="form.attendees" :min="1" :max="selectedRoom?.capacity || 100" />
          </el-form-item>
          <el-form-item label="会议描述">
            <el-input v-model="form.description" type="textarea" rows="4" placeholder="请输入会议描述（选填）" />
          </el-form-item>
        </el-form>
        
        <div class="step-actions">
          <el-button @click="prevStep">上一步</el-button>
          <el-button type="primary" @click="submitReservation" :loading="submitLoading">提交预约</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { getRoomList } from '@/api/room'
import { getRoomTimeSlots, createReservation } from '@/api/reservation'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeStep = ref(0)
const roomList = ref([])
const selectedRoom = ref(null)
const selectedDate = ref(dayjs().add(1, 'day').toDate())
const selectedDateStr = computed(() => dayjs(selectedDate.value).format('YYYY-MM-DD'))
const timeSlots = ref([])
const timeSlotsLoading = ref(false)
const selectedStartTime = ref('')
const selectedEndTime = ref('')
const submitLoading = ref(false)

const form = ref({
  title: '',
  attendees: 2,
  description: ''
})

const formRef = ref(null)

const rules = {
  title: [{ required: true, message: '请输入会议主题', trigger: 'blur' }],
  attendees: [{ required: true, message: '请输入参会人数', trigger: 'blur' }]
}

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const formatFacilities = (facilities) => {
  if (!facilities) return '-'
  try {
    const list = JSON.parse(facilities)
    return Array.isArray(list) ? list.join('、') : facilities
  } catch {
    return facilities
  }
}

const fetchRooms = async () => {
  try {
    const res = await getRoomList()
    roomList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const selectRoom = (room) => {
  selectedRoom.value = room
}

const handleDateChange = async () => {
  if (!selectedRoom.value || !selectedDate.value) return
  
  timeSlotsLoading.value = true
  selectedStartTime.value = ''
  selectedEndTime.value = ''
  
  try {
    const res = await getRoomTimeSlots(selectedRoom.value.id, selectedDateStr.value)
    timeSlots.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    timeSlotsLoading.value = false
  }
}

const isTimeSlotSelected = (slot) => {
  return selectedStartTime.value === slot.startTime && selectedEndTime.value === slot.endTime
}

const selectTimeSlot = (slot) => {
  if (!slot.available) {
    ElMessage.warning('该时间段已被占用')
    return
  }
  selectedStartTime.value = slot.startTime
  selectedEndTime.value = slot.endTime
}

const nextStep = () => {
  if (activeStep.value < 2) {
    activeStep.value++
    if (activeStep.value === 1) {
      handleDateChange()
    }
  }
}

const prevStep = () => {
  if (activeStep.value > 0) {
    activeStep.value--
  }
}

const submitReservation = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const startTime = `${selectedDateStr.value} ${selectedStartTime.value}:00`
        const endTime = `${selectedDateStr.value} ${selectedEndTime.value}:00`
        
        await createReservation({
          roomId: selectedRoom.value.id,
          title: form.value.title,
          attendees: form.value.attendees,
          startTime: startTime,
          endTime: endTime,
          description: form.value.description
        })
        
        ElMessage.success('预约成功，等待审批')
        router.push('/my-reservations')
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchRooms()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.room-card:hover {
  transform: translateY(-2px);
}

.room-card.selected {
  border: 2px solid #409EFF;
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.room-header h3 {
  margin: 0;
  font-size: 16px;
}

.room-location,
.room-facilities {
  color: #666;
  font-size: 13px;
  margin: 5px 0;
  display: flex;
  align-items: center;
  gap: 5px;
}

.room-description {
  color: #999;
  font-size: 12px;
  margin-top: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.step-actions {
  margin-top: 30px;
  text-align: center;
}

.time-slots {
  margin-top: 20px;
}

.time-slots h4 {
  margin-bottom: 15px;
  color: #333;
}

.slots-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.slot-item {
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
  transition: all 0.3s;
}

.slot-item.available {
  background-color: #f0f9ff;
  border-color: #409EFF;
}

.slot-item.available:hover {
  background-color: #409EFF;
  color: white;
}

.slot-item.unavailable {
  background-color: #f5f7fa;
  cursor: not-allowed;
  opacity: 0.6;
}

.slot-item.selected {
  background-color: #409EFF;
  color: white;
  border-color: #409EFF;
}

.slot-item .time {
  display: block;
  font-size: 13px;
  margin-bottom: 5px;
}

.selected-time {
  margin-top: 20px;
  padding: 15px;
  background-color: #f0f9ff;
  border-radius: 4px;
  text-align: center;
}
</style>
