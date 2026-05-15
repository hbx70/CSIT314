package com.yinyang.project.entity;

import com.yinyang.project.DBContext;
import com.yinyang.project.dto.FundRaisingActivityResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FavouriteFRA {
    private Integer id;
    private Integer userAccountId;
    private Integer fraId;
    private LocalDateTime createdAt;

    public FavouriteFRA getFavouriteFRAByUserIdAndFRAId(Integer userId, Integer fraId) {
        String sql = "SELECT * FROM favourite_fra WHERE user_account_id = ? and fra_id = ?";
        try {
            return DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{userId, fraId},
                    (rs, rowNum) -> {
                        FavouriteFRA favouriteFRA = new FavouriteFRA();
                        favouriteFRA.setId(rs.getInt("id"));
                        favouriteFRA.setUserAccountId(rs.getInt("user_account_id"));
                        favouriteFRA.setFraId((rs.getInt("fra_id")));
                        favouriteFRA.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        return favouriteFRA;
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean saveFundRaisingActivityToFavourite(Integer currentUserId, @NotNull Integer fundRaisingActivityId) {
        if (this.getFavouriteFRAByUserIdAndFRAId(currentUserId, fundRaisingActivityId) == null) {
            String sql = "INSERT INTO favourite_fra (user_account_id, fra_id, created_at) values (?, ?, ?)";
            DBContext.getJdbcTemplate().update(
                    sql,
                    currentUserId,
                    fundRaisingActivityId,
                    LocalDateTime.now()
            );
            return true;
        }
        return false;
    }

    public boolean unsaveFundRaisingActivityFromFavourite(Integer currentUserId, @NotNull Integer fundRaisingActivityId) {
        if (this.getFavouriteFRAByUserIdAndFRAId(currentUserId, fundRaisingActivityId) != null) {
            String sql = "DELETE FROM favourite_fra WHERE user_account_id = ? AND fra_id = ?";
            DBContext.getJdbcTemplate().update(
                    sql,
                    currentUserId,
                    fundRaisingActivityId
            );
            return true;
        }
        return false;
    }

    public List<FundRaisingActivityResponse> searchFavouriteList(String title, FundRaisingActivity.Status status, Integer categoryId, Integer currentUserId) {
        StringBuilder sql = new StringBuilder(
                "SELECT fra.*, " +
                "ua.username AS creator_name, ua.user_profile_name AS creator_role, ua.status AS creator_account_status, " +
                "frac.name AS category_name, frac.status AS category_status " +
                "FROM favourite_fra fav " +
                "LEFT JOIN fund_raising_activity fra ON fav.fra_id = fra.id " +
                "LEFT JOIN user_account ua ON fra.created_by = ua.id " +
                "LEFT JOIN fra_category frac ON fra.category_id = frac.id " +
                "WHERE 1 = 1"
        );
        List<Object> params = new ArrayList<>();

        sql.append(" AND fav.user_account_id = ?");
        params.add(currentUserId);

        if (title != null && !title.isEmpty()) {
            sql.append(" AND fra.title LIKE ?");
            params.add("%" + title + "%");
        }

        if (categoryId != null) {
            sql.append(" AND fra.category_id = ?");
            params.add(categoryId);
        }

        if (status != null) {
            sql.append(" AND fra.status = ?");
            params.add(status);
        }

        sql.append(" ORDER BY fav.created_at DESC");

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
}
