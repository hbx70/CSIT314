import request from '@/utils/request.js'

export const getAllUserAccountsService = () => {
    return request.get("/user/account")
}