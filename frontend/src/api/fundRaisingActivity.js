import request from '@/utils/request.js'

export const getAllFundRaisingActivitiesService = (pageNum, pageSize) => {
    return request.get("/fra?pageNum=" + pageNum + "&pageSize=" + pageSize)
}

export const createFundRaisingActivityService = (fundRaisingActivityData) => {
    return request.post("/fra/create", fundRaisingActivityData)
}

export const suspendFundRaisingActivityService = (fundRaisingActivityId) => {
    return request.patch("/fra/suspend?fundRaisingActivityId=" + fundRaisingActivityId)
}

export const activateFundRaisingActivityService = (fundRaisingActivityId) => {
    return request.patch("/fra/activate?fundRaisingActivityId=" + fundRaisingActivityId)
}

export const updateFundRaisingActivityService = (newFundRaisingActivityData) => {
    return request.put("/fra/update", newFundRaisingActivityData)
}

export const searchFundRaisingActivitiesService = (pageNum, pageSize, title, status, categoryId, order) => {
    return request.get("/fra/search", {params: {
        pageNum: pageNum,
        pageSize: pageSize,
        title: title,
        status: status,
        categoryId: categoryId,
        order: order
    }})
}