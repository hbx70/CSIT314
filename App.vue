<template>
  <div v-if="$route.path.startsWith('/admin')" class="appLayout">
    <aside class="sidebar">
      <div class="logo">CSIT314</div>
      
      <nav class="navMenu">
        <div class="expanded-menu">
          <div class="nav-item-wrapper" @click="$router.push('/admin/profile')">
            <div :class="['nav-btn', { active: $route.path === '/admin/profile' }]">UP</div>
            <span class="nav-label">Profile</span>
          </div>

          <div class="nav-item-wrapper" @click="$router.push('/admin/directory')">
            <div :class="['nav-btn ad-theme', { active: $route.path === '/admin/directory' }]">AD</div>
            <span class="nav-label">Directory</span>
          </div>
        </div>
      </nav>

      <button @click="logout" class="logoutBtn">Logout</button>
    </aside>

    <main class="content">
      <router-view />
    </main>
  </div>

  <div v-else-if="['/fundraiser', '/manager', '/donee'].includes($route.path)" class="appLayout">
    <aside class="sidebar">
      <div class="logo">CSIT314</div>
      <button @click="logout" class="logoutBtn">Logout</button>
    </aside>

    <main class="content">
      <router-view />
    </main>
  </div>

  <router-view v-else />
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const logout = () => {
  localStorage.removeItem('currentUser') // 清除登录信息
  router.push('/login')
}
</script>

<style scoped>
.appLayout { 
  display: flex; 
  height: 100vh; 
  width: 100vw; 
  background: #fcfcfd; 
  overflow: hidden; 
}

/* 统一修改：边栏宽度设定为 150px */
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
  margin-bottom: 50px; 
  font-size: 16px; 
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

.nav-item-wrapper { 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  width: 100%; 
  cursor: pointer; 
}

/* 按钮原始样式保持不变 */
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
}

.nav-btn.active { 
  background: #3b82f6; 
  color: white; 
}

.nav-label { 
  font-size: 10px; 
  color: #64748b; 
  font-weight: 700; 
  margin-top: 8px; 
  text-transform: uppercase; 
}

.content { 
  flex: 1; 
  overflow-y: auto; 
  background: #f8fafc; 
}

.logoutBtn { 
  margin-top: auto; 
  margin-bottom: 40px; 
  background: none; 
  border: 1px solid #334155; 
  color: #94a3b8; 
  padding: 8px 15px; 
  border-radius: 10px; 
  font-size: 11px; 
  cursor: pointer; 
}

.logoutBtn:hover { 
  background: #ef4444; 
  color: white; 
  border-color: #ef4444;
}
</style>