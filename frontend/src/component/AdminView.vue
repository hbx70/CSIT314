<template>
    <div class="admin-layout">
        <aside class="sidebar-nav">
            <div class="nav-item" @click="goToProfile">
                <span class="nav-icon">👤</span>
                <span class="nav-label">Profile</span>
            </div>
        </aside>

        <div class="admin-dashboard">
            <transition name="toast-pop">
                <div v-if="toastMsg" class="premium-toast">
                    <div class="toast-indicator"></div>
                    <span class="toast-text">{{ toastMsg }}</span>
                </div>
            </transition>

            <header class="dashboard-header">
                <div class="header-content">
                    <div class="brand-section">
                        <div class="brand-orb"></div>
                        <div class="brand-text">
                            <span class="sub-title">System Management</span>
                            <h1>Account Management</h1>
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
                            <span class="stat-label">Total Users</span>
                        </div>
                    </div>
                </div>
            </header>

            <main class="content-area">
                <div class="glass-card">
                    <table class="premium-table">
                        <thead>
                            <tr>
                                <th width="80">Status</th>
                                <th width="160">Created At</th>
                                <th>Username</th>
                                <th>Email Address</th>
                                <th>Home Address</th>
                                <th>Role</th>
                                <th width="140" style="text-align: right;">Operations</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(user, index) in filteredUsers" :key="user.id || index" :class="{ 'is-locked': user.status === 'SUSPENDED' }">
                                <td>
                                    <div :class="['status-marker', user.status === 'ACTIVE' ? 'st-active' : 'st-locked']"></div>
                                </td>
                                <td><span class="u-info">{{ user.createdAt || '—' }}</span></td>
                                <td><span class="u-name">{{ user.username }}</span></td>
                                <td><span class="u-info">{{ user.email || '—' }}</span></td>
                                <td><span class="u-info truncate">{{ user.address || '—' }}</span></td>
                                <td>
                                    <el-tag type="primary">{{ user.userProfileName }}</el-tag>
                                </td>
                                <td>
                                    <div class="action-row">
                                        <el-button type="success" @click="activateUser(user)" :icon="Unlock" circle v-if="user.status==='SUSPENDED'"/>
                                        <el-button type="danger" @click="suspendUser(user)" :icon="Lock" circle v-else />
                                        <el-button @click="openEditor(user)" type="primary" :icon="Edit" circle />
                                    </div>
                                </td>
                            </tr>
                            <tr v-if="filteredUsers.length === 0">
                                <td colspan="7" style="text-align: center; padding: 40px; color: #94a3b8;">
                                    No matching users found.
                                </td>
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
                <div v-if="showModal" class="modal-backdrop" @click.self="closeModal">
                    <div class="modal-window">
                        <div class="modal-decoration"></div>
                        <div class="modal-header">
                            <h3>{{ isEdit ? 'Update Profile' : 'Register Account' }}</h3>
                            <p>Update any user credentials below.</p>
                        </div>

                        <div class="modal-body">
                            <div class="grid-form">
                                <div class="input-wrapper">
                                    <label>Username</label>
                                    <input v-model="form.username" type="text" placeholder="3-16 chars" />
                                </div>
                                <div class="input-wrapper">
                                    <label>Password</label>
                                    <input v-model="form.password" type="password" placeholder="Min. 6 chars" />
                                </div>
                                <div class="input-wrapper full-width">
                                    <label>Email Address</label>
                                    <input v-model="form.email" type="email" placeholder="example@domain.com" />
                                </div>
                                <div class="input-wrapper full-width">
                                    <label>Home Address</label>
                                    <input v-model="form.address" type="text" placeholder="Full residential address" />
                                </div>
                                <div class="input-wrapper full-width">
                                    <label>Assign Profile</label>
                                    <select v-model="form.userProfileName">
                                        <option value="" disabled>Select System Role</option>
                                        <option value="DONEE">Donee</option>
                                        <option value="FUND_RAISER">Fund Raiser</option>
                                        <option value="PLATFORM_MANAGER">Platform Manager</option>
                                        <option value="ADMIN">User Admin</option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="modal-actions">
                            <button class="btn-ghost" @click="closeModal">Discard</button>
                            <button class="btn-prime" @click="submitForm">
                                {{ isEdit ? 'Save Changes' : 'Initialize Account' }}
                            </button>
                        </div>
                    </div>
                </div>
            </transition>
        </div>
    </div>
