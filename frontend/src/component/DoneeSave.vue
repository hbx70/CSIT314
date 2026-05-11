<template>
  <div class="doneePage">
    <header class="pageHeader">
      <h1 class="mainTitle">Saved Projects</h1>
    </header>

    <main class="mainContent">
      <div v-if="savedList.length === 0" class="emptyState">
        <span class="material-symbols-outlined">bookmark_border</span>
        <p>You haven't saved any projects yet.</p>
      </div>
      <div class="gridWrapper" v-else>
        <div v-for="p in savedList" :key="p.id" class="projectCard">
          <div class="cardTop">
            <span class="categoryBadge">{{ p.category }}</span>
            <button class="removeFavBtn" @click="removeFav(p.id)">❤️</button>
          </div>
          <h2 class="projectTitle">{{ p.title }}</h2>
          <div class="fundingRow">
            <span><b>${{ p.current }}</b> / <small>${{ p.goal }}</small></span>
            <span class="percentText">{{ Math.round((p.current/p.goal)*100) }}%</span>
          </div>
          <div class="progressBar"><div class="fill" :style="{width: (p.current/p.goal*100)+'%'}"></div></div>
          
          <div class="statsRow">
            <div class="statPill"><span class="material-symbols-outlined">visibility</span> {{ p.views || 0 }}</div>
            <div class="statPill"><span class="material-symbols-outlined">star</span> {{ p.favCount || 0 }}</div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
const projects = ref(JSON.parse(localStorage.getItem('fraProjects') || '[]'))
const favs = ref(JSON.parse(localStorage.getItem('fraFavs') || '[]'))

const savedList = computed(() => projects.value.filter(p => favs.value.includes(p.id)))

const removeFav = (id) => {
  favs.value = favs.value.filter(f => f !== id);
  const p = projects.value.find(x => x.id === id);
  if(p) p.favCount--;
  localStorage.setItem('fraFavs', JSON.stringify(favs.value));
  localStorage.setItem('fraProjects', JSON.stringify(projects.value));
}
</script>

<style scoped>
.doneePage { background: #fcfcfd; min-height: 100vh; }
.pageHeader { background: white; padding: 40px 20px; border-bottom: 1px solid #f1f5f9; text-align: center; }
.mainTitle { font-size: 32px; font-weight: 900; color: #0f172a; margin-bottom: 25px; }

.gridWrapper { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 25px; padding: 40px; }
.projectCard { 
  background: white; border-radius: 32px; padding: 28px; border: 1px solid #f1f5f9;
  box-shadow: 0 10px 25px rgba(0,0,0,0.02);
}

.cardTop { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.categoryBadge { background: #eff6ff; color: #3b82f6; font-size: 10px; font-weight: 900; padding: 6px 12px; border-radius: 8px; text-transform: uppercase; }
.removeFavBtn { background: none; border: none; font-size: 20px; cursor: pointer; transition: 0.2s; }
.removeFavBtn:hover { transform: scale(1.2); }

.projectTitle { font-size: 22px; font-weight: 700; color: #1e293b; margin-bottom: 20px; }
.fundingRow { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 8px; }
.percentText { color: #3b82f6; font-weight: 800; font-size: 14px; }
.progressBar { height: 6px; background: #f1f5f9; border-radius: 10px; overflow: hidden; margin-bottom: 20px; }
.progressBar .fill { height: 100%; background: #3b82f6; border-radius: 10px; }

.statsRow { display: flex; gap: 10px; margin-bottom: 10px; }
.statPill { background: #f8fafc; border: 1px solid #f1f5f9; padding: 6px 14px; border-radius: 14px; display: flex; align-items: center; gap: 6px; font-size: 14px; font-weight: 700; color: #64748b; }

.emptyState { text-align: center; margin-top: 100px; color: #94a3b8; }
.emptyState span { font-size: 60px; margin-bottom: 10px; }
</style>