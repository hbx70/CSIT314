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


import { useUserInfoStore } from '@/stores/userInfo';
import router from '@/router'
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
            router.push('/login')
            ElMessage.error("Please Login")
        } else {
            ElMessage.error("Operation failure")
        }
        return Promise.reject(err);
    }
)

export default instance;