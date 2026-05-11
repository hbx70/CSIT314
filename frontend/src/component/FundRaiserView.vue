<template>
    <div class="mainContainer">
        <div class="headerBlock">
            <div class="titleGroup">
                <h1>Campaign Dashboard</h1>
                <p>Manage and track your fundraising impact</p>
            </div>
            <button class="actionItem primary" @click="openCreateDrawer">
                <span class="plusIcon">+</span> New Campaign
            </button>
        </div>

        <!-- <div class="filterBlock">
            <div class="searchWrapper">
                <input v-model="searchQuery" placeholder="Search by title..." class="fieldItem" />
            </div>
            <div class="tabsBox">
                <button v-for="s in ['All', 'Active', 'Suspended', 'Completed']" :key="s" @click="currentFilter = s"
                    :class="{ active: currentFilter === s }">
                    {{ s }}
                </button>
            </div>
        </div> -->

        <div class="gridBlock">
            <div v-for="(fra, index) in fundRaisingActivities" :key="index" class="cardBox"
                :class="{ 'is-locked': fra.status === 'SUSPENDED' }">
                <div class="tagRow">
                    <span><el-tag>{{ fra.categoryName }}</el-tag></span>
                    <div :class="['status-marker', fra.status === 'ACTIVE' ? 'st-active' : 'st-locked']"></div>
                </div>
                <h3 class="titleItem">{{ fra.title }}</h3>

                <div class="progressContainer">
                    <div class="infoBox">
                        <strong>${{ fra.currentAmount }} <span>/ ${{ fra.targetAmount }}</span></strong>
                        <span class="pctText">{{ ((fra.currentAmount / fra.targetAmount) * 100).toFixed(0) }}%</span>
                    </div>
                    <div class="trackBox">
                        <div class="barItem"
                            :style="{ width: Math.min((fra.currentAmount / fra.targetAmount) * 100, 100) + '%' }"></div>
                    </div>
                    <div class="statBox">
                        <div class="statItem views">
                            <span class="material-symbols-outlined">
                                visibility
                            </span>

                            <span>{{ fra.viewCount }}</span>
                        </div>

                        <div class="statItem shortlist">
                            <span class="material-symbols-outlined">
                                star
                            </span>
                            <span>{{ fra.shortlistCount }}</span>
                        </div>
                    </div>
                </div>

                <div class="controlBox">
                    <template v-if="fra.status !== 'COMPLETED'">
                        <el-button :icon="Edit" type="primary" @click="openUpdateDrawer(fra)" round plain>Update</el-button>
                        <el-button
                            @click="fra.status === 'ACTIVE' ? suspendFundRaisingActivity(fra) : activateFundRaisingActivity(fra)"
                            :icon="fra.status === 'ACTIVE' ? 'Lock' : 'Unlock'"
                            :type="fra.status === 'ACTIVE' ? 'danger' : 'success'" round plain>{{ fra.status === 'ACTIVE' ? 'Suspend' : 'Activate' }}</el-button>
                    </template>
                </div>
            </div>
        </div>

        <el-drawer v-model="showDrawer" title="Create Fund Raising Activity" direction="ltr" size="500px"
            :before-close="handleClose" class="fra-drawer">
            <el-form :model="fraForm" label-position="top" size="large" :rules="rules" ref="fraFormRef">
                <el-form-item label="Title" prop="title">
                    <el-input v-model="fraForm.title" placeholder="Enter activity title" maxlength="100" show-word-limit
                        clearable />
                </el-form-item>

                <el-form-item label="Description" prop="description">
                    <el-input v-model="fraForm.description" type="textarea" :rows="5"
                        placeholder="Enter activity description" resize="none" clearable />
                </el-form-item>

                <el-form-item label="Target Amount" prop="targetAmount">
                    <el-input-number v-model="fraForm.targetAmount" :min="1" :max="1000000000" :precision="2"
                        controls-position="right" style="width: 100%;" />
                </el-form-item>

                <el-form-item label="Category" prop="categoryId">
                    <el-select v-model="fraForm.categoryId" placeholder="Select category" style="width: 100%;">
                        <el-option v-for="(category, index) in availableCategories" :key="index" :label="category.name"
                            :value="category.id" />
                    </el-select>
                </el-form-item>


                <div style="display: flex; justify-content: flex-end; gap: 12px;">
                    <el-button @click="close">Cancel</el-button>
                    <el-button type="primary" @click="handleSubmitFRA">{{ isEditMode ? 'Update' : 'Create' }}</el-button>
                </div>
            </el-form>
        </el-drawer>
    </div>
