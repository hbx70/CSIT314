<template>
    <div v-if="$route.path !== '/login' && userInfoStore.info" class="appLayout">
        <aside class="sidebar">
            <div class="logo">CSIT314</div>

            <nav class="navMenu">
                <div class="expanded-menu">

                    <template v-if="userInfoStore.info.userProfileName === 'ADMIN'">
                        <div class="nav-item-wrapper" @click="$router.push('/admin/account')">
                            <div :class="['nav-btn ad-theme', { active: $route.path === '/admin/account' }]"><span
                                    class="material-symbols-outlined">account_circle</span></div>
                            <span class="nav-label">Account</span>
                        </div>
                        <div class="nav-item-wrapper" @click="$router.push('/admin/profile')">
                            <div :class="['nav-btn', { active: $route.path === '/admin/profile' }]"><span
                                    class="material-symbols-outlined">badge</span></div>
                            <span class="nav-label">Profile</span>
                        </div>
                    </template>
                    <template v-else-if="userInfoStore.info.role === 'DONEE'">
                        <div class="nav-item-wrapper" @click="$router.push('/donee/explore')">
                            <div :class="['nav-btn', { active: $route.path === '/donee/explore' }]">
                                <span class="material-symbols-outlined">volunteer_activism</span>
                            </div>
                            <span class="nav-label">Explore</span>
                        </div>
                        <div class="nav-item-wrapper" @click="$router.push('/donee/saved')">
                            <div :class="['nav-btn', { active: $route.path === '/donee/saved' }]"><span class="material-symbols-outlined">star</span></div>
                            <span class="nav-label">Saved</span>
                        </div>
                        <div class="nav-item-wrapper" @click="$router.push('/donee/history')">
                            <div :class="['nav-btn', { active: $route.path === '/donee/history' }]"><span class="material-symbols-outlined">history</span></div>
                            <span class="nav-label">History</span>
                        </div>
                    </template>

                    <template v-else-if="userInfoStore.info.userProfileName === 'PLATFORM_MANAGER'">
                        <div class="nav-item-wrapper" @click="$router.push('/manager/category')">
                            <div :class="['nav-btn', { active: $route.path === '/manager/category' }]"><span
                                    class="material-symbols-outlined">category</span></div>
                            <span class="nav-label">Category</span>
                        </div>
                        <div class="nav-item-wrapper" @click="$router.push('/manager/report')">
                            <div :class="['nav-btn', { active: $route.path === '/manager/report' }]"><span
                                    class="material-symbols-outlined">analytics</span></div>
                            <span class="nav-label">Report</span>
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
import { useRouter } from 'vue-router'
import { useUserInfoStore } from './stores/userInfo'

const router = useRouter()

const userInfoStore = useUserInfoStore()

const logout = () => {
    localStorage.removeItem('currentUser')
    router.push('/login')
}
</script>

<style>
.material-symbols-outlined {
    font-variation-settings:
        'FILL' 0,
        'wght' 300,
        'GRAD' 200,
        'opsz' 48;
    font-size: 30px;
    transition: all 0.3s ease;
}

.appLayout {
    display: flex;
    height: 100vh;
    width: 100vw;
    background: #fcfcfd;
    overflow: hidden;
}

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

.nav-item-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    cursor: pointer;
}

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

.nav-item-wrapper:hover .nav-btn {
    background: #2d3748;
    color: white;
}

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

.content {
    flex: 1;
    overflow-y: auto;
    background: white;
}
</style>