</template>

<script setup>
import { Edit, Lock, Unlock } from '@element-plus/icons-vue'
import { getAllUserAccountsService } from '@/api/userProfile'
import { activateUserAccountService, createUserAccountService, suspendUserAccountService, updateUserAccountService } from '@/api/userAccount'
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const allUsers = ref([])
const searchQuery = ref('') // 集成：搜索关键词
const showModal = ref(false)
const isEdit = ref(false)
const targetUserAccountId = ref(null)
const toastMsg = ref('')

const form = ref({ username: '', password: '', email: '', address: '', userProfileName: ''})

// --- 集成：代码 B 的搜索逻辑 ---
const filteredUsers = computed(() => {
    if (!searchQuery.value) return allUsers.value
    const query = searchQuery.value.toLowerCase()
    return allUsers.value.filter(user => 
        user.username?.toLowerCase().includes(query) || 
        user.email?.toLowerCase().includes(query) ||
        user.userProfileName?.toLowerCase().includes(query)
    )
})

const getAllUserAccounts = async () => {
    const userInfoList = await getAllUserAccountsService();
    allUsers.value = userInfoList
}

onMounted(() => {
    getAllUserAccounts()
})

const suspendUser = async (user) => {
    const isSuspended = await suspendUserAccountService(user.id)
    if (isSuspended) {
        ElMessage.success("Suspended successfully")
        getAllUserAccounts()
    }
}

const activateUser = async (user) => {
    const isActivated = await activateUserAccountService(user.id)
    if (isActivated) {
        ElMessage.success("Activated successfully")
        getAllUserAccounts()
    }
}

const openCreator = () => {
    isEdit.value = false
    form.value = { username: '', password: '', email: '', address: '', userProfileName: ''}
    showModal.value = true
}

const openEditor = (user) => {
    isEdit.value = true
    targetUserAccountId.value = user.id
    form.value = { ...user } // 直接拷贝对象
    showModal.value = true
}

const closeModal = () => {
    showModal.value = false
}

const goToProfile = () => {
    router.push('/profile')
}

const submitForm = async () => {
    // 保留代码 A 严谨的校验
    const { username, password, email, address, userProfileName } = form.value
    if (username.trim().length < 3 || username.trim().length > 16) return ElMessage.error('Username: 3-16 chars')
    if (!isEdit.value && (!password || password.trim().length < 6)) return ElMessage.error('Password: Min 6 chars')
    if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email.trim())) return ElMessage.error('Valid email required')
    
    if (isEdit.value) {
        const isUpdated = await updateUserAccountService({ ...form.value, id: targetUserAccountId.value });
        if (isUpdated) ElMessage.success("Updated account")
    } else {
        const isCreated = await createUserAccountService(form.value);
        if (isCreated) ElMessage.success("Created account")
    }
    closeModal()
    getAllUserAccounts()
}
</script>

<style scoped>
/* 合并两者的样式精华 */
@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap');

