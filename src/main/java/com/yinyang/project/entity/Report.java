package com.yinyang.project.entity;

import com.yinyang.project.DBContext;
import com.yinyang.project.dto.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class Report {
    private TrendDataResponse donationTrend;

    private TrendDataResponse donationCountTrend;

    private List<CategoryReportResponse> topCategories;

    private List<FRAProgressResponse> fraProgress;

    private List<FundRaisingActivity> mostViewedFra;

    private List<FundRaisingActivity> mostShortlistedFra;

    private TrendDataResponse userGrowth;

    private MultiTrendDataResponse userProfileGrowth;

    private DashboardSummaryResponse summary;

    private Range range;

    public enum Range {
        DAILY, WEEKLY, MONTHLY
    }

    public Report getReport(@NotNull Integer size, @NotNull Range range) {
        Report report = new Report();
        report.setRange(range);
        String dateFormat;
        switch (range) {
            case DAILY:
                dateFormat = "%Y-%m-%d";
                break;

            case WEEKLY:
                dateFormat = "%x-W%v";
                break;

            case MONTHLY:
                dateFormat = "%Y-%m";
                break;

            default:
                throw new IllegalArgumentException("Invalid range");
        }

        // Donation Trend
        report.setDonationTrend(this.generateDonationTrend(size, dateFormat));
        // Donation Count Trend
        report.setDonationCountTrend(this.generateDonationCountTrend(size, dateFormat));
        // Top Categories
        report.setTopCategories(this.generateTopCategories());
        // FRA Progress
        report.setFraProgress(this.generateFraProgress());
        // Most Viewed FRA
        report.setMostViewedFra(this.generateMostViewedFra());
        // Most Shortlisted FRA
        report.setMostShortlistedFra(this.generateMostShortlistedFra());
        // User Growth
        report.setUserGrowth(this.generateUserGrowth(size, dateFormat));
        // User With Profile Growth
        report.setUserProfileGrowth(this.generateUserProfilesGrowth(size, dateFormat));
        // Summary
        report.setSummary(this.generateSummary());

        return report;
    }

    public Map<String, BigDecimal> initializeRange(Integer size, String dateFormat) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        switch (dateFormat) {
            case "%Y-%m-%d":
                for (int i = size - 1; i >= 0; i--) {
                    LocalDate date = today.minusDays(i);
                    String label = date.toString();
                    map.put(label, BigDecimal.ZERO);
                }
                break;

            case "%x-W%v":
                WeekFields weekFields = WeekFields.ISO;
                for (int i = size - 1; i >= 0; i--) {
                    LocalDate date = today.minusWeeks(i);
                    int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
                    int year = date.get(weekFields.weekBasedYear());
                    String label = year + "-W" + String.format("%02d", weekNumber);
                    map.put(label, BigDecimal.ZERO);
                }
                break;

            case "%Y-%m":
                for (int i = size - 1; i >= 0; i--) {
                    LocalDate date = today.minusMonths(i);
                    String label = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                    map.put(label, BigDecimal.ZERO);
                }

                break;

            default:
                throw new IllegalArgumentException("Invalid range");
        }

        return map;
    }

    public TrendDataResponse generateDonationTrend(Integer size, String dateFormat) {
        String donationTrendSql =
                "SELECT DATE_FORMAT(created_at, ?) AS label, " +
                "SUM(amount) AS total_amount " +
                "FROM donation " +
                "GROUP BY label " +
                "ORDER BY label";

        Map<String, BigDecimal> donationTrendMap = this.initializeRange(size, dateFormat);

        DBContext.getJdbcTemplate().query(
                donationTrendSql,
                new Object[]{dateFormat},
                rs -> {
                    String label = rs.getString("label");
                    BigDecimal total = BigDecimal.valueOf(rs.getInt("total_amount"));
                    if (donationTrendMap.containsKey(label)) {
                        donationTrendMap.put(label, total);
                    }
                }
        );

        TrendDataResponse donationTrend = new TrendDataResponse();
        donationTrend.setLabels(new ArrayList<>(donationTrendMap.keySet()));
        donationTrend.setValues(new ArrayList<>(donationTrendMap.values()));
        return donationTrend;
    }

    public TrendDataResponse generateDonationCountTrend(Integer size, String dateFormat) {
        String donationCountSql =
                "SELECT DATE_FORMAT(created_at, ?) AS label, " +
                "COUNT(*) AS donation_count " +
                "FROM donation " +
                "GROUP BY label " +
                "ORDER BY label";

        Map<String, BigDecimal> donationCountTrendMap = this.initializeRange(size, dateFormat);

        DBContext.getJdbcTemplate().query(
                donationCountSql,
                new Object[]{dateFormat},
                rs -> {
                    String label = rs.getString("label");
                    BigDecimal count = BigDecimal.valueOf(rs.getInt("donation_count"));
                    if (donationCountTrendMap.containsKey(label)) {
                        donationCountTrendMap.put(label, count);
                    }
                }
        );

        TrendDataResponse donationCountTrend = new TrendDataResponse();
        donationCountTrend.setLabels(new ArrayList<>(donationCountTrendMap.keySet()));
        donationCountTrend.setValues(new ArrayList<>(donationCountTrendMap.values()));

        return donationCountTrend;
    }

    public List<CategoryReportResponse> generateTopCategories() {
        String topCategorySql =
                "SELECT frac.name AS category_name, COUNT(*) AS total " +
                "FROM donation d " +
                "JOIN fund_raising_activity fra ON d.fra_id = fra.id " +
                "JOIN fra_category frac ON fra.category_id = frac.id " +
                "GROUP BY frac.name " +
                "ORDER BY total DESC " +
                "LIMIT 5";

        List<CategoryReportResponse> topCategories = DBContext.getJdbcTemplate().query(
                topCategorySql,
                (rs, rowNum) -> {
                    CategoryReportResponse response = new CategoryReportResponse();

                    response.setCategory(rs.getString("category_name"));

                    response.setCount(rs.getInt("total"));

                    return response;
                }
        );

        return topCategories;
    }

    public List<FRAProgressResponse> generateFraProgress() {
        String fraProgressSql =
                "SELECT title, target_amount, current_amount " +
                "FROM fund_raising_activity " +
                "ORDER BY current_amount DESC " +
                "LIMIT 5";

        List<FRAProgressResponse> fraProgress = DBContext.getJdbcTemplate().query(
                fraProgressSql,
                (rs, rowNum) -> {
                    FRAProgressResponse response = new FRAProgressResponse();

                    response.setFraTitle(rs.getString("title"));

                    response.setTargetAmount(rs.getBigDecimal("target_amount"));

                    response.setCurrentAmount(rs.getBigDecimal("current_amount"));

                    return response;
                }
        );

        return fraProgress;
    }

    public List<FundRaisingActivity> generateMostViewedFra() {
        String mostViewedSql =
                "SELECT * " +
                "FROM fund_raising_activity " +
                "ORDER BY view_count DESC " +
                "LIMIT 5";

        List<FundRaisingActivity> mostViewedFra = DBContext.getJdbcTemplate().query(
                mostViewedSql,
                (rs, rowNum) -> {
                    FundRaisingActivity fundRaisingActivity = new FundRaisingActivity();
                    fundRaisingActivity.setId(rs.getInt("id"));
                    fundRaisingActivity.setTitle(rs.getString("title"));
                    fundRaisingActivity.setDescription(rs.getString("description"));
                    fundRaisingActivity.setViewCount(rs.getInt("view_count"));
                    fundRaisingActivity.setShortlistCount(rs.getInt("shortlist_count"));
                    fundRaisingActivity.setStatus(FundRaisingActivity.Status.valueOf(rs.getString("status")));
                    fundRaisingActivity.setTargetAmount(rs.getBigDecimal("target_amount"));
                    fundRaisingActivity.setCurrentAmount(rs.getBigDecimal("current_amount"));
                    fundRaisingActivity.setCreatedBy(rs.getInt("created_by"));
                    fundRaisingActivity.setCategoryId(rs.getInt("category_id"));
                    fundRaisingActivity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return fundRaisingActivity;
                }
        );

        return mostViewedFra;
    }

    public List<FundRaisingActivity> generateMostShortlistedFra() {
        String mostShortlistedSql =
                "SELECT * " +
                "FROM fund_raising_activity " +
                "ORDER BY shortlist_count DESC " +
                "LIMIT 5";

        List<FundRaisingActivity> mostShortlistedFra = DBContext.getJdbcTemplate().query(
                mostShortlistedSql,
                (rs, rowNum) -> {
                    FundRaisingActivity fundRaisingActivity = new FundRaisingActivity();
                    fundRaisingActivity.setId(rs.getInt("id"));
                    fundRaisingActivity.setTitle(rs.getString("title"));
                    fundRaisingActivity.setDescription(rs.getString("description"));
                    fundRaisingActivity.setViewCount(rs.getInt("view_count"));
                    fundRaisingActivity.setShortlistCount(rs.getInt("shortlist_count"));
                    fundRaisingActivity.setStatus(FundRaisingActivity.Status.valueOf(rs.getString("status")));
                    fundRaisingActivity.setTargetAmount(rs.getBigDecimal("target_amount"));
                    fundRaisingActivity.setCurrentAmount(rs.getBigDecimal("current_amount"));
                    fundRaisingActivity.setCreatedBy(rs.getInt("created_by"));
                    fundRaisingActivity.setCategoryId(rs.getInt("category_id"));
                    fundRaisingActivity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return fundRaisingActivity;
                }
        );

        return mostShortlistedFra;
    }

    public TrendDataResponse generateUserGrowth(Integer size, String dateFormat) {
        String userGrowthSql =
                "SELECT DATE_FORMAT(created_at, ?) AS label, " +
                "COUNT(*) AS total_users " +
                "FROM user_account " +
                "GROUP BY label " +
                "ORDER BY label";

        Map<String, BigDecimal> userGrowthMap = this.initializeRange(size, dateFormat);

        DBContext.getJdbcTemplate().query(
                userGrowthSql,
                new Object[]{dateFormat},
                rs -> {
                    String label = rs.getString("label");
                    BigDecimal totalUsers = BigDecimal.valueOf(rs.getInt("total_users"));
                    if (userGrowthMap.containsKey(label)) {
                        userGrowthMap.put(label, totalUsers);
                    }
                }
        );

        TrendDataResponse userGrowth = new TrendDataResponse();
        userGrowth.setLabels(new ArrayList<>(userGrowthMap.keySet()));
        userGrowth.setValues(new ArrayList<>(userGrowthMap.values()));

        return userGrowth;
    }

    public MultiTrendDataResponse generateUserProfilesGrowth(Integer size, String dateFormat) {
        String sql =
                "SELECT DATE_FORMAT(created_at, ?) AS label, " +
                "user_profile_name, " +
                "COUNT(*) AS total_users " +
                "FROM user_account " +
                "GROUP BY label, user_profile_name " +
                "ORDER BY label";

        Map<String, BigDecimal> baseRange = this.initializeRange(size, dateFormat);
        Map<String, Map<String, BigDecimal>> profileData = new LinkedHashMap<>();

        DBContext.getJdbcTemplate().query(
                sql,
                new Object[]{dateFormat},
                rs -> {
                    String label = rs.getString("label");
                    String profile = rs.getString("user_profile_name");
                    BigDecimal total = BigDecimal.valueOf(rs.getInt("total_users"));
                    profileData.putIfAbsent(profile, new LinkedHashMap<>(baseRange));
                    Map<String, BigDecimal> currentProfileMap = profileData.get(profile);
                    if (currentProfileMap.containsKey(label)) {
                        currentProfileMap.put(label, total);
                    }
                }
        );

        Map<String, List<BigDecimal>> datasets = new LinkedHashMap<>();

        for (String profile : profileData.keySet()) {
            datasets.put(profile, new ArrayList<>(profileData.get(profile).values()));
        }

        MultiTrendDataResponse response = new MultiTrendDataResponse();

        response.setLabels(new ArrayList<>(baseRange.keySet()));

        response.setDatasets(datasets);

        return response;
    }

    public DashboardSummaryResponse generateSummary() {
        String summarySql =
                "SELECT " +
                "(SELECT COALESCE(SUM(amount), 0) FROM donation) AS total_donation_amount, " +
                "(SELECT COUNT(*) FROM user_account) AS total_users, " +
                "(SELECT COUNT(*) FROM fund_raising_activity) AS total_fra, " +
                "(SELECT COUNT(*) FROM donation) AS total_donations";

        DashboardSummaryResponse summary = DBContext.getJdbcTemplate().queryForObject(
                summarySql,
                (rs, rowNum) -> {
                    DashboardSummaryResponse response = new DashboardSummaryResponse();

                    response.setTotalDonationAmount(rs.getBigDecimal("total_donation_amount"));

                    response.setTotalUsers(rs.getInt("total_users"));

                    response.setTotalFra(rs.getInt("total_fra"));

                    response.setTotalDonations(rs.getInt("total_donations"));

                    return response;
                }
        );

        return summary;
    }

}
