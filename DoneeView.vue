<template>
  <div class="doneeContainer">
    <div class="heroSection">
      <div class="contentWrapper">
        <h1 class="mainTitle">Make an Impact</h1>
        <div class="navTabs">
          <button v-for="mode in modes" :key="mode.id" 
            @click="viewMode = mode.id" 
            :class="{ active: viewMode === mode.id }">
            {{ mode.label }}
            <span v-if="mode.id === 'fav'" class="badge">{{ favList.length }}</span>
          </button>
        </div>
        <div class="searchBox">
          <input v-model="searchQuery" type="text" placeholder="Search for causes you care about..." class="glassInput" />
        </div>
      </div>
    </div>

    <div class="mainContent">
      <transition-group name="list" tag="div" v-if="viewMode !== 'history'" class="gridWrapper">
        <div v-for="p in filteredList" :key="p.id" class="projectCard">
          <div class="cardHeader">
            <span class="categoryTag">{{ p.category }}</span>
            <button @click="toggleFav(p.id)" class="favIconButton" :class="{ isFav: isFav(p.id) }">
              {{ isFav(p.id) ? '❤️' : '🤍' }}
            </button>
          </div>
          
          <h3 class="projectTitle">{{ p.title }}</h3>
          
          <div class="progressSection">
            <div class="progressBar">
              <div class="progressFill" :style="{ width: (p.current / p.goal * 100) + '%' }"></div>
            </div>
            <div class="progressLabel">
              <span class="currentVal">${{ p.current.toLocaleString() }}</span>
              <span class="goalVal">of ${{ p.goal.toLocaleString() }}</span>
            </div>
          </div>

          <button @click="donate(p)" class="donateButton" :disabled="p.status === 'Completed'">
            {{ p.status === 'Completed' ? 'Goal Achieved' : 'Support with $100' }}
          </button>
        </div>
      </transition-group>

      <div v-else class="historyWrapper">
        <div v-for="h in history" :key="h.id" class="historyItem">
          <div class="historyInfo">
            <span class="historyDate">{{ h.date }}</span>
            <p class="historyTitle">{{ h.title }}</p>
          </div>
          <span class="historyAmount">+$100.00</span>
        </div>
        <div v-if="history.length === 0" class="emptyState">No donation history yet.</div>
      </div>
    </div>

    <transition name="toast">
      <div v-if="showToast" class="toastNotification">
        <div class="toastIcon">✨</div>
        <div class="toastText">Thank you! Your donation was successful.</div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const projectList = ref(JSON.parse(localStorage.getItem('fraProjects') || '[]'))
const favList = ref(JSON.parse(localStorage.getItem('fraFavs') || '[]'))
const history = ref(JSON.parse(localStorage.getItem('fraDonations') || '[]'))
const searchQuery = ref('')
const viewMode = ref('all')
const showToast = ref(false)

const modes = [
  { id: 'all', label: 'Explore' },
  { id: 'fav', label: 'Saved' },
  { id: 'history', label: 'History' }
]

const isFav = (id) => favList.value.includes(id)

const toggleFav = (id) => {
  if (isFav(id)) favList.value = favList.value.filter(f => f !== id)
  else favList.value.push(id)
  localStorage.setItem('fraFavs', JSON.stringify(favList.value))
}

const filteredList = computed(() => {
  let list = viewMode.value === 'all' ? projectList.value : projectList.value.filter(p => favList.value.includes(p.id))
  return list.filter(p => p.status === 'Active' && p.title.toLowerCase().includes(searchQuery.value.toLowerCase()))
})

const donate = (p) => {
  p.current = Math.min(p.current + 100, p.goal)
  if (p.current === p.goal) p.status = 'Completed'
  
  history.value.unshift({ 
    id: Date.now(), 
    title: p.title, 
    date: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) 
  })
  
  localStorage.setItem('fraDonations', JSON.stringify(history.value))
  localStorage.setItem('fraProjects', JSON.stringify(projectList.value))
  
  // 触发自定义通知，而不是 alert
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 3000)
}
</script>

