import request from '@/utils/request.js'

export const getReportService = (size, range) => {
    return request.get("/report?size=" + size + "&range=" + range)
}