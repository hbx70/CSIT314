<template>
    <div class="doneePage">
        <header class="pageHeader">
            <h1 class="mainTitle">Donation History</h1>
        </header>

        <main class="mainContent">
            <div class="historyWrapper">
                <div class="filterContainer">

                    <!-- Search Title -->
                    <el-input v-model="filter.title" placeholder="Search Fund Raising Activity Title..." :prefix-icon="Search" size="large"
                        clearable class="searchInput" @input="searchHistoryDonations" />

                    <!-- Status -->
                    <div class="filterBlock">
                        <span class="filterLabel">Status</span>

                        <el-radio-group v-model="filter.status" size="large" @change="searchHistoryDonations">
                            <el-radio-button label="All" value="all" />
                            <el-radio-button label="Success" value="SUCCESS" />
                            <el-radio-button label="Cancelled" value="CANCELLED" />
                        </el-radio-group>
                    </div>

                    <!-- Order By -->
                    <div class="filterBlock">
                        <span class="filterLabel">Order By</span>

                        <el-radio-group v-model="filter.orderBy" size="large" @change="searchHistoryDonations">
                            <el-radio-button label="Latest" value="createdAt" />
                            <el-radio-button label="Amount" value="amount" />
                        </el-radio-group>
                    </div>
                </div>
                <div v-if="historyDonations.length === 0" class="emptyHistory">No donation records found.</div>
                <el-table v-else :data="historyDonations" stripe border style="width: 100%">
                    <el-table-column prop="createdAt" label="Created At" min-width="120">
                        <template #default="scope">
                            {{ formatTime(scope.row.createdAt) }}
                        </template>
                    </el-table-column>

                    <el-table-column prop="title" label="Title" min-width="220" />

                    <el-table-column prop="amount" label="Amount" width="200">
                        <template #default="scope">
                            ${{ formatNumber(scope.row.amount) }}
                        </template>
                    </el-table-column>

                    <el-table-column label="Status" width="130" align="center">
                        <template #default="scope">
                            <el-tag :type="getDonationStatusType(scope.row.status)" size="large">
                                {{ scope.row.status }}
                            </el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column label="Contribution Rate" width="180">
                        <template #default="scope">
                            <span v-if="scope.row.status === 'SUCCESS'">{{ ((scope.row.amount / scope.row.targetAmount) *
                                100).toFixed(2)}}%</span>
                            <span v-else>NA</span>
                        </template>
                    </el-table-column>

                    <!-- Current Progress -->
                    <el-table-column label="Current Progress" min-width="250">
                        <template #default="scope">
                            <el-progress :percentage="calculateProgress(
                                scope.row.currentAmount,
                                scope.row.targetAmount
                            )
                                " :status="scope.row.fraStatus === 'COMPLETED'
                                    ? 'success'
                                    : undefined
                                    " />
                        </template>
                    </el-table-column>

                    <!-- FRA Current Status -->
                    <el-table-column label="FRA Status" width="140" align="center">
                        <template #default="scope">
                            <el-tag :type="getFRAStatusType(scope.row.fraStatus)" size="large">
                                {{ scope.row.fraStatus }}
                            </el-tag>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </main>
    </div>
</template>

<script setup>
import { Search, Remove } from '@element-plus/icons-vue'
import { doneeSearchHistoryDonationsService } from '@/api/donee';
import { onMounted, ref } from 'vue'
const historyDonations = ref([]);
const filter = ref({
    title: '',
    status: 'all',
    orderBy: 'createdAt'
})

const searchHistoryDonations = async () => {
    const donationData = await doneeSearchHistoryDonationsService(filter.value.title.trim(), filter.value.status === 'all' ? null : filter.value.status, filter.value.orderBy === 'createdAt' ? 'createdAt' : 'amount')
    historyDonations.value = donationData
    console.log(historyDonations.value);

}

onMounted(() => {
    searchHistoryDonations()
})

const formatNumber = (num) => {
    return Number(num).toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    })
}

import dayjs from 'dayjs'
const formatTime = (time) => {
    return dayjs(time).format("YYYY-MM-DD HH:mm:ss")
}

const calculateProgress = (current, target) => {
    return Number(((current / target) * 100).toFixed(2))
}

const getDonationStatusType = (status) => {
    switch (status) {
        case 'SUCCESS':
            return 'success'
        default:
            return 'info'
    }
}

const getFRAStatusType = (status) => {
    switch (status) {
        case 'COMPLETED':
            return 'success'
        case 'ACTIVE':
            return 'primary'
        case 'SUSPENDED':
            return 'danger'
        default:
            return 'info'
    }
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

.historyWrapper {
    padding: 30px;
}

.filterContainer {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    margin-bottom: 25px;
    align-items: center;
    background: white;
    padding: 20px;
    border-radius: 18px;
    border: 1px solid #f1f5f9;
}

.searchInput {
    width: 320px;
}

.filterBlock {
    display: flex;
    align-items: center;
    gap: 12px;
}

.filterLabel {
    font-weight: 700;
    color: #475569;
    white-space: nowrap;
}

.emptyHistory {
    text-align: center;
    color: #94a3b8;
    margin-top: 100px;
}
</style>