</template>

<script setup>
import { Edit, Lock, Unlock } from '@element-plus/icons-vue'
import { searchFRACategoriesService } from '@/api/fraCategory';
import { activateFundRaisingActivityService, createFundRaisingActivityService, getAllFundRaisingActivitiesService, suspendFundRaisingActivityService, updateFundRaisingActivityService } from '@/api/fundRaisingActivity';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, ref } from 'vue'

const showDrawer = ref(false)
const isEditMode = ref(false);
const fraFormRef = ref()
const filter = ref({
    pageNum: 1,
    pageSize: 20
})
const fraForm = ref({
    id: null,
    title: "",
    description: "",
    targetAmount: null,
    categoryId: null
})
const totalFRA = ref(0)
const fundRaisingActivities = ref([]);
const availableCategories = ref([])

const getAllFundRaisingActivities = async () => {
    const fundRaisingActivitiesData = await getAllFundRaisingActivitiesService(filter.value.pageNum, filter.value.pageSize);
    fundRaisingActivities.value = fundRaisingActivitiesData.items
    totalFRA.value = fundRaisingActivitiesData.total
}

const getAllActiveFRACategories = async () => {
    const fraCategoriesData = await searchFRACategoriesService("", "", "ACTIVE", "DESC")
    availableCategories.value = fraCategoriesData
}

onMounted(() => {
    getAllFundRaisingActivities()
    getAllActiveFRACategories()
})

const close = () => {
    showDrawer.value = false
    fraForm.value.id = null
    fraForm.value.title = ''
    fraForm.value.description = ''
    fraForm.value.targetAmount = null
    fraForm.value.categoryId = null
}

const handleClose = (done) => {
    ElMessageBox.confirm('Are you sure you want to close this?')
        .then(() => {
            done()
            fraForm.value.id = null
            fraForm.value.title = ''
            fraForm.value.description = ''
            fraForm.value.targetAmount = null
            fraForm.value.categoryId = null
        })
        .catch(() => {
            // catch error
        })
}

const rules = {
    title: [
        {
            required: true,
            message: "Title cannot be empty",
            trigger: ["blur", "submit"]
        }
    ],

    description: [
        {
            required: true,
            message: "Description cannot be empty",
            trigger: ["blur", "submit"]
        }
    ],

    targetAmount: [
        {
            required: true,
            message: "Target amount cannot be empty",
            trigger: ["blur", "submit"]
        },
        {
            type: "number",
            message: "Target amount must be a number",
            trigger: ["blur", "submit"]
        },
        {
            validator: (rule, value, callback) => {
                if (value < 0) {
                    callback(new Error("Target amount cannot be less than 0.00"))
                } else {
                    callback()
                }
            },
            trigger: ["blur", "submit"]
        }
    ],

    categoryId: [
        {
            required: true,
            message: "Please select a category",
            trigger: ["blur", "submit"]
        }
    ]
}

const openCreateDrawer = () => {
    fraForm.value.id = null
    fraForm.value.title = ''
    fraForm.value.description = ''
    fraForm.value.categoryId = null
    fraForm.value.targetAmount = null
    isEditMode.value = false;
    showDrawer.value = true;
}

const openUpdateDrawer = (fra) => {
    fraForm.value.id = fra.id
    fraForm.value.title = fra.title
    fraForm.value.description = fra.description
    fraForm.value.targetAmount = fra.targetAmount
    fraForm.value.categoryId = availableCategories.value.find(
        c => c.name === fra.categoryName
    )?.id
    isEditMode.value = true
    showDrawer.value = true
}

