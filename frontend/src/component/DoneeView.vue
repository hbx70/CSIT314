<template>
  <div class="doneePage">
    <header class="pageHeader">
      <h1 class="mainTitle">Donation Module</h1>
      <div class="searchContainer">
        <div class="searchBar">
          <span class="material-symbols-outlined">search</span>
          <input v-model="searchQuery" type="text" placeholder="Search for causes..." />
        </div>
      </div>
    </header>

    <main class="mainContent">
      <div class="gridWrapper">
        <div v-for="p in filteredList" :key="p.id" class="projectCard" @click="openDetails(p)">
          <div class="cardTop">
            <span class="categoryBadge">{{ p.category }}</span>
            <div class="statusDot" :class="{ active: p.status === 'Active' }"></div>
          </div>
          <h2 class="projectTitle">{{ p.title }}</h2>
          <div class="fundingRow">
            <span><b>${{ p.current }}</b> / <small>${{ p.goal }}</small></span>
            <span class="percentText">{{ Math.round((p.current/p.goal)*100) }}%</span>
          </div>
          <div class="progressBar">
            <div class="fill" :style="{width: (p.current/p.goal*100)+'%'}"></div>
          </div>
          
          <div class="statsRow">
            <div class="statPill">
              <span class="material-symbols-outlined">visibility</span> {{ p.views || 0 }}
            </div>
            <div class="statPill">
              <span class="material-symbols-outlined">star</span> {{ p.favCount || 0 }}
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
          <p>Your contribution will directly support the {{ selected.category }} program. Join us today to make a difference!</p>
        </div>
        
        <div class="drawerStatsGrid">
          <div class="statItem"><label>VIEWS</label><span>{{ selected.views }}</span></div>
          <div class="statItem"><label>FAVORITES</label><span>{{ selected.favCount }}</span></div>
        </div>

        <div class="drawerActions">
          <button class="donateBtn" @click="donate(selected)">
            <span class="material-symbols-outlined">payments</span> Donate $100
          </button>
          
          <button 
            :class="['saveBtn', { isSaved: isSaved(selected.id) }]" 
            @click="toggleSave(selected.id)"
          >
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
import { ref, computed } from 'vue'

const searchQuery = ref('')
const drawer = ref(false)
const selected = ref(null)
const projects = ref(JSON.parse(localStorage.getItem('fraProjects') || '[]'))
const favs = ref(JSON.parse(localStorage.getItem('fraFavs') || '[]'))

const filteredList = computed(() => projects.value.filter(p => 
  p.status === 'Active' && p.title.toLowerCase().includes(searchQuery.value.toLowerCase())
))

const isSaved = (id) => favs.value.includes(id)

const openDetails = (p) => { 
  selected.value = p; 
  p.views = (p.views || 0) + 1; 
  drawer.value = true; 
  saveData();
}

const toggleSave = (id) => {
  const p = projects.value.find(x => x.id === id);
  if (isSaved(id)) { 
    favs.value = favs.value.filter(f => f !== id); 
    if(p) p.favCount--; 
  } else { 
    favs.value.push(id); 
    if(p) p.favCount++; 
  }
  saveData();
}

const donate = (p) => {
  p.current = Math.min(p.current + 100, p.goal);
  const history = JSON.parse(localStorage.getItem('fraDonations') || '[]');
  history.unshift({ 
    id: Date.now(), 
    title: p.title, 
    amount: 100, 
    date: new Date().toLocaleDateString() 
  });
  localStorage.setItem('fraDonations', JSON.stringify(history));
  saveData();
}

const saveData = () => {
  localStorage.setItem('fraProjects', JSON.stringify(projects.value));
  localStorage.setItem('fraFavs', JSON.stringify(favs.value));
}
</script>

<style scoped>
/* 页面基础样式 */
.doneePage { background: #fcfcfd; min-height: 100vh; }
.pageHeader { background: white; padding: 40px 20px; border-bottom: 1px solid #f1f5f9; text-align: center; }
.mainTitle { font-size: 32px; font-weight: 900; color: #0f172a; margin-bottom: 25px; }

/* 搜索框 */
.searchBar { 
  display: flex; align-items: center; background: white; border: 1px solid #e2e8f0;
  padding: 12px 20px; border-radius: 16px; max-width: 500px; margin: 0 auto;
}
.searchBar input { border: none; outline: none; flex: 1; margin-left: 10px; font-size: 14px; }

/* 网格布局 */
.gridWrapper { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 25px; padding: 40px; }

/* 卡片样式 - 去除了底部按钮空间 */
.projectCard { 
  background: white; border-radius: 32px; padding: 32px; border: 1px solid #f1f5f9;
  box-shadow: 0 10px 25px rgba(0,0,0,0.02); cursor: pointer; transition: 0.3s;
}
.projectCard:hover { transform: translateY(-5px); box-shadow: 0 20px 40px rgba(0,0,0,0.06); }

.cardTop { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.categoryBadge { background: #eff6ff; color: #3b82f6; font-size: 10px; font-weight: 900; padding: 6px 12px; border-radius: 8px; text-transform: uppercase; }
.statusDot { width: 8px; height: 8px; border-radius: 50%; background: #cbd5e1; }
.statusDot.active { background: #10b981; box-shadow: 0 0 8px #10b981; }

.projectTitle { font-size: 24px; font-weight: 700; color: #1e293b; margin-bottom: 20px; }
.fundingRow { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 8px; }
.percentText { color: #3b82f6; font-weight: 800; font-size: 14px; }
.progressBar { height: 8px; background: #f1f5f9; border-radius: 10px; overflow: hidden; margin-bottom: 20px; }
.progressBar .fill { height: 100%; background: #3b82f6; border-radius: 10px; transition: width 0.3s ease; }

.statsRow { display: flex; gap: 10px; }
.statPill { background: #f8fafc; border: 1px solid #f1f5f9; padding: 6px 14px; border-radius: 14px; display: flex; align-items: center; gap: 6px; font-size: 14px; font-weight: 700; color: #64748b; }

/* 抽屉内部样式 */
.drawerHeader h3 { margin-top: 10px; font-size: 24px; font-weight: 800; color: #0f172a; }
.descriptionBox { background: #f8fafc; padding: 24px; border-radius: 20px; color: #64748b; line-height: 1.6; margin: 20px 0; }
.drawerStatsGrid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 30px; }
.statItem { background: white; border: 1px solid #f1f5f9; padding: 15px; border-radius: 18px; text-align: center; }
.statItem label { display: block; font-size: 10px; color: #94a3b8; font-weight: 800; margin-bottom: 5px; }
.statItem span { font-size: 24px; font-weight: 900; color: #1e293b; }

/* 抽屉操作按钮 */
.drawerActions { display: flex; flex-direction: column; gap: 12px; }

.donateBtn { 
  width: 100%; padding: 18px; background: #3b82f6; color: white; border: none; 
  border-radius: 18px; font-weight: 800; cursor: pointer; transition: 0.2s;
  display: flex; align-items: center; justify-content: center; gap: 10px;
}
.donateBtn:hover { background: #2563eb; transform: translateY(-2px); box-shadow: 0 10px 20px rgba(59, 130, 246, 0.2); }

.saveBtn { 
  width: 100%; padding: 18px; background: #f1f5f9; color: #64748b; border: none; 
  border-radius: 18px; font-weight: 800; cursor: pointer; transition: 0.2s;
  display: flex; align-items: center; justify-content: center; gap: 10px;
}
.saveBtn:hover { background: #e2e8f0; }
.saveBtn.isSaved { background: #fff1f2; color: #e11d48; } /* 收藏后的粉红色视觉 */
</style>