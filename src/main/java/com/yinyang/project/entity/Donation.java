package com.yinyang.project.entity;

import com.yinyang.project.DBContext;
import com.yinyang.project.dto.DonationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public List<DonationResponse> searchDonationHistories(Status status, String title, @NotBlank String orderBy, Integer currentUserId) {
        StringBuilder sql = new StringBuilder(
                "SELECT d.*, " +
                "fra.title, fra.view_count, fra.shortlist_count, fra.status AS fra_status, fra.target_amount, fra.current_amount, fra.end_date " +
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
                    donationResponse.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
                    return donationResponse;
                }
        );
    }
}
