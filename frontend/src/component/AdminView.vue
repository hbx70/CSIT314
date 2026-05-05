<template>
    <div class="admin-dashboard">
        <transition name="toast-pop">
            <div v-if="toastMsg" class="premium-toast">
                <div class="toast-indicator"></div>
                <span class="toast-text">{{ toastMsg }}</span>
            </div>
        </transition>

        <!-- ffhfhf -->

        <header class="dashboard-header">
            <div class="header-content">
                <div class="brand-section">
                    <div class="brand-orb"></div>
                    <div class="brand-text">
                        <span class="sub-title">System Management</span>
                        <h1>Account Management</h1>
                    </div>
                </div>
                <div class="header-stats">
                    <div class="stat-item">
                        <span class="stat-val">{{ allUsers.length }}</span>
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
                            <th>Username</th>
                            <th>Email Address</th>
                            <th>Home Address</th>
                            <th>Role</th>
                            <th width="140" style="text-align: right;">Operations</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(user, index) in allUsers" :key="index" :class="{ 'is-locked': user.status === 'SUSPENDED' }">
                            <td>
                                <div :class="['status-marker', user.status === 'ACTIVE' ? 'st-active' : 'st-locked']"></div>
                            </td>
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
                                    <el-button @click="openEditor(index, user.id)" type="primary" :icon="Edit" circle />
                                </div>
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
                                <input v-model="form.password" type="text" placeholder="Min. 6 chars" />
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
</template>

<script setup>
import { Edit, Lock, Unlock} from '@element-plus/icons-vue'
import { getAllUserAccountsService } from '@/api/userProfile'
import { ref, onMounted } from 'vue'
import { activateUserAccountService, createUserAccountService, suspendUserAccountService, updateUserAccountService } from '@/api/userAccount'
import { ElMessage } from 'element-plus'

const allUsers = ref([])
const showModal = ref(false)
const isEdit = ref(false)
const targetIndex = ref(null)
const targetUserAccountId = ref(null)
const toastMsg = ref('')

const form = ref({ username: '', password: '', email: '', address: '', userProfileName: ''})

const getAllUserAccounts = async () => {
    const userInfoList = await getAllUserAccountsService();
    console.log(userInfoList);
    
    allUsers.value = userInfoList
}

onMounted(() => {
    getAllUserAccounts()
})

const suspendUser = async (user) => {
    if (user.status === 'SUSPENDED') {
        return
    }
    const isSuspended = await suspendUserAccountService(user.id)
    if (isSuspended) {
        ElMessage.success("Suspended successfully")
    } else {
        ElMessage.error("Operation failure")
    }
    getAllUserAccounts()
}

const activateUser = async (user) => {
    if (user.status === 'ACTIVE') {
        return
    }
    const isActivated = await activateUserAccountService(user.id)
    if (isActivated) {
        ElMessage.success("Activated successfully")
    } else {
        ElMessage.error("Operation failure")
    }
    getAllUserAccounts()
}

const openCreator = () => {
    isEdit.value = false
    form.value = { username: '', password: '', email: '', address: '', userProfileName: ''}
    showModal.value = true
}

const openEditor = (index, userAccountId) => {
    isEdit.value = true
    targetIndex.value = index
    targetUserAccountId.value = userAccountId
    form.value = { ...allUsers.value[index] }
    showModal.value = true
}

const closeModal = () => {
    showModal.value = false
    form.value = { username: '', password: '', email: '', address: '', userProfileName: ''}
}

const submitForm = async () => {
    const { username, password, email, address, userProfileName } = form.value
    if (username.trim().length < 3 || username.trim().length > 16) return ElMessage.error('Username: 3-16 chars')
    if (password === null ? false : password.trim().length < 6) return ElMessage.error('Password: Min 6 chars')
    if (email.trim().length <= 0) return ElMessage.error('Please enter email')
    if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email.trim())) return ElMessage.error('Please enter valid email')
    if (address.trim().length <= 0) return ElMessage.error('Please enter address')
    if (userProfileName.trim().length <= 0) return ElMessage.error('Please selete user profile')

    if (allUsers.value.some(u => u.username === username.trim())) return ElMessage.error('Username taken');
    if (allUsers.value.some(u => u.email === email.trim())) return ElMessage.error('Email taken');

    if (isEdit.value) {
        const newUserAccountData = {...form.value, id: targetUserAccountId.value}
        const isUpdated = await updateUserAccountService(newUserAccountData);
        if (isUpdated) {
            ElMessage.success("updated account")
        } else {
            ElMessage.error("Operation failure")
        }
    } else {
        const isCreated = await createUserAccountService(form.value);
        if (isCreated) {
            ElMessage.success("Created account")
        } else {
            ElMessage.error("Operation failure")
        }
    }
    closeModal()
    getAllUserAccounts()
}
</script>

<style scoped>
/* 引入现代字体 */
@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap');

.admin-dashboard {
    padding: 50px 80px;
    background: #fcfcfd;
    min-height: 100vh;
    font-family: 'Plus Jakarta Sans', sans-serif;
}

/* 头部设计 */
.dashboard-header {
    margin-bottom: 40px;
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.brand-section {
    display: flex;
    align-items: center;
    gap: 15px;
}

.brand-orb {
    width: 10px;
    height: 10px;
    background: #3b82f6;
    border-radius: 50%;
    box-shadow: 0 0 15px rgba(59, 130, 246, 0.6);
}

.brand-text h1 {
    font-size: 28px;
    font-weight: 800;
    color: #111827;
    margin: 0;
}

.sub-title {
    font-size: 11px;
    font-weight: 800;
    color: #94a3b8;
    text-transform: uppercase;
    letter-spacing: 1.5px;
}

/* 状态展示 */
.stat-item {
    background: white;
    padding: 12px 25px;
    border-radius: 16px;
    border: 1px solid #f1f5f9;
    text-align: center;
}

.stat-val {
    font-size: 22px;
    font-weight: 800;
    color: #3b82f6;
    display: block;
}

.stat-label {
    font-size: 11px;
    font-weight: 700;
    color: #94a3b8;
}

/* 表格卡片 */
.glass-card {
    background: white;
    border-radius: 24px;
    border: 1px solid #f1f5f9;
    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.02);
    overflow: hidden;
}

