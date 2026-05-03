<template>
  <div class="p-8 flex items-center justify-center min-h-[80vh]">
    <div class="w-full max-w-md bg-white p-12 rounded-[3rem] shadow-xl border border-slate-100 text-center">
      <div class="text-4xl mb-6">👤</div>
      <h2 class="text-2xl font-black text-slate-900 uppercase italic mb-8">Account Settings</h2>
      
      <div class="space-y-4 text-left">
        <div>
          <label class="text-[9px] font-black text-slate-400 uppercase ml-2">Display Name</label>
          <input type="text" value="Current User" class="w-full p-4 bg-slate-50 rounded-2xl font-bold outline-none border-none focus:ring-2 ring-sky-500" />
        </div>
        <button class="w-full py-4 bg-sky-600 text-white rounded-2xl text-[10px] font-black uppercase shadow-lg">Update Profile</button>
        <button class="w-full py-4 border-2 border-rose-100 text-rose-500 rounded-2xl text-[10px] font-black uppercase mt-12 hover:bg-rose-50">Log Out Securely</button>
      </div>
    </div>
  </div>
</template>