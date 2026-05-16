import request from '@/utils/request.js'

export const getDailyReportService = (size) => {
    return request.get("/report/daily?size=" + size)
}

export const getWeeklyReportService = (size) => {
    return request.get("/report/weekly?size=" + size)
}

export const getMonthlyReportService = (size) => {
    return request.get("/report/monthly?size=" + size)
}