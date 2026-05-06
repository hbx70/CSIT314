import request from '@/utils/request.js'

export const loginService = (usename, password) => {
    return request.post("/user/login?username=" + usename + "&password=" + password);
}

export const logoutService = () => {
    return request.post("/user/logout")
}

export const getCurrentUserService = () => {
    return request.get("/user/info")
}

export const getAllUserAccountsService = () => {
    return request.get("/user/account")
}

export const suspendUserAccountService = (userAccountId) => {
    return request.patch("/user/account/suspend?userAccountId=" + userAccountId)
}

export const activateUserAccountService = (userAccountId) => {
    return request.patch("/user/account/activate?userAccountId=" + userAccountId)
}

export const createUserAccountService = (userAccountData) => {
    return request.post("/user/account/create", userAccountData)
}

export const updateUserAccountService = (newUserAccountData) => {
    return request.put("/user/account/update", newUserAccountData)
}

export const searchUserAccountsService = (username, email, address, userProfileName, status, order) => {
    return request.get("/user/account/search", {params: {
        username: username,
        email: email,
        address: address,
        userProfileName: userProfileName,
        status: status,
        order: order
    }})
}