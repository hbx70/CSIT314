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

        <div class="filterBlock">
            <p class="title">My Fund Raising Activities</p>
            <el-input v-model="filter.title" placeholder="Search Fund Raising Activity Title ..." :prefix-icon="Search"
                size="large" @input="searchFundRaisingActivities" />
            <div class="filterContainer">
                <div class="radioGroupContainer">
                    <p>Order</p>
                    <el-radio-group v-model="filter.order" size="large" fill="#409eff"
                        @change="searchFundRaisingActivities">
                        <el-radio-button label="Ascending" value="ASC" />
                        <el-radio-button label="Descending" value="DESC" />
                    </el-radio-group>
                </div>
                <div class="radioGroupContainer">
                    <p>Status</p>
                    <el-radio-group v-model="filter.status" size="large" fill="#409eff"
                        @change="searchFundRaisingActivities">
                        <el-radio-button label="ALL" value="all" />
                        <el-radio-button label="ACTIVE" value="ACTIVE" />
                        <el-radio-button label="SUSPENDED" value="SUSPENDED" />
                        <el-radio-button label="COMPLETED" value="COMPLETED" />
                    </el-radio-group>
                </div>
                <div class="radioGroupContainer">
                    <p>Category</p>
                    <el-radio-group v-model="filter.categotyId" size="large" fill="#409eff"
                        @change="searchFundRaisingActivities">
                        <el-radio-button label="ALL" value="all" />
                        <el-radio-button v-for="(category, index) in existCategories" :key="index"
                            :label="category.name" :value="category.id" />
                    </el-radio-group>
                </div>
            </div>
        </div>

        <div v-if="fundRaisingActivities.length > 0">
            <div class="gridBlock">
                <div v-for="(fra, index) in fundRaisingActivities" :key="index" class="cardBox"
                    :class="{ 'is-locked': fra.status === 'SUSPENDED' }" @click="showFRADetail(fra)">
                    <div class="tagRow">
                        <span><el-tag>{{ fra.categoryName }}</el-tag></span>
                        <div :class="['status-marker', fra.status === 'ACTIVE' ? 'st-active' : 'st-locked']"></div>
                    </div>
                    <h3 class="titleItem">{{ fra.title }}</h3>

                    <div class="progressContainer">
                        <div class="infoBox">
                            <strong>${{ formatNumber(fra.currentAmount) }} <span>/ ${{ formatNumber(fra.targetAmount) }}</span></strong>
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
                            <el-button @click.stop :icon="Edit" type="primary" @click="openUpdateDrawer(fra)" round
                                plain>Update</el-button>
                            <el-button
                                @click.stop
                                @click="fra.status === 'ACTIVE' ? suspendFundRaisingActivity(fra) : activateFundRaisingActivity(fra)"
                                :icon="fra.status === 'ACTIVE' ? 'Lock' : 'Unlock'"
                                :type="fra.status === 'ACTIVE' ? 'danger' : 'success'" round plain>{{ fra.status ===
                                    'ACTIVE' ? 'Suspend' : 'Activate' }}</el-button>
                        </template>
                    </div>
                </div>
            </div>
            <el-pagination
                style="display: flex; justify-content: flex-end; margin-top: 40px;"
                v-model:current-page="filter.pageNum"
                v-model:page-size="filter.pageSize"
                :page-sizes="[20, 50, 100, 200]"
                :background="true"
                layout="total, sizes, prev, pager, next, jumper"
                :total="totalFRA"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>
        <el-empty :image-size="200" v-else />

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
                        controls-position="right" style="width: 100%;">
                        <template #prefix>
                            <span>$</span>
                        </template>
                    </el-input-number>
                </el-form-item>

                <el-form-item label="Category" prop="categoryId">
                    <el-select v-model="fraForm.categoryId" placeholder="Select category" style="width: 100%;">
                        <el-option v-for="(category, index) in availableCategories" :key="index" :label="category.name"
                            :value="category.id" />
                    </el-select>
                </el-form-item>


                <div style="display: flex; justify-content: flex-end; gap: 12px;">
                    <el-button @click="close">Cancel</el-button>
                    <el-button type="primary" @click="handleSubmitFRA">{{ isEditMode ? 'Update' : 'Create'
                        }}</el-button>
                </div>
            </el-form>
        </el-drawer>

        <el-drawer v-model="detailFRAdrawer" size="42%" :with-header="false" class="fra-detail-drawer">
            <div class="drawer-container">

                <div class="drawer-header">
                    <div>
                        <el-tag size="large">{{ currentFra.categoryName }}</el-tag>

                        <h1>{{ currentFra.title }}</h1>

                        <p class="drawer-description">
                            {{ currentFra.description }}
                        </p>
                    </div>

                    <el-tag :type="currentFra.status === 'ACTIVE' ? 'success' : currentFra.status === 'SUSPENDED' ? 'danger' : 'info'" size="large">
                        {{ currentFra.status }}
                    </el-tag>
                </div>

                <div class="amount-section">
                    <div class="amount-card">
                        <span>Current Amount</span>
                        <h2>
                            ${{ formatNumber(currentFra.currentAmount) }}
                        </h2>
                    </div>

                    <div class="amount-card">
                        <span>Target Amount</span>
                        <h2>
                            ${{ formatNumber(currentFra.targetAmount) }}
                        </h2>
                    </div>
                </div>

                <div class="progress-section">
                    <div class="progress-top">
                        <span>Fund Raising Progress</span>
                        <span>
                            {{ calculateProgress() }}%
                        </span>
                    </div>

                    <el-progress :percentage="calculateProgress()" :stroke-width="12" :show-text="false"
                        status="success" />
                </div>

                <div class="stats-grid">
                    <div class="stat-box">
                        <span class="material-symbols-outlined">visibility</span>
                        <p>{{ currentFra.viewCount }}</p>
                    </div>

                    <div class="stat-box">
                        <span class="material-symbols-outlined">star</span>
                        <p>{{ currentFra.shortlistCount }}</p>
                    </div>
                </div>

                <!-- Footer Info -->
                <div class="footer-info">

                    <div class="info-item">
                        <span class="spanText">Created At</span>
                        <p style="display: flex; align-items: center; gap: 5px;"><span
                                class="material-symbols-outlined">calendar_month</span>{{
                                    formatDate(currentFra.createdAt) }}
                        </p>
                    </div>

                    <div class="info-item">
                        <span class="spanText">Category Status</span>
                        <p style="display: flex; align-items: center; gap: 10px;">
                            <div :class="['status-marker', currentFra.categoryStatus === 'ACTIVE' ? 'st-active' : 'st-locked']"></div>
                            <el-tag :type="currentFra.categoryStatus === 'ACTIVE' ? 'success' : 'danger'">
                                {{ currentFra.status }}
                            </el-tag>
                        </p>
                    </div>
                </div>

            </div>
        </el-drawer>

    </div>
