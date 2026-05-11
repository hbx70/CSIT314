<template>
    <div class="manager-container">
        <header class="dashboard-header">
            <div class="header-content">
                <div class="brand-section">
                    <div class="brand-orb"></div>
                    <div class="brand-text">
                        <span class="sub-title">Platform Management</span>
                        <h1>FRA Category Management</h1>
                    </div>
                </div>
                <div class="header-stats">
                    <div class="stat-item">
                        <span class="stat-val">{{ categories.length }}</span>
                        <span class="stat-label">Total Categories</span>
                    </div>
                </div>
            </div>
        </header>
        
        <div class="filterBlockContainer">
            <div class="filterContainer">
                <div class="radioGroupContainer">
                    <p>Order</p>
                    <el-radio-group v-model="filter.order" size="large" fill="#409eff" @change="searchFRACategories">
                        <el-radio-button label="Ascending" value="ASC" />
                        <el-radio-button label="Descending" value="DESC" />
                    </el-radio-group>
                </div>
                <div class="radioGroupContainer">
                    <p>Status</p>
                    <el-radio-group v-model="filter.status" size="large" fill="#409eff" @change="searchFRACategories">
                        <el-radio-button label="ALL" value="all" />
                        <el-radio-button label="ACTIVE" value="ACTIVE" />
                        <el-radio-button label="SUSPENDED" value="SUSPENDED" />
                    </el-radio-group>
                </div>
            </div>
            <div class="filterContainer">
                <div class="radioGroupContainer">
                    <p>Categoty Name</p>
                    <el-input v-model="filter.name" size="large" style="width: 250px" placeholder="Search category name" @input="searchFRACategories"/>
                </div>
                <div class="radioGroupContainer">
                    <p>Category Description</p>
                    <el-input v-model="filter.description" size="large" style="width: 400px" placeholder="Search category description" @input="searchFRACategories"/>
                </div>
            </div>
        </div>

        <div class="glass-card">
            <div class="input-stack">
                <div class="input-row">
                    <div class="field-group">
                        <label>CATEGORY NAME</label>
                        <el-input v-model="categoryForm.name" placeholder="e.g. Medical & Health" />
                    </div>
                    <div class="field-group flex-2">
                        <label>DESCRIPTION</label>
                        <el-input v-model="categoryForm.description" placeholder="Briefly describe this category..." />
                    </div>
                    <button @click="createCategory" class="add-btn">ADD CATEGORY</button>
                </div>
            </div>

            <div class="table-wrapper" v-if="categories.length !== 0">
                <table class="premium-table">
                    <thead>
                        <tr>
                            <th width="80">STATUS</th>
                            <th width="150">CREATION TIME</th>
                            <th width="180">CATEGORY NAME</th>
                            <th>DESCRIPTION</th>
                            <th width="180" style="text-align: right">OPERATIONS</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(category, index) in categories" :key="index"
                            :class="{ 'is-locked': category.status === 'SUSPENDED' }">
                            <td>
                                <div
                                    :class="['status-marker', category.status === 'ACTIVE' ? 'st-active' : 'st-locked']">
                                </div>
                            </td>
                            <td>
                                <span class="time-text">{{ formatTime(category.createdAt) }}</span>
                            </td>
                            <td>
                                <span class="cat-name"><el-tag size="large">{{ category.name }}</el-tag></span>
                            </td>
                            <td>
                                <span class="desc-text">{{ category.description || '-' }}</span>
                            </td>
                            <td>
                                <div class="action-row">
                                    <el-button type="success" @click="activateFRACategory(category)" :icon="Unlock"
                                        circle v-if="category.status === 'SUSPENDED'" />
                                    <el-button type="danger" @click="suspendFRACategory(category)" :icon="Lock" circle
                                        v-else />
                                    <el-button type="primary" @click="openEditor(category)" :icon="Edit"
                                        circle />
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <el-empty :image-size="200" v-else />
        </div>
    </div>

    <el-dialog v-model="updateDialogVisible" title="Update FRA Category" width="500px" align-center>

        <div class="field-group" style="margin-bottom:20px">
            <label>CATEGORY NAME</label>
            <el-input v-model="updateCategoryForm.name" placeholder="Category Name" />
        </div>

        <div class="field-group">
            <label>DESCRIPTION</label>
            <el-input v-model="updateCategoryForm.description" placeholder="Description" />
        </div>

        <template #footer>
            <div style="display:flex; justify-content:flex-end; gap:10px">
                <el-button @click="updateDialogVisible = false">
                    Cancel
                </el-button>

                <el-button type="primary" @click="submitUpdateFRACategory">
                    Update
                </el-button>
            </div>
        </template>

    </el-dialog>
