package com.yinyang.project.entity;

import com.yinyang.project.DBContext;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserProfile {
    private Integer id;
    @NotNull
    private Name name;
    @NotBlank
    private String description;
    private Status status;
    private LocalDateTime createdAt;

    public enum Name {
        ADMIN, DONEE, FUND_RAISER, PLATFORM_MANAGER
    }

    public enum Status {
        ACTIVE, SUSPENDED
    }

    public UserProfile getUserProfileById(Integer id) {
        String sql = "SELECT * FROM user_profile WHERE id = ?";
        try {
            UserProfile userProfile = DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{id},
                    (rs, rowNum) -> {
                        UserProfile user = new UserProfile();
                        user.setId(rs.getInt("id"));
                        user.setName(Name.valueOf(rs.getString("name")));
                        user.setDescription(rs.getString("description"));
                        user.setStatus(Status.valueOf(rs.getString("status")));
                        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        return user;
                    }
            );
            return userProfile;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserProfile getUserProfileByName(String name) {
        String sql = "SELECT * FROM user_profile WHERE name = ?";
        try {
            UserProfile userProfile = DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{name},
                    (rs, rowNum) -> {
                        UserProfile user = new UserProfile();
                        user.setId(rs.getInt("id"));
                        user.setName(Name.valueOf(rs.getString("name")));
                        user.setDescription(rs.getString("description"));
                        user.setStatus(Status.valueOf(rs.getString("status")));
                        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        return user;
                    }
            );
            return userProfile;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean createUserProfile(UserProfile userProfileData) {
        if (this.getUserProfileByName(userProfileData.getName().name()) == null) {
            String sql = "INSERT INTO user_profile (name, description, status, created_at) VALUES (?, ?, ?, ?)";
            DBContext.getJdbcTemplate().update(
                    sql,
                    userProfileData.getName().name(),
                    userProfileData.getDescription(),
                    userProfileData.getStatus().name(),
                    LocalDateTime.now()
            );
            return true;
        }
        return false;
    }

    public List<UserProfile> getAllUserProfiles() {
        String sql = "SELECT * FROM user_profile";
        return DBContext.getJdbcTemplate().query(
                sql,
                (rs, rowNum) -> {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setId(rs.getInt("id"));
                    userProfile.setName(Name.valueOf(rs.getString("name")));
                    userProfile.setDescription(rs.getString("description"));
                    userProfile.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return userProfile;
                }
        );
    }

    public boolean updateUserProfile(UserProfile newUserProfile) {
        if (this.getUserProfileByName(newUserProfile.getName().name()) == null) {
            String sql = "UPDATE user_profile SET name = ?, description = ? WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    newUserProfile.getName().name(),
                    newUserProfile.getDescription(),
                    newUserProfile.getId()
            );
            return row == 1;
        }
        return false;
    }

    public List<UserProfile> searchUserProfile(Name name, String description, Status status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM user_profile WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null) {
            sql.append(" AND status = ?");
            params.add(name.name());
        }

        if (description != null && !description.isEmpty()) {
            sql.append(" AND description LIKE ?");
            params.add("%" + description + "%");
        }

        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.name());
        }

        return DBContext.getJdbcTemplate().query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setId(rs.getInt("id"));
                    userProfile.setName(Name.valueOf(rs.getString("name")));
                    userProfile.setDescription(rs.getString("description"));
                    userProfile.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return userProfile;
                }
        );
    }

    public boolean suspendUserProfile(Integer userProfileId) {
        UserProfile userProfile = this.getUserProfileById(userProfileId);
        if (userProfile != null && userProfile.getStatus() != Status.SUSPENDED) {
            String sql = "UPDATE user_profile SET status = 'SUSPENDED' WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    userProfileId
            );
            return row == 1;
        }
        return false;
    }

    public boolean activateUserProfile(Integer userProfileId) {
        UserProfile userProfile = this.getUserProfileById(userProfileId);
        if (userProfile != null && userProfile.getStatus() != Status.ACTIVE) {
            String sql = "UPDATE user_profile SET status = 'ACTIVE' WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    userProfileId
            );
            return row == 1;
        }
        return false;
    }
}
