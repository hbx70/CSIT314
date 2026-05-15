package com.yinyang.project.entity;

import com.yinyang.project.DBContext;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FundRaisingActivity {
    private Integer id;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    private Integer viewCount;
    private Integer shortlistCount;
    private Status status;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;

    private Integer createdBy;
    @NotNull
    private Integer categoryId;
    private LocalDateTime createdAt;

    public enum Status {
        ACTIVE, SUSPENDED, COMPLETED
    }

    public FundRaisingActivity getFundRaisingActivityById(Integer id) {
        String sql = "SELECT * FROM fund_raising_activity where id = ?";
        try {
            return DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{id},
                    (rs, rowNum) -> {
                        FundRaisingActivity fundRaisingActivity = new FundRaisingActivity();
                        fundRaisingActivity.setId(rs.getInt("id"));
                        fundRaisingActivity.setTitle(rs.getString("title"));
                        fundRaisingActivity.setDescription(rs.getString("description"));
                        fundRaisingActivity.setViewCount(rs.getInt("view_count"));
                        fundRaisingActivity.setShortlistCount(rs.getInt("shortlist_count"));
                        fundRaisingActivity.setStatus(Status.valueOf(rs.getString("status")));
                        fundRaisingActivity.setTargetAmount(rs.getBigDecimal("target_amount"));
                        fundRaisingActivity.setCurrentAmount(rs.getBigDecimal("current_amount"));
                        fundRaisingActivity.setCreatedBy(rs.getInt("created_by"));
                        fundRaisingActivity.setCategoryId(rs.getInt("category_id"));
                        fundRaisingActivity.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        return fundRaisingActivity;
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean createFundRaisingActivity(FundRaisingActivity fundRaisingActivityData) {
        FRACategory fraCategory = new FRACategory();
        FRACategory currentCategory = fraCategory.getFRACategoryById(fundRaisingActivityData.getCategoryId());
        boolean isCategoryValid = currentCategory != null && currentCategory.getStatus() == FRACategory.Status.ACTIVE;
        if (isCategoryValid) {
            String sql = "INSERT INTO fund_raising_activity (title, description, view_count, shortlist_count, status, target_amount, current_amount, created_by, category_id, created_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            DBContext.getJdbcTemplate().update(
                    sql,
                    fundRaisingActivityData.getTitle(),
                    fundRaisingActivityData.getDescription(),
                    fundRaisingActivityData.getViewCount(),
                    fundRaisingActivityData.getShortlistCount(),
                    fundRaisingActivityData.getStatus().name(),
                    fundRaisingActivityData.getTargetAmount(),
                    fundRaisingActivityData.getCurrentAmount(),
                    fundRaisingActivityData.getCreatedBy(),
                    fundRaisingActivityData.getCategoryId(),
                    LocalDateTime.now()
            );
            return true;
        }
        return false;
    }

    public List<FundRaisingActivityResponse> getAllOngoingFundRaisingActivities(Integer currentUserId) {
        String sql = "SELECT fra.*, " +
                "ua.username AS creator_name, ua.user_profile_name AS creator_role, ua.status AS creator_account_status, " +
                "frac.name AS category_name, frac.status AS category_status " +
                "FROM fund_raising_activity fra " +
                "LEFT JOIN user_account ua ON fra.created_by = ua.id " +
                "LEFT JOIN fra_category frac ON fra.category_id = frac.id " +
                "WHERE fra.created_by = ? AND fra.status IN ('ACTIVE', 'SUSPENDED') ORDER BY fra.created_at DESC";
        return DBContext.getJdbcTemplate().query(
                sql,
                new Object[]{currentUserId},
                (rs, rowNum) -> {
                    FundRaisingActivityResponse fundRaisingActivityResponse = new FundRaisingActivityResponse();
                    fundRaisingActivityResponse.setId(rs.getInt("id"));
                    fundRaisingActivityResponse.setTitle(rs.getString("title"));
                    fundRaisingActivityResponse.setDescription(rs.getString("description"));
                    fundRaisingActivityResponse.setViewCount(rs.getInt("view_count"));
                    fundRaisingActivityResponse.setShortlistCount(rs.getInt("shortlist_count"));
                    fundRaisingActivityResponse.setStatus(Status.valueOf(rs.getString("status")));
                    fundRaisingActivityResponse.setTargetAmount(rs.getBigDecimal("target_amount"));
                    fundRaisingActivityResponse.setCurrentAmount(rs.getBigDecimal("current_amount"));
                    fundRaisingActivityResponse.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    fundRaisingActivityResponse.setCreatorName(rs.getString("creator_name"));
                    fundRaisingActivityResponse.setCreatorRole(UserProfile.Name.valueOf(rs.getString("creator_role")));
                    fundRaisingActivityResponse.setCreatorAccountStatus(UserAccount.Status.valueOf(rs.getString("creator_account_status")));
                    fundRaisingActivityResponse.setCategoryId(rs.getInt("category_id"));
                    fundRaisingActivityResponse.setCategoryName(rs.getString("category_name"));
                    fundRaisingActivityResponse.setCategoryStatus(FRACategory.Status.valueOf(rs.getString("category_status")));
                    return fundRaisingActivityResponse;
                }
        );
    }

    public boolean updateFundRaisingActivity(FundRaisingActivity newFundRaisingActivityData) {
        FundRaisingActivity fundRaisingActivity = this.getFundRaisingActivityById(newFundRaisingActivityData.getId());
        if (fundRaisingActivity != null && fundRaisingActivity.getStatus() != Status.COMPLETED && fundRaisingActivity.getCurrentAmount().compareTo(newFundRaisingActivityData.getTargetAmount()) < 0) {
            String sql = "UPDATE fund_raising_activity SET title = ?, description = ?, target_amount = ?, category_id = ? WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    newFundRaisingActivityData.getTitle(),
                    newFundRaisingActivityData.getDescription(),
                    newFundRaisingActivityData.getTargetAmount(),
                    newFundRaisingActivityData.getCategoryId(),
                    newFundRaisingActivityData.getId()
            );
            return row == 1;
        }
        return false;
    }

    public List<FundRaisingActivityResponse> searchFundRaisingActivities(String title, Status status, Integer categoryId, @NotBlank String order, Integer currentUserId) {
        StringBuilder sql = new StringBuilder(
                "SELECT fra.*, " +
                "ua.username AS creator_name, ua.user_profile_name AS creator_role, ua.status AS creator_account_status, " +
                "frac.name AS category_name, frac.status AS category_status " +
                "FROM fund_raising_activity fra " +
                "LEFT JOIN user_account ua ON fra.created_by = ua.id " +
                "LEFT JOIN fra_category frac ON fra.category_id = frac.id " +
                "WHERE 1 = 1"
        );

        List<Object> params = new ArrayList<>();

        sql.append(" AND fra.created_by = ?");
        params.add(currentUserId);

        if (title != null && !title.isEmpty()) {
            sql.append(" AND fra.title LIKE ?");
            params.add("%" + title + "%");
        }

        if (status != null) {
            sql.append(" AND fra.status = ?");
            params.add(status.name());
        } else {
            sql.append(" AND fra.status IN ('ACTIVE', 'SUSPENDED')");
        }

        if (categoryId != null) {
            sql.append(" AND fra.category_id = ?");
            params.add(categoryId);
        }

        String orderDirection = "DESC";
        if ("asc".equalsIgnoreCase(order)) {
            orderDirection = "ASC";
        }
        sql.append(" ORDER BY fra.created_at ").append(orderDirection);


        return DBContext.getJdbcTemplate().query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    FundRaisingActivityResponse fundRaisingActivityResponse = new FundRaisingActivityResponse();
                    fundRaisingActivityResponse.setId(rs.getInt("id"));
                    fundRaisingActivityResponse.setTitle(rs.getString("title"));
                    fundRaisingActivityResponse.setDescription(rs.getString("description"));
                    fundRaisingActivityResponse.setViewCount(rs.getInt("view_count"));
                    fundRaisingActivityResponse.setShortlistCount(rs.getInt("shortlist_count"));
                    fundRaisingActivityResponse.setStatus(Status.valueOf(rs.getString("status")));
                    fundRaisingActivityResponse.setTargetAmount(rs.getBigDecimal("target_amount"));
                    fundRaisingActivityResponse.setCurrentAmount(rs.getBigDecimal("current_amount"));
                    fundRaisingActivityResponse.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    fundRaisingActivityResponse.setCreatorName(rs.getString("creator_name"));
                    fundRaisingActivityResponse.setCreatorRole(UserProfile.Name.valueOf(rs.getString("creator_role")));
                    fundRaisingActivityResponse.setCreatorAccountStatus(UserAccount.Status.valueOf(rs.getString("creator_account_status")));
                    fundRaisingActivityResponse.setCategoryId(rs.getInt("category_id"));
                    fundRaisingActivityResponse.setCategoryName(rs.getString("category_name"));
                    fundRaisingActivityResponse.setCategoryStatus(FRACategory.Status.valueOf(rs.getString("category_status")));
                    return fundRaisingActivityResponse;
                }
        );
    }

    public boolean suspendFundRaisingActivity(@NotNull Integer fundRaisingActivityId) {
        FundRaisingActivity fundRaisingActivity = this.getFundRaisingActivityById(fundRaisingActivityId);
        if (fundRaisingActivity != null && fundRaisingActivity.getStatus() == Status.ACTIVE) {
            String sql = "UPDATE fund_raising_activity SET status = 'SUSPENDED' WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    fundRaisingActivityId
            );
            return row == 1;
        }
        return false;
    }

    public boolean activateFundRaisingActivity(@NotNull Integer fundRaisingActivityId) {
        FundRaisingActivity fundRaisingActivity = this.getFundRaisingActivityById(fundRaisingActivityId);
        if (fundRaisingActivity != null && fundRaisingActivity.getStatus() == Status.SUSPENDED) {
            String sql = "UPDATE fund_raising_activity SET status = 'ACTIVE' WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    fundRaisingActivityId
            );
            return row == 1;
        }
        return false;
    }

    public List<FundRaisingActivityResponse> doneeSearchFundRaisingActivities(String title, Integer categoryId, @NotBlank String orderBy) {
        StringBuilder sql = new StringBuilder(
                "SELECT fra.*, " +
                "ua.username AS creator_name, ua.user_profile_name AS creator_role, ua.status AS creator_account_status, " +
                "frac.name AS category_name, frac.status AS category_status " +
                "FROM fund_raising_activity fra " +
                "LEFT JOIN user_account ua ON fra.created_by = ua.id " +
                "LEFT JOIN fra_category frac ON fra.category_id = frac.id " +
                "WHERE 1 = 1"
        );

        List<Object> params = new ArrayList<>();

        sql.append(" AND fra.status = ?");
        params.add("ACTIVE");

        sql.append(" AND frac.status = ?");
        params.add("ACTIVE");

        sql.append(" AND ua.status = ?");
        params.add("ACTIVE");

        if (title != null && !title.isEmpty()) {
            sql.append(" AND fra.title LIKE ?");
            params.add("%" + title + "%");
        }

        if (categoryId != null) {
            sql.append(" AND fra.category_id = ?");
            params.add(categoryId);
        }

        String orderCondition = "fra.created_at";
        if ("viewCount".equals(orderBy)) {
            orderCondition = "fra.view_count";
        }
        if ("shortlistCount".equals(orderBy)) {
            orderCondition = "fra.shortlist_count";
        }
        sql.append(" ORDER BY ").append(orderCondition).append(" DESC");

        return DBContext.getJdbcTemplate().query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    FundRaisingActivityResponse fundRaisingActivityResponse = new FundRaisingActivityResponse();
                    fundRaisingActivityResponse.setId(rs.getInt("id"));
                    fundRaisingActivityResponse.setTitle(rs.getString("title"));
                    fundRaisingActivityResponse.setDescription(rs.getString("description"));
                    fundRaisingActivityResponse.setViewCount(rs.getInt("view_count"));
                    fundRaisingActivityResponse.setShortlistCount(rs.getInt("shortlist_count"));
                    fundRaisingActivityResponse.setStatus(Status.valueOf(rs.getString("status")));
                    fundRaisingActivityResponse.setTargetAmount(rs.getBigDecimal("target_amount"));
                    fundRaisingActivityResponse.setCurrentAmount(rs.getBigDecimal("current_amount"));
                    fundRaisingActivityResponse.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    fundRaisingActivityResponse.setCreatorName(rs.getString("creator_name"));
                    fundRaisingActivityResponse.setCreatorRole(UserProfile.Name.valueOf(rs.getString("creator_role")));
                    fundRaisingActivityResponse.setCreatorAccountStatus(UserAccount.Status.valueOf(rs.getString("creator_account_status")));
                    fundRaisingActivityResponse.setCategoryId(rs.getInt("category_id"));
                    fundRaisingActivityResponse.setCategoryName(rs.getString("category_name"));
                    fundRaisingActivityResponse.setCategoryStatus(FRACategory.Status.valueOf(rs.getString("category_status")));
                    return fundRaisingActivityResponse;
                }
        );
    }

    public FundRaisingActivityResponse viewFundRaisingActivityDetails(@NotNull Integer fundRaisingActivityId) {
        String sql = "UPDATE fund_raising_activity SET view_count = view_count + 1 WHERE id = ?";
        int row = DBContext.getJdbcTemplate().update(
                sql,
                fundRaisingActivityId
        );
        if (row == 1) {
            return this.getFundRaisingActivityDetailsById(fundRaisingActivityId);
        }
        return null;
    }

    public FundRaisingActivityResponse getFundRaisingActivityDetailsById(@NotNull Integer fundRaisingActivityId) {
        String sql = "SELECT fra.*, " +
                "ua.username AS creator_name, ua.user_profile_name AS creator_role, ua.status AS creator_account_status, " +
                "frac.name AS category_name, frac.status AS category_status " +
                "FROM fund_raising_activity fra " +
                "LEFT JOIN user_account ua ON fra.created_by = ua.id " +
                "LEFT JOIN fra_category frac ON fra.category_id = frac.id " +
                "WHERE fra.id = ?";
        try {
            return DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{fundRaisingActivityId},
                    (rs, rowNum) -> {
                        FundRaisingActivityResponse fundRaisingActivityResponse = new FundRaisingActivityResponse();
                        fundRaisingActivityResponse.setId(rs.getInt("id"));
                        fundRaisingActivityResponse.setTitle(rs.getString("title"));
                        fundRaisingActivityResponse.setDescription(rs.getString("description"));
                        fundRaisingActivityResponse.setViewCount(rs.getInt("view_count"));
                        fundRaisingActivityResponse.setShortlistCount(rs.getInt("shortlist_count"));
                        fundRaisingActivityResponse.setStatus(Status.valueOf(rs.getString("status")));
                        fundRaisingActivityResponse.setTargetAmount(rs.getBigDecimal("target_amount"));
                        fundRaisingActivityResponse.setCurrentAmount(rs.getBigDecimal("current_amount"));
                        fundRaisingActivityResponse.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        fundRaisingActivityResponse.setCreatorName(rs.getString("creator_name"));
                        fundRaisingActivityResponse.setCreatorRole(UserProfile.Name.valueOf(rs.getString("creator_role")));
                        fundRaisingActivityResponse.setCreatorAccountStatus(UserAccount.Status.valueOf(rs.getString("creator_account_status")));
                        fundRaisingActivityResponse.setCategoryId(rs.getInt("category_id"));
                        fundRaisingActivityResponse.setCategoryName(rs.getString("category_name"));
                        fundRaisingActivityResponse.setCategoryStatus(FRACategory.Status.valueOf(rs.getString("category_status")));
                        return fundRaisingActivityResponse;
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void saveFundRaisingActivityToFavourite(@NotNull Integer fundRaisingActivityId) {
        String sql = "UPDATE fund_raising_activity SET shortlist_count = shortlist_count + 1 WHERE id = ?";
        DBContext.getJdbcTemplate().update(
                sql,
                fundRaisingActivityId
        );
    }

    public void unsaveFundRaisingActivityFromFavourite(@NotNull Integer fundRaisingActivityId) {
        String sql = "UPDATE fund_raising_activity SET shortlist_count = shortlist_count - 1 WHERE id = ?";
        DBContext.getJdbcTemplate().update(
                sql,
                fundRaisingActivityId
        );
    }

    public Integer getNumberOfFRAViews(@NotNull Integer fundRaisingActivityId) {
        if (this.getFundRaisingActivityById(fundRaisingActivityId) != null) {
            String sql = "SELECT view_count FROM fund_raising_activity WHERE id = ?";
            return DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    Integer.class,
                    fundRaisingActivityId
            );
        }
        return null;
    }

    public Integer getNumberOfFRAShortlists(@NotNull Integer fundRaisingActivityId) {
        if (this.getFundRaisingActivityById(fundRaisingActivityId) != null) {
            String sql = "SELECT shortlist_count FROM fund_raising_activity WHERE id = ?";
            return DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    Integer.class,
                    fundRaisingActivityId
            );
        }
        return null;
    }

    public void doneeMakeDonation(@NotNull BigDecimal newAmount, @NotNull Integer fraId) {
        String sql = "UPDATE fund_raising_activity SET current_amount = ? WHERE id =?";
        DBContext.getJdbcTemplate().update(
                sql,
                newAmount,
                fraId
        );
    }

    public void completeFundRaisingActivity(@NotNull Integer fraId) {
        String sql = "UPDATE fund_raising_activity  SET status = 'COMPLETED' WHERE id = ?";
        DBContext.getJdbcTemplate().update(
                sql,
                fraId
        );
    }
}
