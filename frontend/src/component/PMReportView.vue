<template>

    <el-row>
        <el-col :span="18" class="chart-block-container">
            <el-row>
                <el-col :span="12">
                    <div :ref="chartRefs.donationCount" class="chart"></div>
                </el-col>
                <el-col :span="12">
                    <div :ref="chartRefs.donationAmount" class="chart"></div>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <div :ref="chartRefs.donationAvg" class="chart"></div>
                </el-col>
                <el-col :span="12">
                    <div :ref="chartRefs.topCategories" class="chart"></div>
                </el-col>
            </el-row>
        </el-col>
        <el-col :span="6" class="chart-info-ops-container flex-column">
            <div style="width: 100%;">
                <el-date-picker-panel :border="false" style="width: 100%;" />
            </div>
            <div class="filter-container flex-column">
                <div class="filter-box flex-column">
                    <p>Size</p>
                    <el-slider v-model="filter.size" @input="getReport" size="large" show-input :min="1"
                        :max="filter.range === 'DAILY' ? 14 : filter.range === 'WEEKLY' ? 12 : 12" />
                </div>
                <div class="filter-box flex-column">
                    <p>Range</p>
                    <el-radio-group v-model="filter.range" size="large" fill="#409eff" @change="getReport">
                        <el-radio-button label="Daily" value="DAILY" />
                        <el-radio-button label="Weekly" value="WEEKLY" />
                        <el-radio-button label="Monthly" value="MONTHLY" />
                    </el-radio-group>
                </div>
                <p class="secondaryText">Generate Past {{ filter.size }} {{ filter.range === 'DAILY' ? 'Days' :
                    filter.range === 'WEEKLY' ? 'Weeks' : "Months" }} Report</p>
            </div>
        </el-col>
    </el-row>

</template>

<script setup>
import { getDailyReportService, getMonthlyReportService, getWeeklyReportService } from '@/api/report';
import { onMounted, ref } from 'vue';
import * as echarts from 'echarts'

const report = ref({})
const filter = ref({
    size: 10,
    range: "DAILY"
})

const chartRefs = {
    donationCount: ref(null),
    donationAmount: ref(null),
    donationAvg: ref(null),
    topCategories: ref(null)
}
const charts = {}

const getReport = async () => {
    let reportData = {}
    if (filter.value.range === 'DAILY') {
        reportData = await getDailyReportService(filter.value.size);
    } else if (filter.value.range === 'WEEKLY') {
        reportData = await getWeeklyReportService(filter.value.size)
    } else {
        reportData = await getMonthlyReportService(filter.value.size)
    }
    report.value = reportData
    console.log(reportData);
    updateChart()
}

const chartSetting = (filed, title, xLabel, yLabel, type) => {
    return {
        title: {
            text: title
        },
        tooltip: {
            trigger: 'axis',
            formatter: function (params) {
                return `
                    ${xLabel}: ${params[0].name}<br/>
                    ${yLabel}: ${params[0].value}
                `
            }
        },
        xAxis: {
            data: report.value[filed].labels,
            axisLabel: {
                rotate: 45
            }
        },
        yAxis: {},
        series: [
            {
                type: type,
                data: report.value[filed].values
            }
        ]
    }
}


const pieOption = () => {
    return {
        title: {
            text: 'Donation Top Categories',
            left: 'center'
        },

        tooltip: {
            trigger: 'item'
        },

        legend: {
            orient: 'vertical',
            left: 'left'
        },

        series: [
            {
                name: 'Donations',
                type: 'pie',
                radius: '65%',

                data: (
                    report.value.donationTopCategories?.topDataList || []
                ).map(item => ({
                    name: item.dataName,
                    value: item.count
                })),

                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                },

                label: {
                    formatter: '{b}: {c} ({d}%)'
                }
            }
        ]
    }
}

const initChart = () => {
    Object.keys(chartRefs).forEach(key => {
        charts[key] = echarts.init(chartRefs[key].value)
    })
}

const updateChart = () => {
    charts.donationCount.setOption(
        chartSetting("donationCountTrend", "Donee Donation Count Trend Line Chart", "Date", "Count", "line")
    )
    charts.donationAmount.setOption(
        chartSetting("donationAmountTrend", "Donee Donation Total Amount Trend Line Chart", "Date", "Total Amount", "line")
    )
    charts.donationAvg.setOption(
        chartSetting("donationAvgTrend", "Donee Donation Average Amount Trend Line Chart", "Date", "Average Amount", "line")
    )
    charts.topCategories.setOption(pieOption())
}

onMounted(() => {
    initChart()
    getReport()
})

</script>

<style scoped>
.flex-column {
    display: flex;
    flex-direction: column;
}

.secondaryText {
    font-size: 14px;
    color: #909399;
}

.chart {
    width: 100%;
    height: 400px;
}

.chart-block-container {
    background-color: #fcfcfd;
    min-height: 100vh;
    border-right: 1px solid #DCDFE6;
}

:deep(.el-picker-panel__content) {
    width: auto !important;
}

.chart-info-ops-container {
    gap: 20px;
}

.filter-container {
    padding: 0 15px;
    gap: 10px;
}

.filter-box {
    gap: 5px;
}
</style>