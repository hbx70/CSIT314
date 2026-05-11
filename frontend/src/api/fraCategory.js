import request from '@/utils/request.js'

export const getAllFRACategoriesService = () => {
    return request.get('/fra/category')
}

export const createFRACategoryService = (categoryData) => {
    return request.post("/fra/category/create", categoryData)
}

export const suspendFRACategoryService = (categoryId) => {
    return request.patch("/fra/category/suspend?FRACategoryId=" + categoryId)
}

export const activateFRACategoryService = (categoryId) => {
    return request.patch("/fra/category/activate?FRACategoryId=" + categoryId)
}

export const updateFRACategoryService = (newFRACategoryData) => {
    return request.put("/fra/category/update", newFRACategoryData)
}

export const searchFRACategoriesService = (name, description, status, order) => {
    return request.get("/fra/category/search", {params: {
        name: name,
        description: description,
        status: status,
        order: order
    }})
}