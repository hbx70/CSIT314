<template>
    <div class="mainContainer">
        <div class="headerBlock">
            <div class="titleGroup">
                <h1>Campaign Dashboard</h1>
                <p>Manage and track your fundraising impact</p>
            </div>
            <button class="actionItem primary" @click="showModal = true">
                <span class="plusIcon">+</span> New Campaign
            </button>
        </div>

        <div class="filterBlock">
            <div class="searchWrapper">
                <input v-model="searchQuery" placeholder="Search by title..." class="fieldItem" />
            </div>
            <div class="tabsBox">
                <button v-for="s in ['All', 'Active', 'Suspended', 'Completed']" :key="s" @click="currentFilter = s"
                    :class="{ active: currentFilter === s }">
                    {{ s }}
                </button>
            </div>
        </div>

        <div class="gridBlock">
            <div v-for="project in filteredProjects" :key="project.id" class="cardBox">
                <div class="tagRow">
                    <span class="tagItem">{{ project.category }}</span>
                    <span class="statusBadge" :class="project.status.toLowerCase()">{{ project.status }}</span>
                </div>
                <h3 class="titleItem">{{ project.title }}</h3>

                <div class="progressContainer">
                    <div class="infoBox">
                        <strong>${{ project.current }} <span>/ ${{ project.goal }}</span></strong>
                        <span class="pctText">{{ ((project.current / project.goal) * 100).toFixed(0) }}%</span>
                    </div>
                    <div class="trackBox">
                        <div class="barItem"
                            :style="{ width: Math.min((project.current / project.goal) * 100, 100) + '%' }"></div>
                    </div>
                </div>

                <div class="controlBox">
                    <template v-if="project.status !== 'Completed'">
                        <button @click="editGoal(project)" class="btnItem ghost">Update Goal</button>
                        <button @click="toggleStatus(project)" class="btnItem outline">Pause</button>
                    </template>
                    <button @click="deleteProject(project.id)" class="btnItem danger">Delete</button>
                </div>
            </div>
        </div>

        <div v-if="showModal" class="modalOverlay">
            <div class="modalContent">
                <div class="modalHeader">
                    <h3>Launch Activity</h3>
                    <p>Provide the essential details to start collecting donations.</p>
                </div>

                <div class="formBody">
                    <div class="inputGroup">
                        <label>Campaign Title</label>
                        <input v-model="newProject.title" placeholder="e.g. Community Garden Support"
                            class="styledInput" />
                    </div>

                    <div class="inputRow">
                        <div class="inputGroup">
                            <label>Category</label>
                            <select v-model="newProject.category" class="styledInput">
                                <option v-for="c in categories" :key="c">{{ c }}</option>
                            </select>
                        </div>
                        <div class="inputGroup">
                            <label>Goal Amount ($)</label>
                            <input v-model.number="newProject.goal" type="number" class="styledInput" />
                        </div>
                    </div>
                </div>

                <div class="modalFooter">
                    <button @click="showModal = false" class="footerBtn secondary">Discard</button>
                    <button @click="confirmAdd" class="footerBtn primary">Create Campaign</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

// 数据逻辑
const projectList = ref(JSON.parse(localStorage.getItem('fraProjects') || '[]'))
const categories = ref(JSON.parse(localStorage.getItem('fraCategories') || '["Medical", "Education", "Nature", "Community"]'))
const searchQuery = ref('')
const currentFilter = ref('All')
const showModal = ref(false)
const newProject = ref({ title: '', category: 'Medical', goal: 1000, current: 0, status: 'Active' })

watch(projectList, (val) => localStorage.setItem('fraProjects', JSON.stringify(val)), { deep: true })

const filteredProjects = computed(() => {
    return projectList.value.filter(p => {
        const matchSearch = p.title.toLowerCase().includes(searchQuery.value.toLowerCase())
        const matchStatus = currentFilter.value === 'All' ? true : p.status === currentFilter.value
        return matchSearch && matchStatus
    })
})

const confirmAdd = () => {
    if (newProject.value.title) {
        projectList.value.push({ ...newProject.value, id: Date.now() })
        showModal.value = false
        newProject.value = { title: '', category: 'Medical', goal: 1000, current: 0, status: 'Active' }
    }
}

const toggleStatus = (p) => p.status = (p.status === 'Active' ? 'Suspended' : 'Active')
const deleteProject = (id) => { if (confirm("Permanently delete this campaign?")) projectList.value = projectList.value.filter(p => p.id !== id) }
const editGoal = (p) => { const g = prompt("Set new goal amount:", p.goal); if (g) p.goal = Number(g) }
</script>

<style scoped>
/* 页面基础布局 */
.mainContainer {
    background: #f8fafc;
    padding: 40px;
    min-height: 100vh;
    font-family: 'Inter', sans-serif;
}

