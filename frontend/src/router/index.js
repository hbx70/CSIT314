import { createRouter, createWebHistory } from "vue-router";

// 这里的路径必须和你新建的文件名完全一致
import LoginView from "../component/LoginView.vue";
import AdminUserAccountView from "../component/AdminUserAccountView.vue";
import FundRaiserView from "../component/FundRaiserView.vue";
import PMCategoryView from "../component/PMCategoryView.vue";
import AdminUserProfileView from "@/component/AdminUserProfileView.vue";
import PMReportView from "@/component/PMReportView.vue";
import DoneeView from '../component/DoneeView.vue'
import DoneeSave from '../component/DoneeSave.vue'
import DoneeHistory from '../component/DoneeHistory.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", redirect: "/login" },
    { path: "/login", component: LoginView },
    {
        path: "/admin/account",
        component: AdminUserAccountView,
        //meta: { requiresAuth: true, role: "ADMIN" },
    },
    {
        path: "/admin/profile",
        component: AdminUserProfileView,
        //meta: { requiresAuth: true, role: "PROFILE" },
    },
    {
        path: "/donee/explore",
        component: DoneeView,
        //meta: { requiresAuth: true, role: "DONEE" },
    },
    {
        path: "/donee/saved",
        component: DoneeSave,
        //meta: { requiresAuth: true, role: "DONEE" },
    },
    {
        path: "/donee/history",
        component: DoneeHistory,
        //meta: { requiresAuth: true, role: "DONEE" },
    },
    {
        path: "/fundraiser",
        component: FundRaiserView,
        //   meta: { requiresAuth: true, role: "FUND_RAISER" },
    },
    {
        path: "/manager/category",
        component: PMCategoryView,
        // meta: { requiresAuth: true, role: 'Platform Manager', mode: 'category' }
    },
    {
        path: "/manager/report",
        component: PMReportView
    }
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
