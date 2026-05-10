<template>
  <div class="manager-container">
    <header class="header">
      <span class="tag">{{ mode.toUpperCase() }}</span>
      <h1>{{ mode === 'category' ? 'Category Control' : 'Operations Report' }}</h1>
    </header>

    <div v-if="mode === 'category'" class="glass-card">
      <div class="input-stack">
        <div class="input-row">
          <div class="field-group">
            <label>CATEGORY NAME</label>
            <input v-model="newCat" placeholder="e.g. Medical & Health" />
          </div>
          <div class="field-group flex-2">
            <label>DESCRIPTION</label>
            <input v-model="newDesc" placeholder="Briefly describe this category..." @keyup.enter="add" />
          </div>
          <button @click="add" class="add-btn">ADD CATEGORY</button>
        </div>
      </div>

      <div class="table-wrapper">
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
            <tr v-for="(c, index) in categories" :key="index" :class="{ 'is-suspended': c.status === 'Suspended' }">
              <td>
                <div :class="['status-marker', c.status === 'Active' ? 'st-active' : 'st-suspended']"></div>
              </td>
              <td>
                <span class="time-text">{{ c.createdAt }}</span>
              </td>
              <td>
                <span class="cat-name">{{ c.name }}</span>
              </td>
              <td>
                <span class="desc-text">{{ c.description || '-' }}</span>
              </td>
              <td>
                <div class="action-row">
                  <button @click="toggleStatus(c)" :class="['text-btn', c.status === 'Active' ? 'suspend-text' : 'activate-text']">
                    {{ c.status === 'Active' ? 'SUSPEND' : 'ACTIVATE' }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-else class="report-content">
      <div class="date-filter-bar">
        <label>Select Reference Date:</label>
        <input type="date" v-model="selectedDate" class="date-picker" />
      </div>

      <div class="stats-grid">
        <div class="stat-card">
          <label>Total Amount</label>
          <div class="value">${{ stats.total.toLocaleString() }}</div>
        </div>
        <div class="stat-card">
          <label>Transactions</label>
          <div class="value">{{ stats.count }}</div>
        </div>
      </div>

      <div class="table-card">
        <h3>Records for {{ selectedDate }} ({{ mode }})</h3>
        <table>
          <thead>
            <tr><th>Date</th><th>Project</th><th>Category</th><th>Amount</th></tr>
          </thead>
          <tbody>
            <tr v-for="row in filteredHistory" :key="row.id">
              <td>{{ new Date(row.date).toLocaleDateString() }}</td>
              <td>{{ row.projectTitle }}</td>
              <td><span class="cat-tag">{{ row.category }}</span></td>
              <td class="amt">${{ row.amount.toFixed(2) }}</td>
            </tr>
            <tr v-if="filteredHistory.length === 0">
              <td colspan="4" class="empty">No records found for this period.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const mode = computed(() => route.meta.mode)
const allHistory = ref([])

// 初始数据结构包含 description 和 createdAt
const categories = ref([
  { name: 'Medical', description: 'Healthcare and emergency medical support', status: 'Active', createdAt: '2024-05-01 10:30' },
  { name: 'Education', description: 'School supplies and tuition grants', status: 'Active', createdAt: '2024-05-02 14:20' }
])
const newCat = ref('')
const newDesc = ref('')
const selectedDate = ref(new Date().toISOString().substr(0, 10))

onMounted(() => {
  const data = localStorage.getItem('donations_history')
  allHistory.value = data ? JSON.parse(data) : []
  const savedCats = localStorage.getItem('platform_categories')
  if (savedCats) categories.value = JSON.parse(savedCats)
})

const saveCats = () => localStorage.setItem('platform_categories', JSON.stringify(categories.value))

const add = () => { 
  if(newCat.value.trim()){ 
    const now = new Date()
    const timeStr = `${now.getFullYear()}-${(now.getMonth()+1).toString().padStart(2,'0')}-${now.getDate().toString().padStart(2,'0')} ${now.getHours().toString().padStart(2,'0')}:${now.getMinutes().toString().padStart(2,'0')}`
    
    categories.value.unshift({ 
      name: newCat.value.trim(), 
      description: newDesc.value.trim(),
      status: 'Active',
      createdAt: timeStr
    })
    newCat.value = ''
    newDesc.value = ''
    saveCats()
  } 
}

const toggleStatus = (c) => {
  c.status = c.status === 'Active' ? 'Suspended' : 'Active'
  saveCats()
}

const remove = (index) => {
  if (confirm('Are you sure you want to delete this category?')) {
    categories.value.splice(index, 1)
    saveCats()
  }
}

const filteredHistory = computed(() => {
  const startDate = new Date(selectedDate.value)
  startDate.setHours(0,0,0,0)
  const startTime = startDate.getTime()

  return allHistory.value.filter(item => {
    const itemDate = new Date(item.date)
    itemDate.setHours(0,0,0,0)
    const itemTime = itemDate.getTime()

    // 计算差值：item日期 减去 选中日期
    const diffDays = (itemTime - startTime) / 86400000

    if (mode.value === 'daily') {
      // 仅当天
      return itemTime === startTime
    } else if (mode.value === 'weekly') {
      // 从选中日期开始，往后推共 7 天 (包含起始日当天)
      return diffDays >= 0 && diffDays < 7
    } else if (mode.value === 'monthly') {
      // 从选中日期开始，往后推共 30 天 (包含起始日当天)
      return diffDays >= 0 && diffDays < 30
    }
    return false
  }).sort((a,b) => new Date(a.date) - new Date(b.date)) // 报表通常按时间正序排列
})

const stats = computed(() => {
  const total = filteredHistory.value.reduce((s, i) => s + i.amount, 0)
  return { total, count: filteredHistory.value.length }
})
</script>

<style scoped>
.manager-container { padding: 40px; max-width: 1000px; margin: 0 auto; }
.tag { background: #3b82f6; color: white; padding: 4px 10px; border-radius: 8px; font-size: 10px; font-weight: 800; }
.header { margin-bottom: 30px; }

/* Category Control 增强样式 */
.input-stack { background: #f8fafc; padding: 20px; border-radius: 16px; margin-bottom: 30px; border: 1px solid #e2e8f0; }
.input-row { display: flex; gap: 20px; align-items: flex-end; }
.field-group { display: flex; flex-direction: column; gap: 8px; flex: 1; }
.flex-2 { flex: 2; }
.field-group label { font-size: 10px; font-weight: 800; color: #94a3b8; }
.field-group input { background: white; border: 1px solid #cbd5e1; padding: 10px 15px; border-radius: 10px; font-weight: 600; outline: none; }
.field-group input:focus { border-color: #3b82f6; }
.add-btn { background: #0f172a; color: white; border: none; padding: 12px 20px; border-radius: 10px; font-weight: 800; cursor: pointer; font-size: 11px; transition: 0.2s; }
.add-btn:hover { background: #3b82f6; }

.premium-table { width: 100%; border-collapse: collapse; }
.premium-table th { text-align: left; font-size: 10px; color: #94a3b8; text-transform: uppercase; letter-spacing: 1px; padding: 15px 10px; border-bottom: 2px solid #f8fafc; }
.premium-table td { padding: 18px 10px; border-bottom: 1px solid #f8fafc; }

.status-marker { width: 8px; height: 8px; border-radius: 50%; }
.st-active { background: #10b981; }
/* 停用状态使用正红色 */
.st-suspended { background: #ef4444; box-shadow: 0 0 8px rgba(239, 68, 68, 0.4); }

.time-text { font-size: 12px; color: #64748b; font-weight: 500; font-family: monospace; }
.cat-name { font-weight: 700; color: #1e293b; font-size: 15px; }
.desc-text { color: #64748b; font-size: 13px; line-height: 1.4; }

.action-row { display: flex; gap: 15px; justify-content: flex-end; }
.text-btn { background: none; border: none; font-size: 11px; font-weight: 900; cursor: pointer; letter-spacing: 0.5px; padding: 5px; }
.activate-text { color: #3b82f6; }
.suspend-text { color: #64748b; }
.suspend-text:hover { color: #ef4444; }
.delete-text { color: #94a3b8; }
.delete-text:hover { color: #000; text-decoration: underline; }

/* 报表样式保持不变 */
.date-filter-bar { background: #f8fafc; padding: 20px; border-radius: 15px; margin-bottom: 25px; display: flex; align-items: center; gap: 15px; border: 1px solid #e2e8f0; }
.date-filter-bar label { font-size: 13px; font-weight: 800; color: #64748b; }
.date-picker { padding: 10px 15px; border-radius: 10px; border: 1px solid #cbd5e1; outline: none; font-weight: 600; color: #0f172a; }
.stats-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 30px; }
.stat-card { background: #0f172a; color: white; padding: 30px; border-radius: 20px; }
.stat-card label { font-size: 10px; opacity: 0.5; text-transform: uppercase; font-weight: 800; }
.stat-card .value { font-size: 32px; font-weight: 900; margin-top: 10px; color: #3b82f6; }
.glass-card, .table-card { background: white; padding: 30px; border-radius: 24px; border: 1px solid #f1f5f9; box-shadow: 0 4px 15px rgba(0,0,0,0.02); }
table { width: 100%; border-collapse: collapse; }
th { text-align: left; font-size: 11px; color: #94a3b8; padding: 10px; border-bottom: 2px solid #f8fafc; }
td { padding: 15px 10px; border-bottom: 1px solid #f8fafc; font-size: 14px; font-weight: 600; color: #1e293b; }
.amt { color: #059669; font-weight: 800; }
.empty { text-align: center; padding: 50px; color: #94a3b8; font-style: italic; }
</style>