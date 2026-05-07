<template>
    <div class="profile-view-container">
        <header class="dashboard-header">
            <div class="header-content">
                <div class="brand-section">
                    <div class="brand-orb"></div>
                    <div class="brand-text">
                        <span class="sub-title">System Management</span>
                        <h1>User Profile Management</h1>
                    </div>
                </div>
                <div class="header-stats">
                    <div class="stat-item">
                        <span class="stat-val">{{ profileList.length }}</span>
                        <span class="stat-label">Total Profile</span>
                    </div>
                </div>
            </div>
        </header>

        <div class="filterBlockContainer">
            <div class="filterContainer">
                <div class="radioGroupContainer">
                    <p>Order</p>
                    <el-radio-group v-model="filter.order" size="large" fill="#409eff" @change="searchUserProfiles">
                        <el-radio-button label="Ascending" value="ASC" />
                        <el-radio-button label="Descending" value="DESC" />
                    </el-radio-group>
                </div>
                <div class="radioGroupContainer">
                    <p>Status</p>
                    <el-radio-group v-model="filter.status" size="large" fill="#409eff" @change="searchUserProfiles">
                        <el-radio-button label="ALL" value="all" />
                        <el-radio-button label="ACTIVE" value="ACTIVE" />
                        <el-radio-button label="SUSPENDED" value="SUSPENDED" />
                    </el-radio-group>
                </div>
                <div class="radioGroupContainer">
                    <p>Name</p>
                    <el-radio-group v-model="filter.name" size="large" fill="#409eff" @change="searchUserProfiles">
                        <el-radio-button label="ALL" value="all" />
                        <el-radio-button :label="userProfile" :value="userProfile" :key="index" v-for="(userProfile, index) in Array.from(profileSet)"/>
                    </el-radio-group>
                </div>
            </div>
            <div class="filterContainer">
                <div class="radioGroupContainer">
                    <p>description</p>
                    <el-input v-model="filter.description" size="large" style="width: 350px" placeholder="Search description" @input="searchUserProfiles"/>
                </div>
            </div>
        </div>

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
                    <tr v-for="(userProfile, index) in profileList" :key="index" :class="{ 'is-locked': userProfile.status === 'SUSPENDED' }">
                        <td>
                            <div :class="['status-marker', userProfile.status === 'ACTIVE' ? 'st-active' : 'st-locked']"></div>
                        </td>
                        <td><span class="u-info">{{ formatTime(userProfile.createdAt) }}</span></td>
                        <td>
                            <el-tag :type="userProfile.name === 'ADMIN' ? 'primary' : userProfile.name  === 'PLATFORM_MANAGER' ? 'success' : userProfile.name  === 'DONEE' ? 'info' : 'warning'" size="large">{{ userProfile.name  }}</el-tag>
                        </td>
                        <td><span class="u-info">{{ userProfile.description }}</span></td>
                        <td>
                            <div class="action-row">
                                <el-button type="success" @click="activateUserProfile(userProfile)" :icon="Unlock" circle v-if="userProfile.status==='SUSPENDED'"/>
                                <el-button type="danger" @click="suspendUserProfile(userProfile)" :icon="Lock" circle v-else />
                                <el-button @click="openEditor(userProfile)" type="primary" :icon="Edit" circle />
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
                    <label>User Profile Name</label>
                    <select v-model="form.name" class="custom-input" :disabled="isEditMode">
                        <option value="ADMIN">User Admin</option>
                        <option value="DONEE">Donee</option>
                        <option value="FUND_RAISER">Fund Raiser</option>
                        <option value="PLATFORM_MANAGER">Platform Manager</option>
                    </select>
                </div>
                <div class="form-item">
                    <label>User Profile Description</label>
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
import { Edit, Lock, Unlock} from '@element-plus/icons-vue'
import { activateUserProfileService, createUserProfileService, getAllUserProfilesService, searchUserProfileService, suspendUserProfileService, updateUserProfileService } from '@/api/userProfile'
import { ref, onMounted } from 'vue'

const profileList = ref([])
const profileSet = ref(new Set())
const isModalVisible = ref(false)
const isEditMode = ref(false)
const form = ref({
    name: '',
    description: ''
})
const filter = ref({
    name: 'all',
    description: '',
    status: 'all',
    order: 'DESC'
})

import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
const formatTime = (time) => {
    return dayjs(time).format("YYYY-MM-DD HH:mm:ss")
}

const searchUserProfiles = async () => {
    const userProfileData = await searchUserProfileService(filter.value.name === 'all' ? null : filter.value.name, filter.value.description.trim() === '' ? null : filter.value.description.trim(), filter.value.status === 'all' ? null : filter.value.status, filter.value.order === 'ASC' ? 'ASC' : "DESC")
    profileList.value = userProfileData;
}

const getAllUserProfiles = async () => {
    const userProfileData = await getAllUserProfilesService()
    profileList.value = userProfileData
    profileList.value.forEach(profile => {
        profileSet.value.add(profile.name)
    })
    console.log(profileList.value);
}

