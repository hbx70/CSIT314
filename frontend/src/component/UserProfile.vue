<template>
  <div class="profile-view-container">
    <header class="profile-header">
      <div class="brand-section">
        <div class="brand-orb"></div>
        <div class="brand-text">
          <span class="sub-title">System Management</span>
          <h1>User Profile Configuration</h1>
        </div>
      </div>
    </header>

    <div class="glass-card">
      <table class="premium-table">
        <thead>
          <tr>
            <th width="80">STATUS</th>
            <th width="220">CREATED AT</th>
            <th width="180">NAME</th>
            <th>DESCRIPTION</th>
            <th width="120" style="text-align: right">ACTIONS</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in profileList" :key="index" :class="{ 'is-locked': !item.isActive }">
            <td><div :class="['status-marker', item.isActive ? 'st-active' : 'st-locked']"></div></td>
            
            <td>
              <div class="time-badge">
                <span class="clock-icon">🕒</span>
                <span class="u-info-bold">{{ item.createdTime }}</span>
              </div>
            </td>

            <td><span class="u-name">{{ item.username }}</span></td>
            <td><span class="u-info">{{ item.description }}</span></td>
            <td>
              <div class="action-row">
                <button @click="toggleLock(item)" class="btn-round">{{ item.isActive ? '🔓' : '🔒' }}</button>
                <button @click="openEditor(item, index)" class="btn-round">✍️</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <button class="prime-fab" @click="openCreator">+ Add Profile</button>

    <div v-if="isModalVisible" class="modal-backdrop" @click.self="closeModal">
      <div class="modal-window">
        <h3>{{ isEditMode ? 'Update Profile' : 'New Profile' }}</h3>
        <div class="form-item">
          <label>Profile Name (Role)</label>
          <select v-model="form.username" class="custom-input">
            <option value="Administrator">Administrator</option>
            <option value="Donee">Donee</option>
            <option value="Fund Raiser">Fund Raiser</option>
            <option value="Platform Management">Platform Management</option>
          </select>
        </div>
        <div class="form-item">
          <label>Description</label>
          <input v-model="form.description" class="custom-input" placeholder="Role description..." />
        </div>
        <div class="modal-actions">
          <button @click="closeModal" class="btn-ghost">Cancel</button>
          <button @click="submitForm" class="btn-prime">Save</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const profileList = ref([])
const isModalVisible = ref(false), isEditMode = ref(false), editIndex = ref(-1)
const form = ref({ username: '', description: '' })

onMounted(() => {
  const saved = localStorage.getItem('user_profiles_final')
  // 初始默认数据也同步为 日/月/年 格式
  profileList.value = saved ? JSON.parse(saved) : [{ username: 'Administrator', description: 'System Root', isActive: true, createdTime: '05/02/2026 13:44' }]
  updateAllowedRoles()
})

const updateAllowedRoles = () => {
  const activeRoles = profileList.value.filter(p => p.isActive).map(p => p.username)
  localStorage.setItem('allowedRoles', JSON.stringify(activeRoles))
}

const openCreator = () => { isEditMode.value = false; form.value = { username: '', description: '' }; isModalVisible.value = true; }
const openEditor = (item, index) => { isEditMode.value = true; editIndex.value = index; form.value = { ...item }; isModalVisible.value = true; }
const closeModal = () => isModalVisible.value = false

const toggleLock = (item) => { item.isActive = !item.isActive; saveAndSync(); }

const submitForm = () => {
  if (isEditMode.value) {
    profileList.value[editIndex.value] = { ...profileList.value[editIndex.value], ...form.value }
  } else {
    // 核心修改：生成 DD/MM/YYYY HH:mm 格式
    const now = new Date();
    const day = String(now.getDate()).padStart(2, '0');
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const year = now.getFullYear();
    const time = now.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });
    
    const formattedDate = `${day}/${month}/${year} ${time}`;
    
    profileList.value.push({ ...form.value, isActive: true, createdTime: formattedDate })
  }
  saveAndSync(); closeModal();
}

const saveAndSync = () => {
  localStorage.setItem('user_profiles_final', JSON.stringify(profileList.value))
  updateAllowedRoles()
}
</script>

<style scoped>
.profile-view-container { padding: 40px 60px; }
.profile-header { margin-bottom: 30px; }
.brand-orb { width: 8px; height: 8px; background: #3b82f6; border-radius: 50%; display: inline-block; margin-right: 10px; }
.sub-title { font-size: 10px; font-weight: 800; color: #94a3b8; text-transform: uppercase; }
.glass-card { background: white; border-radius: 16px; border: 1px solid #f1f5f9; box-shadow: 0 4px 20px rgba(0,0,0,0.02); overflow: hidden; }
.premium-table { width: 100%; border-collapse: collapse; }
.premium-table th { background: #fafbfc; padding: 15px 20px; text-align: left; font-size: 11px; color: #94a3b8; border-bottom: 1px solid #f1f5f9; }
.premium-table td { padding: 18px 20px; border-bottom: 1px solid #f8fafc; }

.time-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f1f5f9;
  padding: 6px 12px;
  border-radius: 8px;
  width: fit-content;
}
.clock-icon { font-size: 12px; }
.u-info-bold { 
  color: #475569; 
  font-size: 13px; 
  font-weight: 700;
}

.status-marker { width: 8px; height: 8px; border-radius: 50%; }
.st-active { background: #10b981; box-shadow: 0 0 8px #10b981; }
.st-locked { background: #ef4444; }
.u-name { font-weight: 700; color: #111827; }
.u-info { color: #64748b; font-size: 13px; }
.action-row { display: flex; gap: 10px; justify-content: flex-end; }
.btn-round { background: white; border: 1px solid #f1f5f9; cursor: pointer; padding: 5px 8px; border-radius: 8px; }
.prime-fab { position: fixed; bottom: 40px; right: 40px; background: #0f172a; color: white; padding: 12px 25px; border-radius: 12px; border: none; font-weight: 700; cursor: pointer; box-shadow: 0 10px 20px rgba(0,0,0,0.1); }
.modal-backdrop { position: fixed; inset: 0; background: rgba(0,0,0,0.3); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-window { background: white; padding: 30px; border-radius: 20px; width: 360px; }
.form-item { margin-bottom: 20px; }
.form-item label { display: block; font-size: 11px; color: #3b82f6; font-weight: 800; margin-bottom: 8px; }
.custom-input { width: 100%; padding: 12px; border: 1px solid #f1f5f9; border-radius: 10px; background: #f8fafc; box-sizing: border-box; }
.modal-actions { display: flex; gap: 10px; }
.btn-prime { flex: 1; padding: 12px; background: #3b82f6; color: white; border: none; border-radius: 10px; font-weight: 700; cursor: pointer; }
.btn-ghost { padding: 12px; background: #f1f5f9; border: none; border-radius: 10px; color: #64748b; cursor: pointer; }
</style>