const handleSubmitFRA = async () => {
    try {
        await fraFormRef.value.validate()
        if (isEditMode.value) {
            const isUpdate = await updateFundRaisingActivityService(fraForm.value);
            if (isUpdate) {
                ElMessage.success("Updated successfully")
                fraForm.value.id = null
                fraForm.value.title = ''
                fraForm.value.description = '',
                fraForm.value.categoryId = null
                fraForm.value.targetAmount = null
                showDrawer.value = false
                getAllFundRaisingActivities()
            } else {
                ElMessage.error("Operation failure")
            }
        } else {
            const isCreated = await createFundRaisingActivityService(fraForm.value)
            if (isCreated) {
                ElMessage.success("Created successfully")
                fraForm.value.id = null
                fraForm.value.title = ''
                fraForm.value.description = '',
                fraForm.value.categoryId = null
                fraForm.value.targetAmount = null
                showDrawer.value = false
                getAllFundRaisingActivities()
            } else {
                ElMessage.error("Operation failure")
            }
        }
    } catch (error) {
        ElMessage.error("Operation failure")
    }
    return
}

const suspendFundRaisingActivity = async (fra) => {
    if (fra.status === 'SUSPENDED') {
        return
    }
    const isSuspended = await suspendFundRaisingActivityService(fra.id);
    if (isSuspended) {
        ElMessage.success("Suspended successfully")
    } else {
        ElMessage.error("Operation failure")
    }
    getAllFundRaisingActivities()
}

const activateFundRaisingActivity = async (fra) => {
    if (fra.status === 'ACTIVE') {
        return
    }
    const isActivated = await activateFundRaisingActivityService(fra.id);
    if (isActivated) {
        ElMessage.success("Activated successfully")
    } else {
        ElMessage.error("Operation failure")
    }
    getAllFundRaisingActivities()
}
</script>

<style scoped>
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

/* 页面基础布局 */
.mainContainer {
    background: #f8fafc;
    padding: 40px;
    min-height: 100vh;
    font-family: 'Inter', sans-serif;
}

