<template>
    <router-view v-if="$route.path === '/login'" />

    <div v-else class="appLayout">
        <aside class="sidebar">
            <div class="logo">FRA</div>
            <nav class="navMenu">
                <div class="activeIndicator"></div>
                <span class="roleIcon">{{ roleIcon }}</span>
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
import { useRouter } from 'vue-router'

const router = useRouter()
const currentUser = computed(() => JSON.parse(localStorage.getItem('currentUser')))

const roleIcon = computed(() => {
    const icons = { 'Administrator': '🛠️', 'Fund Raiser': '📢', 'Donee': '🤝', 'Platform Manager': '📊' }
    return icons[currentUser.value?.role] || '👤'
})

const logout = () => {
    localStorage.removeItem('currentUser')
    router.push('/login')
}
</script>

<style>
body {
    margin: 0;
    padding: 0;
    background: #f8fafc;
}

.appLayout {
    display: flex;
    height: 100vh;
}

.sidebar {
    width: 80px;
    background: #0f172a;
    display: flex;
    flex-direction: column;
    align-items: center;
    color: white;
}

.logo {
    font-weight: 900;
    color: #3b82f6;
    margin: 20px 0;
}

.content {
    flex: 1;
    overflow-y: auto;
}

.logoutBtn {
    margin-top: auto;
    margin-bottom: 20px;
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
}

.roleIcon {
    font-size: 24px;
    margin-top: 20px;
}
</style>