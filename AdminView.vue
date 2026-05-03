<template>
  <div class="admin-layout">

    <div class="admin-dashboard">
      <transition name="toast-pop">
        <div v-if="toastMessage" class="premium-toast">
          <div class="toast-indicator"></div>
          <span class="toast-text">{{ toastMessage }}</span>
        </div>
      </transition>

      <header class="dashboard-header">
        <div class="header-content">
          <div class="brand-section">
            <div class="brand-orb"></div>
            <div class="brand-text">
              <span class="sub-title">System Management</span>
              <h1>Account Directory</h1>
            </div>
          </div>

          <div class="search-box">
            <span class="search-icon">🔍</span>
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="Search username, email or role..." 
            />
          </div>

          <div class="header-stats">
            <div class="stat-item">
              <span class="stat-val">{{ filteredUsers.length }}</span>
              <span class="stat-label">Results</span>
            </div>
          </div>
        </div>
      </header>

      <main class="content-area">
        <div class="glass-card">
          <table class="premium-table">
            <thead>
              <tr>
                <th width="80">Access</th>
                <th width="180">Created At</th>
                <th>Username</th>
                <th>Email Address</th>
                <th>Home Address</th>
                <th>Role</th>
                <th width="140" style="text-align: right;">Operations</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in filteredUsers" :key="user.username" :class="{ 'is-locked': !user.isActive }">
                <td>
                  <div :class="['status-marker', user.isActive ? 'st-active' : 'st-locked']"></div>
                </td>
                <td><span class="u-info">{{ user.createdAt || '—' }}</span></td>
                <td><span class="u-name">{{ user.username }}</span></td>
                <td><span class="u-info">{{ user.email || '—' }}</span></td>
                <td><span class="u-info truncate">{{ user.address || '—' }}</span></td>
                <td>
                  <span :class="['role-tag', user.role ? 'has-role' : 'no-role']">
                    {{ user.role || 'Unassigned' }}
                  </span>
                </td>
                <td>
                  <div class="action-row">
                    <button @click="toggleUserLock(user)" class="btn-round" :title="user.isActive ? 'SUSPENDED' : 'ACTIVE'">
                      {{ user.isActive ? '🔓' : '🔒' }}
                    </button>
                    <button @click="openEditor(user)" class="btn-round highlight" title="Modify User">
                      ✍️
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="filteredUsers.length === 0">
                <td colspan="7" class="empty-results">No matching users found.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </main>

      <button class="prime-fab" @click="openCreator">
        <span class="fab-icon">+</span>
        <span class="fab-label">Add New User</span>
      </button>

      <transition name="modal-zoom">
        <div v-if="isModalVisible" class="modal-backdrop" @click.self="closeModal">
          <div class="modal-window">
            <div class="modal-decoration"></div>
            <div class="modal-header">
              <h3>{{ isEditMode ? 'Update Profile' : 'Register Account' }}</h3>
              <p>Update any user credentials below.</p>
            </div>

            <div class="modal-body">
              <div class="grid-form">
                <div class="input-wrapper">
                  <label>Username</label>
                  <input v-model="userForm.username" type="text" placeholder="3-16 chars" />
                </div>
                <div class="input-wrapper">
                  <label>Password</label>
                  <input v-model="userForm.password" type="text" placeholder="Min. 6 chars" />
                </div>
                <div class="input-wrapper full-width">
                  <label>Email Address</label>
                  <input v-model="userForm.email" type="email" placeholder="example@domain.com" />
                </div>
                <div class="input-wrapper full-width">
                  <label>Home Address</label>
                  <input v-model="userForm.address" type="text" placeholder="Full residential address" />
                </div>
                <div class="input-wrapper full-width">
                  <label>Assign Role</label>
                  <select v-model="userForm.role">
                    <option value="" disabled>Select System Role</option>
                    <option value="Donee">Donee</option>
                    <option value="Fund Raiser">Fund Raiser</option>
                    <option value="Platform Manager">Platform Manager</option>
                    <option value="Administrator">Administrator</option>
                  </select>
                </div>
              </div>
            </div>

            <div class="modal-actions">
              <button class="btn-ghost" @click="closeModal">Discard</button>
              <button class="btn-prime" @click="submitForm">
                {{ isEditMode ? 'Save Changes' : 'Confirm' }}
              </button>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const allUsers = ref([])
