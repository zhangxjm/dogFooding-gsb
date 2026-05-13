<template>
  <div class="room-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>会议室管理</span>
          <el-button type="primary" @click="handleAdd">新增会议室</el-button>
        </div>
      </template>
      
      <el-table :data="roomList" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="会议室名称" min-width="150" />
        <el-table-column prop="location" label="位置" min-width="150" />
        <el-table-column prop="capacity" label="容纳人数" width="100" />
        <el-table-column prop="facilities" label="设施" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ formatFacilities(row.facilities) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">可用</el-tag>
            <el-tag v-else type="danger">不可用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑会议室' : '新增会议室'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="会议室名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入会议室名称" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="容纳人数" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="500" />
        </el-form-item>
        <el-form-item label="设施">
          <el-select v-model="selectedFacilities" multiple placeholder="选择设施" style="width: 100%">
            <el-option label="投影仪" value="投影仪" />
            <el-option label="白板" value="白板" />
            <el-option label="音响" value="音响" />
            <el-option label="电视" value="电视" />
            <el-option label="视频会议系统" value="视频会议系统" />
            <el-option label="同声传译" value="同声传译" />
            <el-option label="茶歇" value="茶歇" />
            <el-option label="沙发" value="沙发" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">可用</el-radio>
            <el-radio :label="0">不可用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoomList, addRoom, updateRoom, deleteRoom } from '@/api/room'

const loading = ref(false)
const submitLoading = ref(false)
const roomList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const selectedFacilities = ref([])

const form = ref({
  id: null,
  name: '',
  location: '',
  capacity: 10,
  facilities: '',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入会议室名称', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容纳人数', trigger: 'blur' }]
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

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRoomList()
    roomList.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    location: '',
    capacity: 10,
    facilities: '',
    description: '',
    status: 1
  }
  selectedFacilities.value = []
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  try {
    selectedFacilities.value = JSON.parse(row.facilities || '[]')
  } catch {
    selectedFacilities.value = []
  }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该会议室吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteRoom(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        form.value.facilities = JSON.stringify(selectedFacilities.value)
        if (isEdit.value) {
          await updateRoom(form.value.id, form.value)
          ElMessage.success('更新成功')
        } else {
          await addRoom(form.value)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

watch(selectedFacilities, (val) => {
  form.value.facilities = JSON.stringify(val)
})

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
