import request from '@/utils/request.js'

export const getAllFundRaisingActivitiesService = () => {
    return request.get("/fra")
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

export const searchFundRaisingActivitiesService = (title, status, categoryId, order) => {
    return request.get("/fra/search", {params: {
        title: title,
        status: status,
        categoryId: categoryId,
        order: order
    }})
}

export const searchHistoryOfCompletedFRAService = (title, categoryId, order) => {
    return request.get("/fra/search/completed", {params: {
        title: title,
        categoryId: categoryId,
        order: order
    }})
}