<style scoped>
/* 基础容器 */
.doneeContainer {
  min-height: 100vh;
  background: #f8fafc;
  font-family: 'Inter', -apple-system, sans-serif;
  padding-bottom: 100px;
}

/* 顶部 Hero 区域 */
.heroSection {
  background: #ffffff;
  padding: 60px 20px;
  border-bottom: 1px solid #e2e8f0;
  text-align: center;
}

.mainTitle {
  font-size: 2.5rem;
  font-weight: 900;
  color: #0f172a;
  letter-spacing: -1px;
  margin-bottom: 30px;
}

.navTabs {
  display: inline-flex;
  background: #f1f5f9;
  padding: 6px;
  border-radius: 16px;
  margin-bottom: 30px;
}

.navTabs button {
  padding: 10px 24px;
  border: none;
  background: transparent;
  border-radius: 12px;
  font-weight: 700;
  font-size: 14px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 8px;
}

.navTabs button.active {
  background: #ffffff;
  color: #2563eb;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.badge {
  background: #2563eb;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 8px;
}

/* 搜索框 */
.glassInput {
  width: 100%;
  max-width: 500px;
  padding: 16px 24px;
  border-radius: 20px;
  border: 2px solid #f1f5f9;
  background: #ffffff;
  font-size: 16px;
  transition: all 0.3s;
  outline: none;
}

.glassInput:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
}

/* 卡片布局 */
.mainContent {
  max-width: 1100px;
  margin: 40px auto;
  padding: 0 20px;
}

.gridWrapper {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 30px;
}

.projectCard {
  background: #ffffff;
  border-radius: 32px;
  padding: 30px;
  transition: all 0.4s;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
  display: flex;
  flex-direction: column;
}

.projectCard:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.06);
}

.cardHeader {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.categoryTag {
  font-size: 11px;
  font-weight: 800;
  color: #059669;
  background: #ecfdf5;
  padding: 6px 14px;
  border-radius: 12px;
  text-transform: uppercase;
}

.favIconButton {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  transition: transform 0.2s;
}

.favIconButton:hover { transform: scale(1.2); }

.projectTitle {
  font-size: 20px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.4;
  margin-bottom: 25px;
}

/* 进度条 */
.progressSection {
  margin-top: auto;
  margin-bottom: 25px;
}

.progressBar {
  height: 8px;
  background: #f1f5f9;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 12px;
}

.progressFill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #2563eb);
  border-radius: 10px;
  transition: width 1s ease-out;
}

.progressLabel {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.currentVal { font-weight: 800; color: #0f172a; }
.goalVal { color: #94a3b8; }

/* 按钮 */
.donateButton {
  width: 100%;
  padding: 16px;
  border-radius: 18px;
  border: none;
  background: #0f172a;
  color: white;
  font-weight: 800;
  cursor: pointer;
  transition: all 0.3s;
}

.donateButton:hover:not(:disabled) {
  background: #2563eb;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.3);
}

.donateButton:disabled {
  background: #e2e8f0;
  color: #94a3b8;
  cursor: not-allowed;
}

/* 历史记录 */
.historyWrapper {
  background: white;
  border-radius: 32px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.02);
}

.historyItem {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f1f5f9;
}

.historyItem:last-child { border: none; }

.historyDate { font-size: 12px; color: #94a3b8; font-weight: 600; }
.historyTitle { font-weight: 700; color: #1e293b; margin: 4px 0 0 0; }
.historyAmount { color: #059669; font-weight: 800; font-size: 18px; }

/* 通知组件样式 */
.toastNotification {
  position: fixed;
  top: 30px;
  right: 30px;
  background: #0f172a;
  color: white;
  padding: 16px 28px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
  z-index: 1000;
}

.toastText { font-weight: 600; font-size: 14px; }

/* 动画效果 */
.toast-enter-active, .toast-leave-active { transition: all 0.4s ease; }
.toast-enter-from, .toast-leave-to { transform: translateX(100px); opacity: 0; }

.list-enter-active, .list-leave-active { transition: all 0.5s ease; }
.list-enter-from, .list-leave-to { opacity: 0; transform: scale(0.9); }
</style>