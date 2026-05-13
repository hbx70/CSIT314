<template>
    <div class="doneePage">
        <header class="pageHeader">
            <h1 class="mainTitle">Donation Module</h1>
            <div class="searchContainer">
                <div class="filterBlock">
                    <p class="title">My Fund Raising Activities</p>
                    <el-input v-model="filter.title" placeholder="Search Fund Raising Activity Title ..."
                        :prefix-icon="Search" size="large" @input="getAllFundRaisingActivities" />
                    <div class="filterContainer">
                        <div class="radioGroupContainer">
                            <p>Order</p>
                            <el-radio-group v-model="filter.orderBy" size="large" fill="#409eff"
                                @change="getAllFundRaisingActivities">
                                <el-radio-button label="Date" value="createdAt" />
                                <el-radio-button label="Views" value="viewCount" />
                                <el-radio-button label="shortlists" value="shortlistCount" />
                            </el-radio-group>
                        </div>
                        <div class="radioGroupContainer">
                            <p>Category</p>
                            <el-radio-group v-model="filter.categotyId" size="large" fill="#409eff"
                                @change="getAllFundRaisingActivities">
                                <el-radio-button label="ALL" value="all" />
                                <el-radio-button v-for="(category, index) in existCategories" :key="index"
                                    :label="category.name" :value="category.id" />
                            </el-radio-group>
                        </div>
                    </div>
                </div>
            </div>
        </header>

        <main class="mainContent">
            <div class="gridWrapper">
                <div v-for="(fra, index) in fundRaisingActivities" :key="index" class="projectCard"
                    @click="openDetails(fra)">
                    <div class="cardTop">
                        <span class="categoryBadge">{{ fra.categoryName }}</span>
                        <div class="statusDot" :class="{ active: fra.status === 'ACTIVE' }"></div>
                    </div>
                    <h2 class="projectTitle">{{ fra.title }}</h2>
                    <div class="fundingRow">
                        <span><b>${{ formatNumber(fra.currentAmount) }}</b> / <small>${{ formatNumber(fra.targetAmount)
                                }}</small></span>
                        <span class="percentText">{{ Math.round((fra.currentAmount / fra.targetAmount) * 100) }}%</span>
                    </div>
                    <div class="progressBar">
                        <div class="fill" :style="{ width: (fra.currentAmount / fra.targetAmount * 100) + '%' }"></div>
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
            </div>
        </main>

        <el-drawer v-model="drawer" direction="rtl" size="42%" :with-header="false" class="fra-detail-drawer">
            <div class="drawer-container" v-if="selected">
                <div class="drawer-header">
                    <div>
                        <el-tag size="large">{{ selected.categoryName }}</el-tag>
                        <h1>{{ selected.title }}</h1>
                        <p class="drawer-description">
                            {{ selected.description }}
                        </p>
                    </div>
                </div>

                <div class="amount-section">
                    <div class="amount-card">
                        <span>Current Amount</span>
                        <h2>${{ formatNumber(selected.currentAmount) }}</h2>
                    </div>
                    <div class="amount-card">
                        <span>Target Amount</span>
                        <h2>${{ formatNumber(selected.targetAmount) }}</h2>
                    </div>
                </div>

                <div class="progress-section">
                    <div class="progress-top">
                        <span>Fund Raising Progress</span>
                        <span>{{ Math.min(Math.round((selected.currentAmount / selected.targetAmount) * 100), 100)
                        }}%</span>
                    </div>
                    <el-progress
                        :percentage="Math.min(Math.round((selected.currentAmount / selected.targetAmount) * 100), 100)"
                        :stroke-width="12" :show-text="false" status="success" />
                </div>

                <div class="stats-grid">
                    <div class="stat-box">
                        <span class="material-symbols-outlined">visibility</span>
                        <p>{{ selected.viewCount }} Views</p>
                    </div>
                    <div class="stat-box">
                        <span class="material-symbols-outlined">star</span>
                        <p>{{ selected.shortlistCount }} Saved</p>
                    </div>
                </div>

                <div class="drawerActions" style="margin-top: 40px;">
                    <button class="donateBtn" @click="donate(selected)">
                        <span class="material-symbols-outlined">payments</span> Donate $100
                    </button>
                </div>
            </div>
        </el-drawer>
    </div>
</template>

<script setup>
import { Search } from '@element-plus/icons-vue'
import { doneeSearchFundRaisingActiviesService } from '@/api/donee'
import { ref, onMounted } from 'vue'

const drawer = ref(false)
const selected = ref(null)
const donationAmount = ref(0) // 初始为0，由用户输入

const filter = ref({
    pageNum: 1,
    pageSize: 20,
    title: '',
    categoryId: 'all',
    orderBy: 'createdAt'
})
const fundRaisingActivities = ref([])
const totalFRA = ref(0);
const existCategories = ref([]);

