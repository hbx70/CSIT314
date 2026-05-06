import request from '@/utils/request.js'

export const getAllUserProfilesService = () => {
    return request.get("/user/profile")
}