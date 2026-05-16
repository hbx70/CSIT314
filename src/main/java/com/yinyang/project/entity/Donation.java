package com.yinyang.project.entity;

import com.yinyang.project.DBContext;
import com.yinyang.project.dto.DonationResponse;
import com.yinyang.project.dto.TopData;
import com.yinyang.project.dto.TopDataResponse;
import com.yinyang.project.dto.TrendDataResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Donation {
    private Integer id;
    private Integer userAccountId;
    @NotNull
    private Integer fraId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Status status;
    private LocalDateTime createdAt;

    public enum Status {
        SUCCESS, CANCELLED
    }

    public DonationResponse getDonationDetailsById(@NotNull Integer donationId) {
        String sql =
                "SELECT d.*, " +
                "fra.title, fra.view_count, fra.shortlist_count, fra.status AS fra_status, fra.target_amount, fra.current_amount " +
                "FROM donation d " +
                "LEFT JOIN fund_raising_activity fra ON d.fra_id = fra.id " +
                "WHERE d.id = ?";
        try {
            return DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{donationId},
                    (rs, rowNum) -> {
                        DonationResponse donationResponse = new DonationResponse();
                        donationResponse.setId(rs.getInt("id"));
                        donationResponse.setUserAccountId(rs.getInt("user_account_id"));
                        donationResponse.setFraId(rs.getInt("fra_id"));
                        donationResponse.setAmount(rs.getBigDecimal(rs.getString("amount")));
                        donationResponse.setStatus(Status.valueOf(rs.getString("status")));
                        donationResponse.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        donationResponse.setTitle(rs.getString("title"));
                        donationResponse.setViewCount(rs.getInt("view_count"));
                        donationResponse.setShortlistCount(rs.getInt("shortlist_count"));
                        donationResponse.setFraStatus(FundRaisingActivity.Status.valueOf(rs.getString("fra_status")));
                        donationResponse.setTargetAmount(rs.getBigDecimal("target_amount"));
                        donationResponse.setCurrentAmount(rs.getBigDecimal("current_amount"));
                        return donationResponse;
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void doneeMakeDonation(@Valid Donation donationData) {
        String sql = "INSERT INTO donation (user_account_id, fra_id, amount, status, created_at) values (?, ?, ?, ?, ?)";
        DBContext.getJdbcTemplate().update(
                sql,
                donationData.getUserAccountId(),
                donationData.getFraId(),
                donationData.getAmount(),
                donationData.getStatus().name(),
                LocalDateTime.now()
        );
    }

    public List<DonationResponse> searchHistoryDonations(Status status, String title, @NotBlank String orderBy, Integer currentUserId) {
        StringBuilder sql = new StringBuilder(
                "SELECT d.*, " +
                "fra.title, fra.view_count, fra.shortlist_count, fra.status AS fra_status, fra.target_amount, fra.current_amount " +
                "FROM donation d " +
                "LEFT JOIN fund_raising_activity fra ON d.fra_id = fra.id " +
                "WHERE 1 = 1"
        );
        List<Object> params = new ArrayList<>();

        sql.append(" AND d.user_account_id = ?");
        params.add(currentUserId);

        if (status != null) {
            sql.append(" AND d.status = ?");
            params.add(status.name());
        }

        if (title != null && !title.isEmpty()) {
            sql.append(" AND fra.title LIKE ?");
            params.add("%" + title + "%");
        }

        String orderCondition = "d.created_at";
        if ("amount".equals(orderBy)) {
            orderCondition = "d.amount";
        }
        sql.append(" ORDER BY ").append(orderCondition).append(" DESC");


        return DBContext.getJdbcTemplate().query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    DonationResponse donationResponse = new DonationResponse();
                    donationResponse.setId(rs.getInt("id"));
                    donationResponse.setUserAccountId(rs.getInt("user_account_id"));
                    donationResponse.setFraId(rs.getInt("fra_id"));
                    donationResponse.setAmount(rs.getBigDecimal(rs.getString("amount")));
                    donationResponse.setStatus(Status.valueOf(rs.getString("status")));
                    donationResponse.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    donationResponse.setTitle(rs.getString("title"));
                    donationResponse.setViewCount(rs.getInt("view_count"));
                    donationResponse.setShortlistCount(rs.getInt("shortlist_count"));
                    donationResponse.setFraStatus(FundRaisingActivity.Status.valueOf(rs.getString("fra_status")));
                    donationResponse.setTargetAmount(rs.getBigDecimal("target_amount"));
                    donationResponse.setCurrentAmount(rs.getBigDecimal("current_amount"));
                    return donationResponse;
                }
        );
    }

    public TrendDataResponse getDonationAmountTrendReport(@NotNull Integer size, Report.@NotNull Range range) {
        String donationTrendSql =
                "SELECT DATE_FORMAT(created_at, ?) AS label, " +
                        "SUM(amount) AS total_amount " +
                        "FROM donation " +
                        "WHERE status = 'SUCCESS' " +
                        "GROUP BY label " +
                        "ORDER BY label";

        Map<String, BigDecimal> donationTrendMap = this.initializeRange(size, range);
        String dateFormat = this.getDateFormat(range);

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

    public TrendDataResponse getDonationCountTrendReport(@NotNull Integer size, Report.@NotNull Range range) {
        String donationCountSql =
                "SELECT DATE_FORMAT(created_at, ?) AS label, " +
                        "COUNT(*) AS donation_count " +
                        "FROM donation " +
                        "WHERE status = 'SUCCESS' " +
                        "GROUP BY label " +
                        "ORDER BY label";

        Map<String, BigDecimal> donationCountTrendMap = this.initializeRange(size, range);
        String dateFormat = this.getDateFormat(range);

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

    public TopDataResponse<TopData> getDonationTopCategoriesReport(@NotNull Integer size, Report.@NotNull Range range) {
        String topCategorySql =
                "SELECT frac.name AS category_name, " +
                        "COUNT(*) AS total " +
                        "FROM donation d " +
                        "JOIN fund_raising_activity fra ON d.fra_id = fra.id " +
                        "JOIN fra_category frac ON fra.category_id = frac.id " +
                        "WHERE d.created_at >= ? AND d.status = 'SUCCESS'" +
                        "GROUP BY frac.name " +
                        "ORDER BY total DESC " +
                        "LIMIT 5";

        LocalDateTime startDate = this.getStartDate(size, range);

        List<TopData> topCategories = DBContext.getJdbcTemplate().query(
                topCategorySql,
                new Object[]{Timestamp.valueOf(startDate)},
                (rs, rowNum) -> {
                    TopData response = new TopData();
                    response.setDataName(rs.getString("category_name"));
                    response.setCount(rs.getInt("total"));
                    return response;
                }
        );

        TopDataResponse<TopData> topDataResponse = new TopDataResponse<>();
        topDataResponse.setTopDataList(topCategories);
        topDataResponse.setStartDate(startDate);
        topDataResponse.setEndDate(LocalDateTime.now());

        return topDataResponse;
    }

    public LocalDateTime getStartDate(Integer size, Report.Range range) {

        LocalDateTime now = LocalDateTime.now();

        switch (range) {

            case DAILY:
                return now.minusDays(size - 1);

            case WEEKLY:
                return now.minusWeeks(size - 1);

            case MONTHLY:
                return now.minusMonths(size - 1);

            default:
                throw new IllegalArgumentException("Invalid range");
        }
    }

    public String getDateFormat(Report.Range range) {
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
        return dateFormat;
    }

    public Map<String, BigDecimal> initializeRange(Integer size, Report.Range range) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        switch (range) {
            case DAILY:
                for (int i = size - 1; i >= 0; i--) {
                    LocalDate date = today.minusDays(i);
                    String label = date.toString();
                    map.put(label, BigDecimal.ZERO);
                }
                break;

            case WEEKLY:
                WeekFields weekFields = WeekFields.ISO;
                for (int i = size - 1; i >= 0; i--) {
                    LocalDate date = today.minusWeeks(i);
                    int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
                    int year = date.get(weekFields.weekBasedYear());
                    String label = year + "-W" + String.format("%02d", weekNumber);
                    map.put(label, BigDecimal.ZERO);
                }
                break;

            case MONTHLY:
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
}
