<template>
  <router-view v-if="$route.path === '/login'" />

  <div v-else class="appLayout">
    <aside class="sidebar">
      <div class="logo">FRA</div>

      <nav class="navMenu">
        <div class="expanded-menu">
          <div class="nav-item-wrapper">
            <div class="nav-btn active">
              {{ roleIcon }}
            </div>
            <span class="nav-label">{{ currentUser?.role || 'User' }}</span>
          </div>

          <template v-if="currentUser?.role === 'Administrator'">
            <div class="nav-item-wrapper" @click="$router.push('/admin/profile')">
              <div :class="['nav-btn', { active: $route.path === '/admin/profile' }]">UP</div>
              <span class="nav-label">Profile</span>
            </div>

            <div class="nav-item-wrapper" @click="$router.push('/admin/directory')">
              <div :class="['nav-btn', { active: $route.path === '/admin/directory' }]">AD</div>
              <span class="nav-label">Directory</span>
            </div>
          </template>
        </div>
      </nav>

      <button @click="logout" class="logoutBtn">Logout</button>
    </aside>

    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 从本地存储获取当前用户信息
const currentUser = computed(() => {
  const user = localStorage.getItem('currentUser')
  return user ? JSON.parse(user) : null
})

// 动态角色图标逻辑
const roleIcon = computed(() => {
  const icons = { 
    'Administrator': '🛠️', 
    'Fund Raiser': '📢', 
    'Donee': '🤝', 
    'Platform Manager': '📊' 
  }
  return icons[currentUser.value?.role] || '👤'
})

const logout = () => {
  localStorage.removeItem('currentUser')
  router.push('/login')
}
</script>

<style scoped>
/* 继承你提供的外观样式 */
.appLayout {
  display: flex;
  height: 100vh;
  width: 100vw;
  background: #fcfcfd;
  overflow: hidden;
}

.sidebar {
  width: 150px; /* 统一宽度 */
  background: #0f172a;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 40px;
  flex-shrink: 0;
  border-right: 1px solid #1e293b;
}

.logo {
  font-weight: 900;
  color: #3b82f6;
  margin-bottom: 50px;
  font-size: 20px;
  letter-spacing: 1px;
}

.navMenu {
  width: 100%;
}

.expanded-menu {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
  width: 100%;
}

.nav-item-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.nav-item-wrapper:hover {
  transform: translateY(-2px);
}

.nav-btn {
  width: 56px;
  height: 56px;
  background: #1e293b;
  color: #94a3b8;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 20px;
  transition: all 0.3s ease;
}

.nav-btn.active {
  background: #3b82f6;
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.nav-label {
  font-size: 10px;
  color: #64748b;
  font-weight: 700;
  margin-top: 8px;
  text-transform: uppercase;
  text-align: center;
}

.content {
  flex: 1;
  overflow-y: auto;
  background: #f8fafc;
  padding: 20px;
}

.logoutBtn {
  margin-top: auto;
  margin-bottom: 40px;
  background: none;
  border: 1px solid #334155;
  color: #94a3b8;
  padding: 8px 20px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logoutBtn:hover {
  background: #ef4444;
  color: white;
  border-color: #ef4444;
  box-shadow: 0 4px 10px rgba(239, 68, 68, 0.3);
}

/* 全局滚动条美化 (可选) */
.content::-webkit-scrollbar {
  width: 6px;
}
.content::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}
</style>