</template>

<script setup>

import { activateFRACategoryService, createFRACategoryService, getAllFRACategoriesService, searchFRACategoriesService, suspendFRACategoryService, updateFRACategoryService } from '@/api/fraCategory'
import { Edit, Lock, Unlock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'

const categories = ref([])
const categoryForm = ref({
    name: "",
    description: ""
})
const updateCategoryForm = ref({
    id: null,
    name: "",
    description: ""
})
const updateDialogVisible = ref(false);
const filter = ref({
    order: "DESC",
    status: "all",
    name: "",
    description: ""
})

import dayjs from 'dayjs'
const formatTime = (time) => {
    return dayjs(time).format("YYYY-MM-DD HH:mm:ss")
}

onMounted(() => {
    getAllFRACategories()
})

const searchFRACategories = async () => {
    const fraCategoriesData = await searchFRACategoriesService(filter.value.name.trim(), filter.value.description.trim(), filter.value.status === 'all' ? null : filter.value.status, filter.value.order.trim() === 'ASC' ? 'ASC' : 'DESC')
    categories.value = fraCategoriesData
}

const getAllFRACategories = async () => {
    const fraCategoriesData = await getAllFRACategoriesService();
    categories.value = fraCategoriesData;
}

const createCategory = async () => {
    const name = categoryForm.value.name.trim()
    const description = categoryForm.value.description.trim();
    if (name === '') return ElMessage.error("Please enter category name")
    if (description === '') return ElMessage.error("Please enter category description")
    if (categories.value.some(category => category.name === name.toUpperCase())) return ElMessage.error(name.toUpperCase() + " has been created")

    const isCreated = await createFRACategoryService(categoryForm.value)
    if (isCreated) {
        ElMessage.success("Created successfully")
        categoryForm.value.name = ''
        categoryForm.value.description = ''
        getAllFRACategories()
        return
    } else {
        ElMessage.error("Operation failure")
        return
    }
}

const suspendFRACategory = async (category) => {
    if (category.status === 'SUSPENDED') {
        return
    }
    const isSuspended = await suspendFRACategoryService(category.id)
    if (isSuspended) {
        ElMessage.success("Suspended successfully")
    } else {
        ElMessage.error("Operation failure")
    }
    getAllFRACategories()
}

const activateFRACategory = async (category) => {
    if (category.status === 'ACTIVE') {
        return
    }
    const isActivated = await activateFRACategoryService(category.id)
    if (isActivated) {
        ElMessage.success("Activated successfully")
    } else {
        ElMessage.error("Operation failure")
    }
    getAllFRACategories()
}

const openEditor = (category) => {
    updateCategoryForm.value.id = category.id
    updateCategoryForm.value.name = category.name;
    updateCategoryForm.value.description = category.description
    updateDialogVisible.value = true
}

const submitUpdateFRACategory = async () => {
    const id = updateCategoryForm.value.id;
    const name = updateCategoryForm.value.name.trim().toUpperCase()
    const description = updateCategoryForm.value.description.trim();
    if (name === "") return ElMessage.error("Please enter category name")
    if (description === "") return ElMessage.error("Please enter category description")
    if (categories.value.some(category => category.name === name && category.id != id)) return ElMessage.error(name + "has been token by others")
    const isUpdated = await updateFRACategoryService(updateCategoryForm.value);
    if (isUpdated) {
        ElMessage.success("updated successfully")
        updateDialogVisible.value = false
    } else {
        ElMessage.error("Operation failure")
    }
    getAllFRACategories()
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

.manager-container {
    padding: 50px 80px;
    background: #fcfcfd;
    display: flex;
    flex-direction: column;
    gap: 40px;
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

.tag {
    background: #3b82f6;
    color: white;
    padding: 4px 10px;
    border-radius: 8px;
    font-size: 10px;
    font-weight: 800;
}

.header {
    margin-bottom: 30px;
}

/* Category Control 增强样式 */
.input-stack {
    background: #f8fafc;
    padding: 20px;
    border-radius: 16px;
    margin-bottom: 30px;
    border: 1px solid #e2e8f0;
}

.input-row {
    display: flex;
    gap: 20px;
    align-items: flex-end;
}

.field-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
    flex: 1;
}

.flex-2 {
    flex: 2;
}

.field-group label {
    font-size: 10px;
    font-weight: 800;
    color: #94a3b8;
}

.field-group input {
    background: white;
    border: 1px solid #cbd5e1;
    padding: 10px 15px;
    border-radius: 10px;
    font-weight: 600;
    outline: none;
}

.field-group input:focus {
    border-color: #3b82f6;
}

.add-btn {
    background: #0f172a;
    color: white;
    border: none;
    padding: 12px 20px;
    border-radius: 10px;
    font-weight: 800;
    cursor: pointer;
    font-size: 11px;
    transition: 0.2s;
}

.add-btn:hover {
    background: #3b82f6;
}

.premium-table {
    width: 100%;
    border-collapse: collapse;
}

.premium-table th {
    text-align: left;
    font-size: 10px;
    color: #94a3b8;
    text-transform: uppercase;
    letter-spacing: 1px;
    padding: 15px 10px;
    border-bottom: 2px solid #EBEEF5;
}

.premium-table td {
    padding: 18px 10px;
    border-bottom: 1px solid #EBEEF5;
}

.premium-table tr {
    transition: background-color 0.3s ease;
}

.premium-table tr:hover {
    background-color: #F5F7FA;
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

.time-text {
    font-size: 12px;
    color: #64748b;
    font-weight: 500;
    font-family: monospace;
}

.cat-name {
    font-weight: 700;
    color: #1e293b;
    font-size: 15px;
}

.desc-text {
    color: #64748b;
    font-size: 13px;
    line-height: 1.4;
}

.action-row {
    display: flex;
    gap: 15px;
    justify-content: flex-end;
}

.text-btn {
    background: none;
    border: none;
    font-size: 11px;
    font-weight: 900;
    cursor: pointer;
    letter-spacing: 0.5px;
    padding: 5px;
}

.activate-text {
    color: #3b82f6;
}

.suspend-text {
    color: #64748b;
}

.suspend-text:hover {
    color: #ef4444;
}

.delete-text {
    color: #94a3b8;
}

.delete-text:hover {
    color: #000;
    text-decoration: underline;
}

/* 报表样式保持不变 */
.date-filter-bar {
    background: #f8fafc;
    padding: 20px;
    border-radius: 15px;
    margin-bottom: 25px;
    display: flex;
    align-items: center;
    gap: 15px;
    border: 1px solid #e2e8f0;
}

.date-filter-bar label {
    font-size: 13px;
    font-weight: 800;
    color: #64748b;
}

.date-picker {
    padding: 10px 15px;
    border-radius: 10px;
    border: 1px solid #cbd5e1;
    outline: none;
    font-weight: 600;
    color: #0f172a;
}

.stats-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    margin-bottom: 30px;
}

.stat-card {
    background: #0f172a;
    color: white;
    padding: 30px;
    border-radius: 20px;
}

.stat-card label {
    font-size: 10px;
    opacity: 0.5;
    text-transform: uppercase;
    font-weight: 800;
}

.stat-card .value {
    font-size: 32px;
    font-weight: 900;
    margin-top: 10px;
    color: #3b82f6;
}

.glass-card,
.table-card {
    background: white;
    padding: 30px;
    border-radius: 24px;
    border: 1px solid #f1f5f9;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.02);
}

table {
    width: 100%;
    border-collapse: collapse;
}

th {
    text-align: left;
    font-size: 11px;
    color: #94a3b8;
    padding: 10px;
    border-bottom: 2px solid #f8fafc;
}

td {
    padding: 15px 10px;
    border-bottom: 1px solid #f8fafc;
    font-size: 14px;
    font-weight: 600;
    color: #1e293b;
}

.amt {
    color: #059669;
    font-weight: 800;
}

.empty {
    text-align: center;
    padding: 50px;
    color: #94a3b8;
    font-style: italic;
}
</style>