</template>

<script setup>
import { Edit, Lock, Unlock, Search } from '@element-plus/icons-vue'
import { searchFRACategoriesService } from '@/api/fraCategory';
import { activateFundRaisingActivityService, createFundRaisingActivityService, getAllFundRaisingActivitiesService, searchFundRaisingActivitiesService, suspendFundRaisingActivityService, updateFundRaisingActivityService } from '@/api/fundRaisingActivity';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, ref } from 'vue'

const currentFra = ref({})
const detailFRAdrawer = ref(false)
const showDrawer = ref(false)
const isEditMode = ref(false);
const fraFormRef = ref()
const filter = ref({
    pageNum: 1,
    pageSize: 20,
    title: '',
    status: 'all',
    categotyId: 'all',
    order: "DESC"
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
const existCategories = ref([]);

const searchFundRaisingActivities = async () => {
    const fundRaisingActivitiesData = await searchFundRaisingActivitiesService(filter.value.pageNum, filter.value.pageSize, filter.value.title.trim(), filter.value.status === 'all' ? null : filter.value.status, filter.value.categotyId === 'all' ? null : filter.value.categotyId, filter.value.order === 'ASC' ? 'ASC' : 'DESC');
    fundRaisingActivities.value = fundRaisingActivitiesData.items
    totalFRA.value = fundRaisingActivitiesData.total
}

const getAllFundRaisingActivities = async () => {
    const fundRaisingActivitiesData = await getAllFundRaisingActivitiesService(filter.value.pageNum, filter.value.pageSize);
    fundRaisingActivities.value = fundRaisingActivitiesData.items
    totalFRA.value = fundRaisingActivitiesData.total
    fundRaisingActivitiesData.items.forEach(fra => {
        if (existCategories.value.some(category => category.id == fra.categoryId)) return
        const categoryData = {
            id: fra.categoryId,
            name: fra.categoryName
        }
        existCategories.value.push(categoryData)
    });
    console.log(fundRaisingActivitiesData);

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

const showFRADetail = (fra) => {
    currentFra.value = fra
    detailFRAdrawer.value = true;
}

const handleSizeChange = (size) => {
    filter.value.pageSize = size
    searchFundRaisingActivities()
}

const handleCurrentChange = (pageNum) => {
    filter.value.pageNum = pageNum
    searchFundRaisingActivities()
}










const formatNumber = (num) => {
    return Number(num).toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    })
}

const calculateProgress = () => {
    if (!currentFra.value.targetAmount) return 0
    return Math.min(Math.round((currentFra.value.currentAmount / currentFra.value.targetAmount) * 100), 100)
}

const formatDate = (date) => {
    return new Date(date).toLocaleString()
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

.filterBlock {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-bottom: 35px;
    align-items: center;
    padding: 0 60px;
}

.filterBlock .title {
    font-size: 20px;
    font-weight: 500;
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


.filterContainer {
    display: flex;
    gap: 10px;
}

.radioGroupContainer {
    display: flex;
    flex-direction: column;
    gap: 5px;
}















.fra-card {
    background: white;
    border-radius: 24px;
    padding: 24px;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #ebeef5;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.fra-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
}

.fra-card-content {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 20px;
}

.fra-card h2 {
    margin: 0;
    font-size: 24px;
    color: #111827;
}

.fra-card p {
    margin-top: 10px;
    color: #6b7280;
    line-height: 1.6;
}

/* Drawer */
.fra-detail-drawer :deep(.el-drawer) {
    border-radius: 28px 0 0 28px;
    overflow: hidden;
}

.drawer-container {
    padding: 40px;
    background: #f8fafc;
    min-height: 100%;
}

.drawer-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 20px;
}

.drawer-category {
    display: inline-block;
    padding: 6px 14px;
    background: #111827;
    color: white;
    border-radius: 999px;
    font-size: 12px;
    letter-spacing: 1px;
    margin-bottom: 18px;
}

.drawer-header h1 {
    margin: 0;
    font-size: 40px;
    font-weight: 700;
    color: #111827;
}

.drawer-description {
    margin-top: 18px;
    font-size: 16px;
    line-height: 1.8;
    color: #6b7280;
    max-width: 700px;
}

/* Amount */
.amount-section {
    margin-top: 40px;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
}

.amount-card {
    background: white;
    border-radius: 22px;
    padding: 28px;
    box-shadow: 0 4px 18px rgba(0, 0, 0, 0.04);
}

.amount-card span {
    color: #6b7280;
    font-size: 14px;
}

.amount-card h2 {
    margin-top: 14px;
    font-size: 34px;
    color: #111827;
}

/* Progress */
.progress-section {
    margin-top: 40px;
    background: white;
    padding: 28px;
    border-radius: 22px;
    box-shadow: 0 4px 18px rgba(0, 0, 0, 0.04);
}

.progress-top {
    display: flex;
    justify-content: space-between;
    margin-bottom: 18px;
    font-weight: 600;
    color: #111827;
}

/* Stats */
.stats-grid {
    margin-top: 40px;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
}

.stat-box {
    background: white;
    color: #111827;
    padding: 10px;
    border-radius: 20px;
    box-shadow: 0 4px 18px rgba(0, 0, 0, 0.04);
    display: flex;
    align-items: center;
    gap: 10px;
}

.stat-box .material-symbols-outlined {
    color: #94a3b8;
}

/* Footer */
.footer-info {
    margin-top: 40px;
    background: white;
    border-radius: 22px;
    padding: 28px;
    display: flex;
    flex-direction: column;
    gap: 22px;
    box-shadow: 0 4px 18px rgba(0, 0, 0, 0.04);
}

.info-item .spanText {
    font-size: 13px;
    color: #9ca3af;
}

.info-item p {
    margin-top: 6px;
    font-size: 16px;
    color: #111827;
    font-weight: 600;
}

.info-item .material-symbols-outlined {
    font-variation-settings:
        'FILL' 0,
        'wght' 300,
        'GRAD' 200,
        'opsz' 48;
    font-size: 22px;
    color: #9ca3af;
    transition: all 0.3s ease;
}

:deep(.fra-detail-drawer .el-drawer__body) {
    padding: 0;
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