<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="brand-panel">
        <div class="logo-text">CSIT314</div>
        <div class="brand-content">
          <h2 class="system-title">Donation System</h2>
          <p class="system-desc">Secure management platform. Access is restricted to accounts created by administrators.</p>
        </div>
        <div class="footer-note">V1.0.5 • Secure Entry</div>
      </div>

      <div class="form-panel">
        <div class="form-card">
          <header class="form-header">
            <h1>Sign In</h1>
            <p>Please enter your assigned credentials</p>
          </header>

          <div class="input-stack">
            <div class="input-group">
              <label>Username</label>
              <input v-model="loginData.username" type="text" placeholder="Username" />
            </div>

            <div class="input-group">
              <label>Password</label>
              <input v-model="loginData.password" type="password" placeholder="••••••••" />
            </div>
          </div>

          <button @click="handleLogin" class="btn-primary">Login</button>
          
          <div class="login-footer">
            <p>Forgot password? Contact your Administrator.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginData = ref({ username: '', password: '' })

const handleLogin = () => {
  // 从本地存储读取所有用户（包括管理员手动创建的用户）
  const users = JSON.parse(localStorage.getItem('app_users') || '[]')
  
  // 查找匹配的用户
  const user = users.find(u => u.username === loginData.value.username && u.password === loginData.value.password)
  
  if (user) {
    localStorage.setItem('currentUser', JSON.stringify(user))
    
    // 根据管理员设置的角色进行精准跳转
    const routes = {
      'Administrator': '/admin',
      'Donee': '/donee',
      'Fund Raiser': '/fundraiser',
      'Platform Manager': '/manager'
    }
    
    const targetPath = routes[user.role] || '/'
    router.push(targetPath)
  } else {
    alert('Invalid credentials. Please contact your system administrator.')
  }
}
</script>

<style scoped>
.auth-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #ffffff; font-family: 'Inter', sans-serif; }
.auth-container { display: flex; width: 100%; max-width: 1000px; background: #ffffff; border-radius: 32px; box-shadow: 0 40px 100px -20px rgba(0,0,0,0.05); border: 1px solid #f1f5f9; overflow: hidden; }
.brand-panel { flex: 1; padding: 60px; border-right: 1px solid #f1f5f9; display: flex; flex-direction: column; justify-content: space-between; }
.logo-text { font-size: 18px; font-weight: 800; color: #3b82f6; }
.system-title { font-size: 36px; font-weight: 900; color: #0f172a; margin-top: 10px; }
.system-desc { color: #64748b; font-size: 15px; line-height: 1.6; margin-top: 15px; }
.form-panel { flex: 1.2; padding: 60px; display: flex; align-items: center; justify-content: center; }
.form-card { width: 100%; max-width: 340px; }
.form-header h1 { font-size: 24px; font-weight: 800; color: #0f172a; }
.form-header p { font-size: 14px; color: #94a3b8; margin: 10px 0 35px 0; }
.input-stack { display: flex; flex-direction: column; gap: 20px; }
.input-group label { display: block; font-size: 11px; font-weight: 800; color: #3b82f6; text-transform: uppercase; margin-bottom: 8px; }
.input-group input { width: 100%; padding: 14px 18px; border: 1px solid #e2e8f0; border-radius: 12px; font-size: 15px; box-sizing: border-box; }
.btn-primary { width: 100%; margin-top: 35px; padding: 16px; background: #0f172a; color: white; border: none; border-radius: 12px; font-size: 16px; font-weight: 700; cursor: pointer; transition: 0.3s; }
.btn-primary:hover { background: #3b82f6; }
.login-footer { text-align: center; margin-top: 25px; font-size: 12px; color: #cbd5e1; }
@media (max-width: 900px) { .brand-panel { display: none; } }
</style>