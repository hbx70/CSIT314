<template>
  <div class="doneePage">
    <header class="pageHeader">
      <h1 class="mainTitle">Donation History</h1>
    </header>

    <main class="mainContent">
      <div class="historyWrapper">
        <div v-if="history.length === 0" class="emptyHistory">No donation records found.</div>
        <div v-else class="historyCard">
          <div class="historyItem header">
            <span>Project Name</span>
            <span>Amount</span>
            <span>Date</span>
          </div>
          <div v-for="item in history" :key="item.id" class="historyItem">
            <span class="h-name">{{ item.title }}</span>
            <span class="h-amount">$ {{ item.amount }}</span>
            <span class="h-date">{{ item.date }}</span>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const history = ref(JSON.parse(localStorage.getItem('fraDonations') || '[]'))
</script>

<style scoped>
.doneePage { background: #fcfcfd; min-height: 100vh; }
.pageHeader { background: white; padding: 40px 20px; border-bottom: 1px solid #f1f5f9; text-align: center; }
.mainTitle { font-size: 32px; font-weight: 900; color: #0f172a; margin-bottom: 25px; }

.historyWrapper { max-width: 900px; margin: 40px auto; padding: 0 20px; }
.historyCard { background: white; border-radius: 24px; padding: 10px; box-shadow: 0 10px 25px rgba(0,0,0,0.02); border: 1px solid #f1f5f9; }

.historyItem { 
  display: grid; grid-template-columns: 2.5fr 1fr 1fr; 
  padding: 20px; border-bottom: 1px solid #f8fafc; align-items: center;
}
.historyItem.header { font-weight: 800; color: #64748b; font-size: 12px; text-transform: uppercase; letter-spacing: 1px; }
.historyItem:last-child { border-bottom: none; }

.h-name { font-weight: 700; color: #1e293b; }
.h-amount { color: #10b981; font-weight: 800; font-size: 16px; }
.h-date { color: #94a3b8; font-size: 14px; }

.emptyHistory { text-align: center; color: #94a3b8; margin-top: 100px; }
</style>