const getAllFundRaisingActivities = async () => {
    const fundRaisingActivitiesData = await doneeSearchFundRaisingActiviesService(filter.value.pageNum, filter.value.pageSize, filter.value.title.trim(), filter.value.categoryId === 'all' ? null : filter.value.category, filter.value.orderBy)
    fundRaisingActivities.value = fundRaisingActivitiesData.items;
    totalFRA.value = fundRaisingActivitiesData.total;
    fundRaisingActivitiesData.items.forEach(fra => {
        if (existCategories.value.some(category => category.id == fra.categoryId)) return
        const categoryData = {
            id: fra.categoryId,
            name: fra.categoryName
        }
        existCategories.value.push(categoryData)
    });
}

onMounted(() => {
    getAllFundRaisingActivities()
})

const openDetails = (fra) => {
    selected.value = fra;
    donationAmount.value = 0; // 每次打开重置金额
    drawer.value = true;
}

const formatNumber = (num) => {
    return Number(num).toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    })
}
</script>

<style scoped>
/* 保持原有页面样式不变 */
.doneePage {
    background: #fcfcfd;
    min-height: 100vh;
}

.pageHeader {
    background: white;
    padding: 40px 20px;
    border-bottom: 1px solid #f1f5f9;
    text-align: center;
}

.mainTitle {
    font-size: 32px;
    font-weight: 900;
    color: #0f172a;
    margin-bottom: 25px;
}

.gridWrapper {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 25px;
    padding: 40px;
}

.projectCard {
    background: white;
    border-radius: 32px;
    padding: 32px;
    border: 1px solid #f1f5f9;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.02);
    cursor: pointer;
    transition: 0.3s;
}

.projectCard:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06);
}

.cardTop {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.categoryBadge {
    background: #eff6ff;
    color: #3b82f6;
    font-size: 10px;
    font-weight: 900;
    padding: 6px 12px;
    border-radius: 8px;
    text-transform: uppercase;
}

.statusDot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #cbd5e1;
}

.statusDot.active {
    background: #10b981;
    box-shadow: 0 0 8px #10b981;
}

.projectTitle {
    font-size: 24px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 20px;
}

.fundingRow {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    margin-bottom: 8px;
}

.percentText {
    color: #3b82f6;
    font-weight: 800;
    font-size: 14px;
}

.progressBar {
    height: 8px;
    background: #f1f5f9;
    border-radius: 10px;
    overflow: hidden;
    margin-bottom: 20px;
}

.progressBar .fill {
    height: 100%;
    background: #3b82f6;
    border-radius: 10px;
    transition: width 0.3s ease;
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

.filterContainer {
    display: flex;
    gap: 10px;
}

.radioGroupContainer {
    display: flex;
    flex-direction: column;
    gap: 5px;
}

/* 重点修改：Fundraiser 风格的抽屉内部样式 */
.fra-detail-drawer :deep(.el-drawer) {
    border-radius: 28px 0 0 28px;
    overflow: hidden;
}

.drawer-container {
    padding: 40px;
    background: #f8fafc;
    min-height: 100%;
    text-align: left;
}

.drawer-header h1 {
    margin: 15px 0;
    font-size: 32px;
    font-weight: 800;
    color: #111827;
}

.drawer-description {
    font-size: 16px;
    line-height: 1.8;
    color: #6b7280;
}

.amount-section {
    margin-top: 30px;
    display: grid;
    grid-template-columns: 1fr 1fr;
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
    font-weight: 600;
}

.amount-card h2 {
    margin-top: 14px;
    font-size: 30px;
    color: #111827;
    font-weight: 800;
}

.progress-section {
    margin-top: 30px;
    background: white;
    padding: 28px;
    border-radius: 22px;
    box-shadow: 0 4px 18px rgba(0, 0, 0, 0.04);
}

.progress-top {
    display: flex;
    justify-content: space-between;
    margin-bottom: 18px;
    font-weight: 700;
    color: #111827;
}

.stats-grid {
    margin-top: 25px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
}

.stat-box {
    background: white;
    padding: 18px;
    border-radius: 20px;
    box-shadow: 0 4px 18px rgba(0, 0, 0, 0.04);
    display: flex;
    align-items: center;
    gap: 12px;
}

.stat-box p {
    margin: 0;
    font-weight: 700;
    color: #111827;
}

.stat-box .material-symbols-outlined {
    color: #94a3b8;
}

/* 底部操作按钮 */
.drawerActions {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.donateBtn {
    width: 100%;
    padding: 20px;
    background: #3b82f6;
    color: white;
    border: none;
    border-radius: 20px;
    font-weight: 800;
    cursor: pointer;
    transition: 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    font-size: 16px;
}

.donateBtn:hover {
    background: #2563eb;
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(59, 130, 246, 0.2);
}

.saveBtn {
    width: 100%;
    padding: 20px;
    background: #f1f5f9;
    color: #64748b;
    border: none;
    border-radius: 20px;
    font-weight: 800;
    cursor: pointer;
    transition: 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    font-size: 16px;
}

.saveBtn:hover {
    background: #e2e8f0;
}

.saveBtn.isSaved {
    background: #fff1f2;
    color: #e11d48;
}
</style>