onMounted(() => {
    getAllUserProfiles()
})

const suspendUserProfile = async (userProfile) => {
    if (userProfile.status === 'SUSPENDED') return
    const isSuspended = await suspendUserProfileService(userProfile.name);
    if (isSuspended) {
        ElMessage.success("Suspended successfully")
    } else {
        ElMessage.error("Operation failure")
    }
    getAllUserProfiles()
}

const activateUserProfile = async (userProfile) => {
    if (userProfile.status === 'ACTIVE') return
    const isActivated = await activateUserProfileService(userProfile.name)
    if (isActivated) {
        ElMessage.success("Activated successfully")
    } else {
        ElMessage.error("Operation failure")
    }
    getAllUserProfiles()
}

const openCreator = () => {
    isEditMode.value = false;
    form.value = {
        name: '',
        description: ''
    };
    isModalVisible.value = true;
}
const openEditor = (item) => {
    isEditMode.value = true;
    form.value = { ...item };
    isModalVisible.value = true;
}

const closeModal = () => isModalVisible.value = false

const submitForm = async () => {
    const name = form.value.name.trim();
    const description = form.value.description.trim();

    if (name.length <= 0) return ElMessage.error("Please select user profile name")
    if (description.length <= 0) return ElMessage.error("Please enter user profile description")

    if (isEditMode.value) {
        const isUpdated = await updateUserProfileService(form.value);
        if (isUpdated) {
            ElMessage.success("updated successfully")
        } else {
            ElMessage.error("Operation failure")
        }
    } else {
        if (profileList.value.some(userProfile => userProfile.name === name)) return ElMessage.error(name + " has been created");
        const isCreated = await createUserProfileService(form.value)
        if (isCreated) {
            ElMessage.success("Created successfully")
        } else {
            ElMessage.error("Operation failure")
        }
    }
    closeModal();
    getAllUserProfiles();
}
</script>

<style scoped>

.filterBlockContainer {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.filterContainer {
    display: flex;
    gap: 10px;
}

.radioGroupContainer {
    display: flex;
    flex-direction: column;
    gap: 5px;
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

.profile-view-container {
    padding: 50px 80px;
    display: flex;
    flex-direction: column;
    gap: 40px;
}

.profile-header {
    margin-bottom: 30px;
}

.glass-card {
    background: white;
    border-radius: 16px;
    border: 1px solid #DCDFE6;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
    overflow: hidden;
}

.premium-table {
    width: 100%;
    border-collapse: collapse;
}

.premium-table th {
    background: #fafbfc;
    padding: 15px 20px;
    text-align: left;
    font-size: 11px;
    color: #94a3b8;
    border-bottom: 1px solid #EBEEF5;
}

.premium-table td {
    padding: 18px 20px;
    border-bottom: 1px solid #EBEEF5;
}

.premium-table tr {
    transition: background-color 0.3s ease;
}

.premium-table tr:hover {
    background-color: #F5F7FA;
}

.time-badge {
    display: flex;
    align-items: center;
    gap: 8px;
    background: #f1f5f9;
    padding: 6px 12px;
    border-radius: 8px;
    width: fit-content;
}

.clock-icon {
    font-size: 12px;
}

.u-info-bold {
    color: #475569;
    font-size: 13px;
    font-weight: 700;
}

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

.u-name {
    font-weight: 700;
    color: #111827;
}

.u-info {
    color: #64748b;
    font-size: 13px;
}

.action-row {
    display: flex;
    gap: 10px;
    justify-content: flex-end;
}

.btn-round {
    background: white;
    border: 1px solid #f1f5f9;
    cursor: pointer;
    padding: 5px 8px;
    border-radius: 8px;
}

.prime-fab {
    position: fixed;
    bottom: 40px;
    right: 40px;
    background: #0f172a;
    color: white;
    padding: 12px 25px;
    border-radius: 12px;
    border: none;
    font-weight: 700;
    cursor: pointer;
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.modal-backdrop {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(4px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 100;
}

.modal-window {
    background: white;
    padding: 30px;
    border-radius: 20px;
    width: 360px;
}

.form-item {
    margin-bottom: 20px;
}

.form-item label {
    display: block;
    font-size: 11px;
    color: #3b82f6;
    font-weight: 800;
    margin-bottom: 8px;
}

.custom-input {
    width: 100%;
    padding: 12px;
    border: 1px solid #f1f5f9;
    border-radius: 10px;
    background: #f8fafc;
    box-sizing: border-box;
}

.modal-actions {
    display: flex;
    gap: 10px;
}

.btn-prime {
    flex: 1;
    padding: 12px;
    background: #3b82f6;
    color: white;
    border: none;
    border-radius: 10px;
    font-weight: 700;
    cursor: pointer;
}

.btn-ghost {
    padding: 12px;
    background: #f1f5f9;
    border: none;
    border-radius: 10px;
    color: #64748b;
    cursor: pointer;
}
</style>