.headerBlock {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40px;
    background: white;
    padding: 30px;
    border-radius: 28px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.titleGroup h1 {
    font-size: 28px;
    font-weight: 900;
    color: #0f172a;
    margin: 0;
}

.titleGroup p {
    color: #64748b;
    margin: 5px 0 0 0;
}

.actionItem.primary {
    background: #2563eb;
    color: white;
    border: none;
    padding: 14px 28px;
    border-radius: 16px;
    font-weight: 700;
    cursor: pointer;
    transition: 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.actionItem.primary:hover {
    background: #1d4ed8;
    transform: translateY(-2px);
    box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.4);
}

/* 过滤器 */
.filterBlock {
    display: flex;
    gap: 20px;
    margin-bottom: 35px;
    align-items: center;
}

.searchWrapper {
    flex: 1;
}

.fieldItem {
    width: 100%;
    padding: 14px 20px;
    border-radius: 16px;
    border: 1px solid #e2e8f0;
    outline: none;
    transition: 0.2s;
}

.fieldItem:focus {
    border-color: #3b82f6;
    box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.tabsBox {
    display: flex;
    gap: 8px;
    background: #e2e8f0;
    padding: 6px;
    border-radius: 18px;
}

.tabsBox button {
    padding: 10px 20px;
    border-radius: 14px;
    border: none;
    font-size: 12px;
    font-weight: 700;
    cursor: pointer;
    transition: 0.2s;
    color: #64748b;
    background: transparent;
}

.tabsBox button.active {
    background: white;
    color: #0f172a;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

/* 活动卡片 */
.gridBlock {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 25px;
}

.cardBox {
    background: white;
    padding: 35px;
    border-radius: 36px;
    border: 1px solid #f1f5f9;
    transition: 0.3s;
    position: relative;
}

.cardBox:hover {
    transform: translateY(-8px);
    border-color: #3b82f6;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.05);
}

.tagRow {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
}

.tagItem {
    background: #eff6ff;
    color: #3b82f6;
    padding: 6px 14px;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 800;
    text-transform: uppercase;
}

.statusBadge {
    font-size: 10px;
    font-weight: 900;
    text-transform: uppercase;
    color: #94a3b8;
}

.titleItem {
    font-size: 22px;
    font-weight: 800;
    color: #1e293b;
    margin: 0 0 25px 0;
    line-height: 1.3;
}

/* 进度条 */
.progressContainer {
    margin-bottom: 30px;
}

.infoBox {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 10px;
}

.infoBox strong {
    font-size: 20px;
    color: #0f172a;
}

.infoBox strong span {
    font-size: 14px;
    color: #94a3b8;
    font-weight: 500;
}

.pctText {
    color: #3b82f6;
    font-weight: 900;
}

.trackBox {
    height: 10px;
    background: #f1f5f9;
    border-radius: 20px;
    overflow: hidden;
}

.barItem {
    height: 100%;
    background: linear-gradient(90deg, #3b82f6, #60a5fa);
    border-radius: 20px;
}

.controlBox {
    display: flex;
    justify-content: space-between;
}

.btnItem {
    flex: 1;
    padding: 12px;
    border-radius: 14px;
    border: none;
    font-size: 11px;
    font-weight: 800;
    cursor: pointer;
    text-transform: uppercase;
    transition: 0.2s;
}

.btnItem.ghost {
    background: #f8fafc;
    color: #64748b;
}

.btnItem.danger {
    background: #fff1f2;
    color: #e11d48;
}

/* ========================================= 
   重新设计的弹窗样式 (Modal)
   ========================================= */
.modalOverlay {
    position: fixed;
    inset: 0;
    background: rgba(15, 23, 42, 0.6);
    backdrop-filter: blur(8px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    animation: fadeIn 0.3s ease;
}

.modalContent {
    background: white;
    padding: 45px;
    border-radius: 45px;
    width: 100%;
    max-width: 500px;
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
    animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.modalHeader {
    margin-bottom: 35px;
}

.modalHeader h3 {
    font-size: 26px;
    font-weight: 900;
    color: #0f172a;
    margin: 0 0 10px 0;
}

.modalHeader p {
    color: #64748b;
    font-size: 15px;
    margin: 0;
}

.formBody {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.inputRow {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 15px;
}

.inputGroup {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.inputGroup label {
    font-size: 13px;
    font-weight: 800;
    color: #94a3b8;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

.styledInput {
    width: 100%;
    padding: 15px 20px;
    border-radius: 18px;
    border: 2px solid #f1f5f9;
    background: #f8fafc;
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
    outline: none;
    transition: 0.2s;
}

.styledInput:focus {
    border-color: #3b82f6;
    background: white;
    box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.modalFooter {
    display: flex;
    gap: 15px;
    margin-top: 40px;
}

.footerBtn {
    flex: 1;
    padding: 18px;
    border-radius: 20px;
    border: none;
    font-size: 15px;
    font-weight: 800;
    cursor: pointer;
    transition: 0.2s;
}

.footerBtn.secondary {
    background: #f1f5f9;
    color: #64748b;
}

.footerBtn.primary {
    background: #0f172a;
    color: white;
}

.footerBtn.primary:hover {
    background: #2563eb;
    transform: translateY(-2px);
}

.statBox {
    display: flex;
    gap: 12px;
    margin-top: 14px;
    justify-content: flex-end;
}

.statItem {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 10px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 600;
    backdrop-filter: blur(8px);
}

.statItem .material-symbols-outlined {
    font-size: 18px;
}

.statItem.views {
    background: rgba(245, 247, 250, 0.95);
    color: #4b5563;
    border: 1px solid rgba(209, 213, 219, 0.7);
}

.statItem.shortlist {
    background: rgba(255, 248, 235, 0.95);
    color: #d97706;
    border: 1px solid rgba(251, 191, 36, 0.45);
}

.statItem .material-symbols-outlined {
    font-variation-settings:
        'FILL' 0,
        'wght' 300,
        'GRAD' 200,
        'opsz' 48;
    font-size: 20px;
    transition: all 0.3s ease;
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>