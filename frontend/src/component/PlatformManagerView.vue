<template>
    <div class="mainContainer">
        <div class="layoutGrid">
            <div class="managerCard">
                <div class="cardHeader">
                    <h2>Category Control</h2>
                    <p>Classify and organize fundraising projects</p>
                </div>
                <div class="addBox">
                    <input v-model="newCategory" placeholder="e.g. Technology" class="inputItem"
                        @keyup.enter="addCategory" />
                    <button @click="addCategory" class="btnItem primary">Add</button>
                </div>
                <div class="categoryList">
                    <transition-group name="list">
                        <div v-for="c in categories" :key="c" class="categoryRow">
                            <span class="catLabel">{{ c }}</span>
                            <button @click="removeCategory(c)" class="deleteBtn">Remove</button>
                        </div>
                    </transition-group>
                </div>
            </div>

            <div class="managerCard dark">
                <div class="cardHeader">
                    <h2>Operations Report</h2>
                    <p>Real-time platform performance tracking</p>
                </div>

                <div class="reportNav">
                    <div class="selectorGroup">
                        <button v-for="mode in ['Day', 'Week', 'Month']" :key="mode" @click="setPeriod(mode)"
                            :class="{ active: reportPeriod === mode }">
                            {{ mode }}
                        </button>
                    </div>
                    <div class="datePickerBox">
                        <label>Reference Date:</label>
                        <input type="date" v-model="selectedDate" class="dateInput" @change="calculateReport" />
                    </div>
                </div>

                <div class="reportDisplay">
                    <div class="statBox">
                        <small>Donations Count</small>
                        <strong>{{ report.count }}</strong>
                    </div>
                    <div class="statBox">
                        <small>Total Volume</small>
                        <strong>${{ report.total.toLocaleString() }}</strong>
                    </div>
                </div>

                <p class="reportHint">Calculated based on your selected {{ reportPeriod.toLowerCase() }} and date.</p>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const categories = ref(JSON.parse(localStorage.getItem('fraCategories') || '["Medical", "Education", "Nature"]'))
const newCategory = ref('')
const reportPeriod = ref('Day') // Day, Week, Month
const selectedDate = ref(new Date().toISOString().substr(0, 10)) // 默认今天
const report = ref({ count: 0, total: 0 })

// 分类管理逻辑
const addCategory = () => {
    if (newCategory.value && !categories.value.includes(newCategory.value)) {
        categories.value.push(newCategory.value)
        localStorage.setItem('fraCategories', JSON.stringify(categories.value))
        newCategory.value = ''
    }
}
const removeCategory = (c) => {
    categories.value = categories.value.filter(cat => cat !== c)
    localStorage.setItem('fraCategories', JSON.stringify(categories.value))
}

// 报告计算逻辑
const setPeriod = (p) => {
    reportPeriod.value = p
    calculateReport()
}

const calculateReport = () => {
    const donations = JSON.parse(localStorage.getItem('fraDonations') || '[]')
    const refDate = new Date(selectedDate.value)

    const filtered = donations.filter(d => {
        const dDate = new Date(d.date)

        if (reportPeriod.value === 'Day') {
            // 同一天
            return dDate.toDateString() === refDate.toDateString()
        } else if (reportPeriod.value === 'Week') {
            // 往前推 7 天
            const diffTime = refDate - dDate
            const diffDays = diffTime / (1000 * 60 * 60 * 24)
            return diffDays >= 0 && diffDays < 7
        } else if (reportPeriod.value === 'Month') {
            // 往前推 30 天
            const diffTime = refDate - dDate
            const diffDays = diffTime / (1000 * 60 * 60 * 24)
            return diffDays >= 0 && diffDays < 30
        }
        return false
    })

    report.value = {
        count: filtered.length,
        total: filtered.length * 100 // 假设每次捐赠是 $100，实际可读取 d.amount
    }
}

onMounted(() => calculateReport())
</script>

<style scoped>
.mainContainer {
    padding: 60px 40px;
    background: #f8fafc;
    min-height: 100vh;
    font-family: 'Inter', sans-serif;
}

.layoutGrid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
    gap: 30px;
    max-width: 1200px;
    margin: 0 auto;
}

.managerCard {
    background: white;
    padding: 40px;
    border-radius: 40px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
    border: 1px solid #f1f5f9;
}

.managerCard.dark {
    background: #0f172a;
    color: white;
    border: none;
}

.cardHeader {
    margin-bottom: 30px;
}

.cardHeader h2 {
    font-size: 24px;
    font-weight: 900;
    color: inherit;
    margin: 0;
}

.cardHeader p {
    color: #64748b;
    font-size: 14px;
    margin-top: 5px;
}

/* 分类管理样式 */
.addBox {
    display: flex;
    gap: 10px;
    margin-bottom: 25px;
}

.inputItem {
    flex: 1;
    padding: 14px 20px;
    border-radius: 16px;
    border: 2px solid #f1f5f9;
    outline: none;
    transition: 0.2s;
}

.inputItem:focus {
    border-color: #3b82f6;
}

.btnItem.primary {
    background: #0f172a;
    color: white;
    border: none;
    padding: 0 25px;
    border-radius: 16px;
    font-weight: 700;
    cursor: pointer;
}

.categoryRow {
    display: flex;
    justify-content: space-between;
    padding: 15px 0;
    border-bottom: 1px solid #f1f5f9;
}

.catLabel {
    font-weight: 600;
    color: #1e293b;
}

.deleteBtn {
    color: #ef4444;
    background: none;
    border: none;
    font-weight: 800;
    font-size: 11px;
    cursor: pointer;
    opacity: 0.6;
}

.deleteBtn:hover {
    opacity: 1;
}

/* 报告部分样式 */
.reportNav {
    margin: 30px 0;
}

.selectorGroup {
    display: flex;
    gap: 8px;
    background: rgba(255, 255, 255, 0.05);
    padding: 5px;
    border-radius: 14px;
    margin-bottom: 20px;
}

.selectorGroup button {
    flex: 1;
    border: none;
    background: transparent;
    color: #94a3b8;
    padding: 10px;
    border-radius: 10px;
    font-weight: 700;
    cursor: pointer;
    transition: 0.3s;
}

.selectorGroup button.active {
    background: white;
    color: #0f172a;
}

.datePickerBox {
    display: flex;
    align-items: center;
    gap: 15px;
}

.datePickerBox label {
    font-size: 12px;
    font-weight: 700;
    color: #94a3b8;
    text-transform: uppercase;
}

.dateInput {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: white;
    padding: 10px 15px;
    border-radius: 12px;
    cursor: pointer;
}

.reportDisplay {
    display: flex;
    gap: 20px;
    margin-top: 30px;
}

.statBox {
    flex: 1;
    background: rgba(255, 255, 255, 0.05);
    padding: 30px;
    border-radius: 30px;
    text-align: center;
    border: 1px solid rgba(255, 255, 255, 0.05);
}

.statBox small {
    color: #94a3b8;
    font-weight: 700;
    text-transform: uppercase;
    font-size: 10px;
    letter-spacing: 1px;
}

.statBox strong {
    font-size: 32px;
    font-weight: 900;
    display: block;
    margin-top: 10px;
    color: #3b82f6;
}

.reportHint {
    font-size: 12px;
    color: #475569;
    text-align: center;
    margin-top: 25px;
    font-style: italic;
}

/* 动画 */
.list-enter-active,
.list-leave-active {
    transition: all 0.3s ease;
}

.list-enter-from,
.list-leave-to {
    opacity: 0;
    transform: translateX(-20px);
}
</style>