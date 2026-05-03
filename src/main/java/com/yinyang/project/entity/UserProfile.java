package com.yinyang.project.entity;

import com.yinyang.project.DBContext;
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

    public UserProfile getUserProfileByName(UserProfile.Name name) {
        String sql = "SELECT * FROM user_profile WHERE name = ?";
        try {
            UserProfile userProfile = DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{name.name()},
                    (rs, rowNum) -> {
                        UserProfile user = new UserProfile();
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
        if (this.getUserProfileByName(userProfileData.getName()) == null) {
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
                    userProfile.setName(Name.valueOf(rs.getString("name")));
                    userProfile.setDescription(rs.getString("description"));
                    userProfile.setStatus(Status.valueOf(rs.getString("status")));
                    userProfile.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return userProfile;
                }
        );
    }

    public boolean updateUserProfile(UserProfile newUserProfile) {
        if (this.getUserProfileByName(newUserProfile.getName()) == null) {
            String sql = "UPDATE user_profile SET description = ? WHERE name = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    newUserProfile.getDescription(),
                    newUserProfile.getName().name()
            );
            return row == 1;
        }
        return false;
    }

    public List<UserProfile> searchUserProfiles(Name name, String description, Status status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM user_profile WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null) {
            sql.append(" AND name = ?");
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
                    userProfile.setName(Name.valueOf(rs.getString("name")));
                    userProfile.setDescription(rs.getString("description"));
                    userProfile.setStatus(Status.valueOf(rs.getString("status")));
                    userProfile.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return userProfile;
                }
        );
    }

    public boolean suspendUserProfile(UserProfile.Name userProfileName) {
        UserProfile userProfile = this.getUserProfileByName(userProfileName);
        if (userProfile != null && userProfile.getStatus() != Status.SUSPENDED) {
            String sql = "UPDATE user_profile SET status = 'SUSPENDED' WHERE name = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    userProfileName.name()
            );
            return row == 1;
        }
        return false;
    }

    public boolean activateUserProfile(UserProfile.Name userProfileName) {
        UserProfile userProfile = this.getUserProfileByName(userProfileName);
        if (userProfile != null && userProfile.getStatus() != Status.ACTIVE) {
            String sql = "UPDATE user_profile SET status = 'ACTIVE' WHERE name = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    userProfileName
            );
            return row == 1;
        }
        return false;
    }
}
