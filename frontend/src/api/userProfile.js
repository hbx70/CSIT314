import request from '@/utils/request.js'

export const getAllUserProfilesService = () => {
    return request.get("/user/profile")
}

export const suspendUserProfileService = (userProfileName) => {
    return request.patch("/user/profile/suspend?userProfileName=" + userProfileName);
}

export const activateUserProfileService = (userProfileName) => {
    return request.patch("/user/profile/activate?userProfileName=" + userProfileName)
}

export const createUserProfileService = (userProfileData) => {
    return request.post("/user/profile/create", userProfileData)
}

export const updateUserProfileService = (newUserProfileData) => {
    return request.put("/user/profile/update", newUserProfileData)
}

export const searchUserProfileService = (name, description, status, order) => {
    return request.get("/user/profile/search", {params: {
        name: name,
        description: description,
        status: status,
        order: order
    }})
}