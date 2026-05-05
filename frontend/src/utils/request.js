import axios from 'axios';

import { ElMessage } from 'element-plus'

const baseURL = '/api';
const instance = axios.create({baseURL})


import { useTokenStore } from '@/stores/token.js';
instance.interceptors.request.use(
    (config) => {
        const tokenStore = useTokenStore();
        if (tokenStore.token) {
            config.headers.Authorization = tokenStore.token
        }
        return config
    },
    (err) => {
        Promise.reject(err)
    }
)

import router from '@/router';
import { useUserInfoStore } from '@/stores/userInfo';
instance.interceptors.response.use(
    result=>{
        return result.data;
    },
    err=>{
        if (err.response.status === 401) {
            const tokenStore = useTokenStore();
            tokenStore.removeToken();
            const userInfoStore = useUserInfoStore();
            userInfoStore.removeInfo();
        } else {
            ElMessage.error(err.message ? err.message : "server error")
        }
        return Promise.reject(err);
    }
)

export default instance;