const searchQuery = ref('')
const isModalVisible = ref(false)
const isEditMode = ref(false)
const currentSelectedUser = ref(null)
const toastMessage = ref('')

const userForm = ref({ 
  username: '', 
  password: '', 
  email: '', 
  address: '', 
  role: '', 
  isActive: false 
})

onMounted(() => {
  const storedData = localStorage.getItem('app_users')
  allUsers.value = storedData ? JSON.parse(storedData) : []
})

// 侧边栏跳转功能
const goToProfile = () => {
  // 如果你配置了 vue-router，它会跳转到 /profile 页面
  // 如果没有配置，此行代码会报错，你可以改为提示：alert('Navigate to Profile')
  router.push('/profile')
}

const filteredUsers = computed(() => {
  if (!searchQuery.value) return allUsers.value
  const query = searchQuery.value.toLowerCase()
  return allUsers.value.filter(user => 
    user.username.toLowerCase().includes(query) || 
    (user.email && user.email.toLowerCase().includes(query)) ||
    (user.role && user.role.toLowerCase().includes(query))
  )
})

const triggerToast = (message) => {
  toastMessage.value = message
  setTimeout(() => { toastMessage.value = '' }, 3000)
}

const syncLocalStorage = () => {
  localStorage.setItem('app_users', JSON.stringify(allUsers.value))
}

const toggleUserLock = (user) => {
  user.isActive = !user.isActive
  syncLocalStorage()
  triggerToast(user.isActive ? 'Access Enabled' : 'Access Restricted')
}

const openCreator = () => {
  isEditMode.value = false
  userForm.value = { username: '', password: '', email: '', address: '', role: '', isActive: false }
  isModalVisible.value = true
}

const openEditor = (user) => {
  isEditMode.value = true
  currentSelectedUser.value = user
  userForm.value = { ...user }
  isModalVisible.value = true
}

const closeModal = () => { isModalVisible.value = false }

