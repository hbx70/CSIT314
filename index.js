import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../component/LoginView.vue'
import UserProfile from '../component/UserProfile.vue'
import AdminView from '../component/AdminView.vue'
import FundRaiserView from '../component/FundRaiserView.vue'
import PlatformManagerView from '../component/PlatformManagerView.vue'
import DoneeView from '@/component/DoneeView.vue' 

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: LoginView },
    
    // 1. Administrator 对应的页面 (保持原样)
    { 
      path: '/admin/profile', 
      component: UserProfile, 
      meta: { requiresAuth: true, role: 'Administrator' } 
    },
    { 
      path: '/admin/directory', 
      component: AdminView, 
      meta: { requiresAuth: true, role: 'Administrator' } 
    },

    // 2. Fund Raiser 对应的页面
    { 
      path: '/fundraiser', 
      component: FundRaiserView, 
      meta: { requiresAuth: true, role: 'Fund Raiser' } 
    },

    // 3. Platform Manager 对应的页面
    { 
      path: '/manager', 
      component: PlatformManagerView, 
      meta: { requiresAuth: true, role: 'Platform Manager' } 
    },

    // 4. 【补全】Donee 对应的页面
    { 
      path: '/donee', 
      component: DoneeView, 
      meta: { requiresAuth: true, role: 'Donee' } 
    }
  ]
})

router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('currentUser'))
  
  if (to.meta.requiresAuth) {
    if (!user) {
      return next('/login')
    }
    
    // 权限检查：确保用户角色与路由定义的 role 匹配
    // 注意：这里的 user.role 字符串必须与 meta.role 完全一致
    if (to.meta.role && to.meta.role !== user.role) {
      console.warn('权限不足，正在重定向');
      return next('/login')
    }
  }
  next()
})

export default router