import request from '@/utils/request.js'

export const doneeSearchFundRaisingActiviesService = (title, categoryId, orderBy) => {
    return request.get("/donee/fra/search", {params: {
        title: title,
        categoryId: categoryId,
        orderBy: orderBy
    }})
}

export const doneeViewDetailsOfFundRaisingActivityService = (fundRaisingActivityId) => {
    return request.get("/donee/fra/detail?fundRaisingActivityId=" + fundRaisingActivityId);
}

export const doneeMakeDonationService = (donationData) => {
    return request.post("/donee/donation/make", donationData)
}

export const doneeSaveFRAToFavouriteListService = (fundRaisingActivityId) => {
    return request.post("/donee/fra/save?fundRaisingActivityId=" + fundRaisingActivityId);
}

export const doneeUnsaveFRAFromFavouriteListService = (fundRaisingActivityId) => {
    return request.delete("/donee/fra/unsave?fundRaisingActivityId=" + fundRaisingActivityId);
}

export const doneeSearchFavouriteListService = (title, status, categoryId) => {
    return request.get("/donee/favourite/search", {params: {
        title: title,
        status: status,
        categoryId: categoryId
    }});
}

export const doneeSearchHistoryDonationsService = (title, status, orderBy) => {
    return request.get("/donee/donation/search", {params: {
        status: status,
        title: title,
        orderBy: orderBy
    }})
}