.headerBlock {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40px;
    background: white;
    padding: 30px;
    border-radius: 28px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.titleGroup h1 {
    font-size: 28px;
    font-weight: 900;
    color: #0f172a;
    margin: 0;
}

.titleGroup p {
    color: #64748b;
    margin: 5px 0 0 0;
}

.actionItem.primary {
    background: #2563eb;
    color: white;
    border: none;
    padding: 14px 28px;
    border-radius: 16px;
    font-weight: 700;
    cursor: pointer;
    transition: 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.actionItem.primary:hover {
    background: #1d4ed8;
    transform: translateY(-2px);
    box-shadow: 0 10px 15px -3px rgba(37, 99, 235, 0.4);
}

/* 过滤器 */
.filterBlock {
    display: flex;
    gap: 20px;
    margin-bottom: 35px;
    align-items: center;
}

.searchWrapper {
    flex: 1;
}

.fieldItem {
    width: 100%;
    padding: 14px 20px;
    border-radius: 16px;
    border: 1px solid #e2e8f0;
    outline: none;
    transition: 0.2s;
}

.fieldItem:focus {
    border-color: #3b82f6;
    box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.tabsBox {
    display: flex;
    gap: 8px;
    background: #e2e8f0;
    padding: 6px;
    border-radius: 18px;
}

.tabsBox button {
    padding: 10px 20px;
    border-radius: 14px;
    border: none;
    font-size: 12px;
    font-weight: 700;
    cursor: pointer;
    transition: 0.2s;
    color: #64748b;
    background: transparent;
}

.tabsBox button.active {
    background: white;
    color: #0f172a;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

/* 活动卡片 */
.gridBlock {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 25px;
}

.cardBox {
    background: white;
    padding: 35px;
    border-radius: 36px;
    border: 1px solid #f1f5f9;
    transition: 0.3s;
    position: relative;
}

.cardBox:hover {
    transform: translateY(-8px);
    border-color: #3b82f6;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.05);
}

.tagRow {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
}

.tagItem {
    background: #eff6ff;
    color: #3b82f6;
    padding: 6px 14px;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 800;
    text-transform: uppercase;
}

.statusBadge {
    font-size: 10px;
    font-weight: 900;
    text-transform: uppercase;
    color: #94a3b8;
}

.titleItem {
    font-size: 22px;
    font-weight: 800;
    color: #1e293b;
    margin: 0 0 25px 0;
    line-height: 1.3;
}

/* 进度条 */
.progressContainer {
    margin-bottom: 30px;
}

.infoBox {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 10px;
}

.infoBox strong {
    font-size: 20px;
    color: #0f172a;
}

.infoBox strong span {
    font-size: 14px;
    color: #94a3b8;
    font-weight: 500;
}

.pctText {
    color: #3b82f6;
    font-weight: 900;
}

.trackBox {
    height: 10px;
    background: #f1f5f9;
    border-radius: 20px;
    overflow: hidden;
}

.barItem {
    height: 100%;
    background: linear-gradient(90deg, #3b82f6, #60a5fa);
    border-radius: 20px;
}

.controlBox {
    display: flex;
    gap: 10px;
}

.btnItem {
    flex: 1;
    padding: 12px;
    border-radius: 14px;
    border: none;
    font-size: 11px;
    font-weight: 800;
    cursor: pointer;
    text-transform: uppercase;
    transition: 0.2s;
}

.btnItem.ghost {
    background: #f8fafc;
    color: #64748b;
}

.btnItem.danger {
    background: #fff1f2;
    color: #e11d48;
}

/* ========================================= 
   重新设计的弹窗样式 (Modal)
   ========================================= */
.modalOverlay {
    position: fixed;
    inset: 0;
    background: rgba(15, 23, 42, 0.6);
    backdrop-filter: blur(8px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    animation: fadeIn 0.3s ease;
}

.modalContent {
    background: white;
    padding: 45px;
    border-radius: 45px;
    width: 100%;
    max-width: 500px;
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
    animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.modalHeader {
    margin-bottom: 35px;
}

.modalHeader h3 {
    font-size: 26px;
    font-weight: 900;
    color: #0f172a;
    margin: 0 0 10px 0;
}

.modalHeader p {
    color: #64748b;
    font-size: 15px;
    margin: 0;
}

.formBody {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.inputRow {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 15px;
}

.inputGroup {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.inputGroup label {
    font-size: 13px;
    font-weight: 800;
    color: #94a3b8;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

.styledInput {
    width: 100%;
    padding: 15px 20px;
    border-radius: 18px;
    border: 2px solid #f1f5f9;
    background: #f8fafc;
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
    outline: none;
    transition: 0.2s;
}

.styledInput:focus {
    border-color: #3b82f6;
    background: white;
    box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.modalFooter {
    display: flex;
    gap: 15px;
    margin-top: 40px;
}

.footerBtn {
    flex: 1;
    padding: 18px;
    border-radius: 20px;
    border: none;
    font-size: 15px;
    font-weight: 800;
    cursor: pointer;
    transition: 0.2s;
}

.footerBtn.secondary {
    background: #f1f5f9;
    color: #64748b;
}

.footerBtn.primary {
    background: #0f172a;
    color: white;
}

.footerBtn.primary:hover {
    background: #2563eb;
    transform: translateY(-2px);
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>