.admin-layout { display: flex; min-height: 100vh; background: #fcfcfd; }

/* 侧边栏样式 */
.sidebar-nav {
    width: 80px;
    background: #0f172a;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 40px;
    gap: 20px;
}

.nav-item {
    width: 50px;
    height: 50px;
    border-radius: 15px;
    background: rgba(255,255,255,0.05);
    color: white;
    cursor: pointer;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    transition: 0.3s;
}

.nav-item:hover { background: #3b82f6; }

.nav-label { font-size: 9px; margin-top: 2px; opacity: 0.7; }

.admin-dashboard {
    flex: 1;
    padding: 50px 60px;
    font-family: 'Plus Jakarta Sans', sans-serif;
}

/* 搜索框样式集成 */
.search-box {
    flex: 1;
    max-width: 400px;
    margin: 0 40px;
    position: relative;
}

.search-icon {
    position: absolute;
    left: 15px;
    top: 50%;
    transform: translateY(-50%);
    opacity: 0.4;
}

.search-box input {
    width: 100%;
    padding: 12px 15px 12px 40px;
    border-radius: 15px;
    border: 1px solid #f1f5f9;
    background: #f8fafc;
    transition: 0.3s;
}

.search-box input:focus {
    border-color: #3b82f6;
    background: white;
    outline: none;
}

/* 基础样式复用代码 A */
.dashboard-header { margin-bottom: 40px; }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.brand-section { display: flex; align-items: center; gap: 15px; }
.brand-orb { width: 10px; height: 10px; background: #3b82f6; border-radius: 50%; box-shadow: 0 0 15px rgba(59, 130, 246, 0.6); }
.brand-text h1 { font-size: 28px; font-weight: 800; color: #111827; margin: 0; }
.sub-title { font-size: 11px; font-weight: 800; color: #94a3b8; text-transform: uppercase; letter-spacing: 1.5px; }

.stat-item { background: white; padding: 12px 25px; border-radius: 16px; border: 1px solid #f1f5f9; text-align: center; }
.stat-val { font-size: 22px; font-weight: 800; color: #3b82f6; display: block; }
.stat-label { font-size: 11px; font-weight: 700; color: #94a3b8; }

.glass-card { background: white; border-radius: 24px; border: 1px solid #f1f5f9; box-shadow: 0 4px 30px rgba(0, 0, 0, 0.02); overflow: hidden; }
.premium-table { width: 100%; border-collapse: collapse; }
.premium-table th { padding: 20px; background: #fafbfc; text-align: left; font-size: 11px; font-weight: 800; color: #94a3b8; text-transform: uppercase; }
.premium-table td { padding: 20px; border-bottom: 1px solid #f8fafc; font-size: 14px; }

.u-name { font-weight: 700; color: #111827; }
.u-info { color: #64748b; }
.truncate { max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: block; }

.status-marker { width: 8px; height: 8px; border-radius: 50%; }
.st-active { background: #10b981; box-shadow: 0 0 10px #10b981; }
.st-locked { background: #ef4444; box-shadow: 0 0 10px #ef4444; }
.is-locked { opacity: 0.6; background: #fafafa; }

.action-row { display: flex; gap: 8px; justify-content: flex-end; }

.prime-fab { position: fixed; bottom: 50px; right: 50px; padding: 16px 28px; border-radius: 20px; background: #0f172a; color: white; border: none; font-weight: 700; cursor: pointer; display: flex; align-items: center; gap: 10px; box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15); transition: 0.3s; }
.prime-fab:hover { background: #3b82f6; transform: translateY(-5px); }

/* 弹窗样式 */
.modal-backdrop { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.1); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-window { background: white; width: 480px; padding: 40px; border-radius: 30px; box-shadow: 0 30px 80px rgba(0, 0, 0, 0.1); border: 1px solid #f1f5f9; }
.grid-form { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
.full-width { grid-column: span 2; }
.input-wrapper label { display: block; font-size: 10px; font-weight: 800; color: #3b82f6; text-transform: uppercase; margin-bottom: 8px; }
.input-wrapper input, .input-wrapper select { width: 100%; padding: 12px 16px; border: 1px solid #f1f5f9; border-radius: 12px; background: #fcfcfd; font-size: 14px; box-sizing: border-box; }
.modal-actions { display: flex; gap: 10px; margin-top: 35px; }
.btn-prime { flex: 2; padding: 15px; background: #3b82f6; color: white; border: none; border-radius: 15px; font-weight: 700; cursor: pointer; }
.btn-ghost { flex: 1; padding: 15px; background: #f8fafc; color: #94a3b8; border: none; border-radius: 15px; font-weight: 700; cursor: pointer; }

/* 动画样式略 (保持代码 A 的逻辑) */
.modal-zoom-enter-active { transition: 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
.modal-zoom-enter-from { opacity: 0; transform: scale(0.9); }
</style>