const submitForm = () => {
  const { username, password } = userForm.value
  if (username.length < 3 || username.length > 16) return triggerToast('Username: 3-16 chars')
  if (password.length < 6) return triggerToast('Password: Min 6 chars')

  if (isEditMode.value) {
    const userIndex = allUsers.value.indexOf(currentSelectedUser.value)
    if (userIndex !== -1) {
      allUsers.value[userIndex] = { ...userForm.value }
    }
    triggerToast('Profile updated')
  } else {
    if (allUsers.value.some(u => u.username === username)) return triggerToast('Username taken')
    
    // 时间格式: 02/05/2026 13:44
    const now = new Date()
    const pad = (n) => n.toString().padStart(2, '0')
    const createdAt = `${pad(now.getDate())}/${pad(now.getMonth() + 1)}/${now.getFullYear()} ${pad(now.getHours())}:${pad(now.getMinutes())}`
    
    allUsers.value.push({ ...userForm.value, isActive: false, createdAt })
    triggerToast('Account Created')
  }
  syncLocalStorage()
  closeModal()
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap');

/* 布局外层 */
.admin-layout { display: flex; min-height: 100vh; background: #fcfcfd; }

.nav-item {
  width: 50px;
  height: 50px;
  border-radius: 15px;
  background: rgba(255,255,255,0.05);
  border: none;
  color: white;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: 0.3s;
}
.nav-item:hover { background: #3b82f6; transform: scale(1.05); }
.nav-icon { font-weight: 800; font-size: 14px; }
.nav-label { font-size: 9px; margin-top: 2px; opacity: 0.7; }

/* 主区域 (留出侧边栏宽度) */
.admin-dashboard { flex: 1; margin-left: 10px; padding: 50px 120px; position: relative; }

/* 状态灯颜色：激活绿，停用红 */
.status-marker { width: 8px; height: 8px; border-radius: 50%; }
.st-active { background: #10b981; box-shadow: 0 0 10px #10b981; }
.st-locked { background: #ef4444; box-shadow: 0 0 10px #ef4444; }

/* 剩下的原有样式保持不变 */
.dashboard-header { margin-bottom: 40px; }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.brand-section { display: flex; align-items: center; gap: 15px; }
.brand-orb { width: 10px; height: 10px; background: #3b82f6; border-radius: 50%; }
.brand-text h1 { font-size: 28px; font-weight: 800; color: #111827; margin: 0; }
.sub-title { font-size: 11px; font-weight: 800; color: #94a3b8; text-transform: uppercase; letter-spacing: 1.5px; }
.search-box { flex: 1; max-width: 400px; margin: 0 40px; position: relative; }
.search-icon { position: absolute; left: 15px; top: 50%; transform: translateY(-50%); font-size: 14px; opacity: 0.4; }
.search-box input { width: 100%; padding: 12px 15px 12px 40px; border-radius: 15px; border: 1px solid #f1f5f9; background: #f8fafc; font-size: 14px; outline: none; }

.glass-card { background: white; border-radius: 24px; border: 1px solid #f1f5f9; box-shadow: 0 4px 30px rgba(0,0,0,0.02); overflow: hidden; }
.premium-table { width: 100%; border-collapse: collapse; }
.premium-table th { padding: 20px; background: #fafbfc; text-align: left; font-size: 11px; font-weight: 800; color: #94a3b8; text-transform: uppercase; }
.premium-table td { padding: 20px; border-bottom: 1px solid #f8fafc; font-size: 14px; }

.u-name { font-weight: 700; color: #111827; }
.u-info { color: #64748b; }
.truncate { max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: block; }
.role-tag { padding: 5px 12px; border-radius: 8px; font-size: 12px; font-weight: 700; }
.has-role { background: #eff6ff; color: #3b82f6; }
.no-role { background: #f1f5f9; color: #cbd5e1; }

.action-row { display: flex; gap: 8px; justify-content: flex-end; }
.btn-round { width: 40px; height: 40px; border-radius: 12px; border: 1px solid #f1f5f9; background: white; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 16px; }

.prime-fab { position: fixed; bottom: 50px; right: 50px; padding: 16px 28px; border-radius: 20px; background: #0f172a; color: white; border: none; font-weight: 700; cursor: pointer; display: flex; align-items: center; gap: 10px; box-shadow: 0 20px 40px rgba(0,0,0,0.15); transition: 0.3s; }
.prime-fab:hover { background: #3b82f6; transform: translateY(-5px); }

/* 弹窗与 Toast 样式省略，与之前保持一致 */
.modal-backdrop { position: fixed; inset: 0; background: rgba(15,23,42,0.1); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-window { background: white; width: 480px; padding: 40px; border-radius: 30px; box-shadow: 0 30px 80px rgba(0,0,0,0.1); }
.grid-form { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.full-width { grid-column: span 2; }
.input-wrapper label { display: block; font-size: 10px; font-weight: 800; color: #3b82f6; text-transform: uppercase; margin-bottom: 8px; }
.input-wrapper input, .input-wrapper select { width: 100%; padding: 12px 16px; border: 1px solid #f1f5f9; border-radius: 12px; background: #fcfcfd; font-size: 14px; box-sizing: border-box; }
.modal-actions { display: flex; gap: 10px; margin-top: 35px; }
.btn-prime { flex: 2; padding: 15px; background: #3b82f6; color: white; border: none; border-radius: 15px; font-weight: 700; cursor: pointer; }
.btn-ghost { flex: 1; padding: 15px; background: #f8fafc; color: #94a3b8; border: none; border-radius: 15px; font-weight: 700; cursor: pointer; }

.premium-toast { position: fixed; top: 40px; left: 50%; transform: translateX(-50%); background: white; padding: 14px 25px; border-radius: 20px; box-shadow: 0 10px 40px rgba(0,0,0,0.1); display: flex; align-items: center; gap: 12px; z-index: 2000; }
.toast-indicator { width: 6px; height: 6px; background: #3b82f6; border-radius: 50%; animation: pulse 1.5s infinite; }
@keyframes pulse { 0% { transform: scale(1); opacity: 1; } 50% { transform: scale(1.5); opacity: 0.5; } 100% { transform: scale(1); opacity: 1; } }
</style>