.premium-table {
    width: 100%;
    border-collapse: collapse;
}

.premium-table th {
    padding: 20px;
    background: #fafbfc;
    text-align: left;
    font-size: 11px;
    font-weight: 800;
    color: #94a3b8;
    text-transform: uppercase;
}

.premium-table td {
    padding: 20px;
    border-bottom: 1px solid #f8fafc;
    font-size: 14px;
}

.u-name {
    font-weight: 700;
    color: #111827;
}

.u-info {
    color: #64748b;
}

.truncate {
    max-width: 250px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block;
}

/* 状态标记 */
.status-marker {
    width: 8px;
    height: 8px;
    border-radius: 50%;
}

.st-active {
    background: #10b981;
    box-shadow: 0 0 10px #10b981;
}

.st-locked {
    background: #ef4444;
    box-shadow: 0 0 10px #ef4444;
}

.is-locked {
    opacity: 0.6;
    background: #fafafa;
}

/* 角色标签 */
.role-tag {
    padding: 5px 12px;
    border-radius: 8px;
    font-size: 12px;
    font-weight: 700;
}

.has-role {
    background: #eff6ff;
    color: #3b82f6;
}

.no-role {
    background: #f1f5f9;
    color: #cbd5e1;
}

/* 操作按钮 */
.action-row {
    display: flex;
    gap: 8px;
    justify-content: flex-end;
}

.btn-round {
    width: 40px;
    height: 40px;
    border-radius: 12px;
    border: 1px solid #f1f5f9;
    background: white;
    cursor: pointer;
    transition: 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
}

.btn-round:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.btn-round.highlight:hover {
    border-color: #3b82f6;
    color: #3b82f6;
}

/* FAB */
.prime-fab {
    position: fixed;
    bottom: 50px;
    right: 50px;
    padding: 16px 28px;
    border-radius: 20px;
    background: #0f172a;
    color: white;
    border: none;
    font-weight: 700;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 10px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
    transition: 0.3s;
}

.prime-fab:hover {
    background: #3b82f6;
    transform: translateY(-5px);
}

/* 弹窗设计 */
.modal-backdrop {
    position: fixed;
    inset: 0;
    background: rgba(15, 23, 42, 0.1);
    backdrop-filter: blur(8px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal-window {
    background: white;
    width: 480px;
    padding: 40px;
    border-radius: 30px;
    box-shadow: 0 30px 80px rgba(0, 0, 0, 0.1);
    border: 1px solid #f1f5f9;
}

.modal-decoration {
    height: 6px;
    width: 60px;
    background: #3b82f6;
    border-radius: 3px;
    margin-bottom: 25px;
}

.modal-header h3 {
    font-size: 24px;
    font-weight: 800;
    margin: 0;
}

.modal-header p {
    font-size: 13px;
    color: #94a3b8;
    margin: 5px 0 30px;
}

/* 弹窗表单布局 */
.grid-form {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 15px;
}

.full-width {
    grid-column: span 2;
}

.input-wrapper label {
    display: block;
    font-size: 10px;
    font-weight: 800;
    color: #3b82f6;
    text-transform: uppercase;
    margin-bottom: 8px;
}

.input-wrapper input,
.input-wrapper select {
    width: 100%;
    padding: 12px 16px;
    border: 1px solid #f1f5f9;
    border-radius: 12px;
    background: #fcfcfd;
    font-size: 14px;
    box-sizing: border-box;
    transition: 0.2s;
}

.input-wrapper input:focus {
    border-color: #3b82f6;
    outline: none;
    background: white;
    box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.05);
}

.modal-actions {
    display: flex;
    gap: 10px;
    margin-top: 35px;
}

.btn-prime {
    flex: 2;
    padding: 15px;
    background: #3b82f6;
    color: white;
    border: none;
    border-radius: 15px;
    font-weight: 700;
    cursor: pointer;
}

.btn-ghost {
    flex: 1;
    padding: 15px;
    background: #f8fafc;
    color: #94a3b8;
    border: none;
    border-radius: 15px;
    font-weight: 700;
    cursor: pointer;
}

/* Toast */
.premium-toast {
    position: fixed;
    top: 40px;
    left: 50%;
    transform: translateX(-50%);
    background: white;
    padding: 14px 25px;
    border-radius: 20px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    gap: 12px;
    z-index: 2000;
}

.toast-indicator {
    width: 6px;
    height: 6px;
    background: #3b82f6;
    border-radius: 50%;
    animation: pulse 1.5s infinite;
}

@keyframes pulse {
    0% {
        transform: scale(1);
        opacity: 1;
    }

    50% {
        transform: scale(1.5);
        opacity: 0.5;
    }

    100% {
        transform: scale(1);
        opacity: 1;
    }
}

/* 动画 */
.modal-zoom-enter-active {
    transition: 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.modal-zoom-enter-from {
    opacity: 0;
    transform: scale(0.9);
}

.toast-pop-enter-active {
    transition: 0.3s ease-out;
}

.toast-pop-enter-from {
    opacity: 0;
    transform: translate(-50%, -20px);
}
</style>