import request from '@/utils/request.js'

export const doneeSearchFundRaisingActiviesService = (pageNum, pageSize, title, categoryId, orderBy) => {
    return request.get("/donee/fra/search", {params: {
        pageNum: pageNum,
        pageSize: pageSize,
        title: title,
        categoryId: categoryId,
        orderBy: orderBy
    }})
}