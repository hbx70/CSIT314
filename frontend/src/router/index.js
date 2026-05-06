import { createRouter, createWebHistory } from "vue-router";

// 这里的路径必须和你新建的文件名完全一致
import LoginView from "../component/LoginView.vue";
import AdminView from "../component/AdminView.vue";
import DoneeView from "../component/DoneeView.vue";
import FundRaiserView from "../component/FundRaiserView.vue";
import PlatformManagerView from "../component/PlatformManagerView.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", redirect: "/login" },
    { path: "/login", component: LoginView },
    {
      path: "/admin",
      component: AdminView,
      //   meta: { requiresAuth: true, role: "ADMIN" },
    },
    {
      path: "/donee",
      component: DoneeView,
      //   meta: { requiresAuth: true, role: "DONEE" },
    },
    {
      path: "/fundraiser",
      component: FundRaiserView,
      //   meta: { requiresAuth: true, role: "FUND_RAISER" },
    },
    {
      path: "/manager",
      component: PlatformManagerView,
      //   meta: { requiresAuth: true, role: "PLATFORM_MANAGER" },
    },
  ],
});

// 路由守卫：防止未登录乱跑，防止进错角色页面
// router.beforeEach((to, from, next) => {
//   const user = JSON.parse(localStorage.getItem("currentUser"));

//   if (to.meta.requiresAuth) {
//     if (!user) {
//       next("/login");
//     } else if (to.meta.role && to.meta.role !== user.role) {
//       alert("Unauthorized! Redirecting to login.");
//       next("/login");
//     } else {
//       next();
//     }
//   } else {
//     next();
//   }
// });

export default router;
