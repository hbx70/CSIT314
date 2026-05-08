<template>
  <div v-if="$route.path !== '/login' && currentUser" class="appLayout">
    <aside class="sidebar">
      <div class="logo">CSIT314</div>
      
      <nav class="navMenu">
        <div class="expanded-menu">
          
          <template v-if="currentUser.role === 'Administrator'">
            <div class="nav-item-wrapper" @click="$router.push('/admin/profile')">
              <div :class="['nav-btn', { active: $route.path === '/admin/profile' }]">UP</div>
              <span class="nav-label">Profile</span>
            </div>
            <div class="nav-item-wrapper" @click="$router.push('/admin/directory')">
              <div :class="['nav-btn ad-theme', { active: $route.path === '/admin/directory' }]">AD</div>
              <span class="nav-label">Directory</span>
            </div>
          </template>

          <template v-else-if="currentUser.role === 'Platform Manager'">
            <div class="nav-item-wrapper" @click="$router.push('/manager/category')">
              <div :class="['nav-btn', { active: $route.path === '/manager/category' }]">CC</div>
              <span class="nav-label">Category</span>
            </div>
            <div class="nav-item-wrapper" @click="$router.push('/manager/daily')">
              <div :class="['nav-btn', { active: $route.path === '/manager/daily' }]">DR</div>
              <span class="nav-label">Daily</span>
            </div>
            <div class="nav-item-wrapper" @click="$router.push('/manager/weekly')">
              <div :class="['nav-btn', { active: $route.path === '/manager/weekly' }]">WR</div>
              <span class="nav-label">Weekly</span>
            </div>
            <div class="nav-item-wrapper" @click="$router.push('/manager/monthly')">
              <div :class="['nav-btn', { active: $route.path === '/manager/monthly' }]">MR</div>
              <span class="nav-label">Monthly</span>
            </div>
          </template>

          <template v-else>
            </template>

        </div>
      </nav>

      <button @click="logout" class="logoutBtn">Logout</button>
    </aside>

    <main class="content">
      <router-view :key="$route.fullPath" />
    </main>
  </div>

  <router-view v-else />
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 响应式获取当前登录用户
const currentUser = computed(() => {
  const user = localStorage.getItem('currentUser')
  return user ? JSON.parse(user) : null
})

// 退出登录函数
const logout = () => {
  localStorage.removeItem('currentUser')
  router.push('/login')
}
</script>

<style>
/* 布局容器 */
.appLayout { 
  display: flex; 
  height: 100vh; 
  width: 100vw; 
  background: #fcfcfd; 
  overflow: hidden; 
}

/* 侧边栏：宽度统一设为150px，维持高级感 */
.sidebar { 
  width: 150px; 
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
  margin-bottom: 60px; 
  font-size: 18px; 
  letter-spacing: 1px;
}

.navMenu { 
  width: 100%; 
}

.expanded-menu { 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  gap: 35px; 
  width: 100%; 
}

/* 按钮包装容器 */
.nav-item-wrapper { 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  width: 100%; 
  cursor: pointer; 
}

/* 按钮原始风格 */
.nav-btn { 
  width: 56px; 
  height: 56px; 
  background: #1e293b; 
  border-radius: 18px; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  color: #94a3b8; 
  font-size: 13px; 
  font-weight: 800; 
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); 
}

/* 悬停效果 */
.nav-item-wrapper:hover .nav-btn {
  background: #2d3748;
  color: white;
}

/* 激活状态：蓝色发光 */
.nav-btn.active { 
  background: #3b82f6; 
  color: white;
  box-shadow: 0 10px 25px rgba(59, 130, 246, 0.4); 
  transform: translateY(-2px);
}

.nav-label { 
  font-size: 10px; 
  font-weight: 700; 
  color: #64748b; 
  margin-top: 10px; 
  text-transform: uppercase; 
  letter-spacing: 0.5px;
}

/* 底部退出按钮 */
.logoutBtn { 
  background: transparent; 
  border: 1px solid #1e293b; 
  color: #64748b; 
  font-size: 11px; 
  font-weight: 800; 
  padding: 10px 20px; 
  border-radius: 12px; 
  cursor: pointer; 
  margin-top: auto; 
  margin-bottom: 40px; 
  transition: all 0.2s;
}

.logoutBtn:hover {
  background: #ef4444;
  color: white;
  border-color: #ef4444;
}

/* 右侧主内容区域 */
.content { 
  flex: 1; 
  overflow-y: auto; 
  background: white; 
}
</style>