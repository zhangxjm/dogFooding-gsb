<template>
  <div>
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-size: 16px; font-weight: 600">会议室列表</span>
          <el-button v-if="userStore.isAdmin()" type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon> 新增会议室
          </el-button>
        </div>
      </template>

      <el-table :data="rooms" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="会议室名称" width="160" />
        <el-table-column prop="location" label="位置" width="140" />
        <el-table-column prop="capacity" label="容纳人数" width="100" />
        <el-table-column prop="equipment" label="设备" min-width="200" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '可用' : '不可用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180" />
        <el-table-column v-if="userStore.isAdmin()" label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button type="danger" text size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑会议室' : '新增会议室'" width="500px">
      <el-form ref="roomFormRef" :model="roomForm" :rules="roomRules" label-width="90px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="roomForm.name" placeholder="请输入会议室名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="roomForm.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input-number v-model="roomForm.capacity" :min="1" :max="500" />
        </el-form-item>
        <el-form-item label="设备">
          <el-input v-model="roomForm.equipment" placeholder="如：投影仪,白板" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="roomForm.status" :active-value="1" :inactive-value="0" active-text="可用" inactive-text="不可用" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="roomForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllRooms, createRoom, updateRoom, deleteRoom } from '../api/room'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const rooms = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const roomFormRef = ref(null)

const roomForm = reactive({
  id: null,
  name: '',
  location: '',
  capacity: 10,
  equipment: '',
  status: 1,
  description: ''
})

const roomRules = {
  name: [{ required: true, message: '请输入会议室名称', trigger: 'blur' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容纳人数', trigger: 'blur' }]
}

onMounted(() => {
  loadRooms()
})

async function loadRooms() {
  const res = await getAllRooms()
  rooms.value = res.data
}

function showAddDialog() {
  isEdit.value = false
  Object.assign(roomForm, { id: null, name: '', location: '', capacity: 10, equipment: '', status: 1, description: '' })
  dialogVisible.value = true
}

function showEditDialog(row) {
  isEdit.value = true
  Object.assign(roomForm, { ...row })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await roomFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateRoom(roomForm.id, roomForm)
      ElMessage.success('更新成功')
    } else {
      await createRoom(roomForm)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadRooms()
  } catch (e) {
    // handled
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除会议室「${row.name}」？`, '提示', { type: 'warning' })
  await deleteRoom(row.id)
  ElMessage.success('删除成功')
  loadRooms()
}
</script>
