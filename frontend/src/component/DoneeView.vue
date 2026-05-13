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
                                <el-radio-button v-for="(category, index) in existCategories" :key="index" :label="category.name" :value="category.id" />
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

        <el-drawer v-model="drawer" direction="rtl" size="400px">
            <template #header>
                <div class="drawerHeader">
                    <span class="categoryBadge">{{ selected?.category }}</span>
                    <h3>{{ selected?.title }}</h3>
                </div>
            </template>
            <div class="drawerBody" v-if="selected">
                <div class="descriptionBox">
                    <p>Your contribution will directly support the {{ selected.category }} program. Join us today to
                        make a
                        difference!</p>
                </div>

                <div class="drawerStatsGrid">
                    <div class="statItem"><label>VIEWS</label><span>{{ selected.views }}</span></div>
                    <div class="statItem"><label>FAVORITES</label><span>{{ selected.favCount }}</span></div>
                </div>

                <div class="drawerActions">
                    <button class="donateBtn" @click="donate(selected)">
                        <span class="material-symbols-outlined">payments</span> Donate $100
                    </button>

                    <button :class="['saveBtn', { isSaved: isSaved(selected.id) }]" @click="toggleSave(selected.id)">
                        <span class="material-symbols-outlined">
                            {{ isSaved(selected.id) ? 'bookmark_added' : 'bookmark' }}
                        </span>
                        {{ isSaved(selected.id) ? 'Saved to Collection' : 'Save for Later' }}
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

.searchBar {
    display: flex;
    align-items: center;
    background: white;
    border: 1px solid #e2e8f0;
    padding: 12px 20px;
    border-radius: 16px;
    max-width: 500px;
    margin: 0 auto;
}

.searchBar input {
    border: none;
    outline: none;
    flex: 1;
    margin-left: 10px;
    font-size: 14px;
}

/* 网格布局 */
.gridWrapper {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 25px;
    padding: 40px;
}

/* 卡片样式 - 去除了底部按钮空间 */
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

.statsRow {
    display: flex;
    gap: 10px;
}

.statPill {
    background: #f8fafc;
    border: 1px solid #f1f5f9;
    padding: 6px 14px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    font-weight: 700;
    color: #64748b;
}

/* 抽屉内部样式 */
.drawerHeader h3 {
    margin-top: 10px;
    font-size: 24px;
    font-weight: 800;
    color: #0f172a;
}

.descriptionBox {
    background: #f8fafc;
    padding: 24px;
    border-radius: 20px;
    color: #64748b;
    line-height: 1.6;
    margin: 20px 0;
}

.drawerStatsGrid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 15px;
    margin-bottom: 30px;
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


/* 抽屉操作按钮 */
.drawerActions {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.donateBtn {
    width: 100%;
    padding: 18px;
    background: #3b82f6;
    color: white;
    border: none;
    border-radius: 18px;
    font-weight: 800;
    cursor: pointer;
    transition: 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
}

.donateBtn:hover {
    background: #2563eb;
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(59, 130, 246, 0.2);
}

.saveBtn {
    width: 100%;
    padding: 18px;
    background: #f1f5f9;
    color: #64748b;
    border: none;
    border-radius: 18px;
    font-weight: 800;
    cursor: pointer;
    transition: 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
}

.saveBtn:hover {
    background: #e2e8f0;
}

.saveBtn.isSaved {
    background: #fff1f2;
    color: #e11d48;
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
</style>