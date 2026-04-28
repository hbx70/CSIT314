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
    @NotNull
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotNull
    private Status status;
    @NotNull
    private Role role;
    private LocalDateTime createdAt;

    public enum Status {
        ACTIVE, SUSPENDED
    }

    public enum Role {
        ADMIN, DONEE, FUND_RAISER, PLATFORM_MANAGER
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
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setStatus(Status.valueOf(rs.getString("status")));
                        user.setRole(Role.valueOf(rs.getString("role")));
                        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        return user;
                    }
            );
            return userProfile;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserProfile getUserProfileByEmail(String email) {
        String sql = "SELECT * FROM user_profile WHERE email = ?";
        try {
            UserProfile userProfile = DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{email},
                    (rs, rowNum) -> {
                        UserProfile user = new UserProfile();
                        user.setId(rs.getInt("id"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setStatus(Status.valueOf(rs.getString("status")));
                        user.setRole(Role.valueOf(rs.getString("role")));
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
        if (this.getUserProfileByEmail(userProfileData.getEmail()) == null) {
            userProfileData.setStatus(Status.ACTIVE);
            String sql = "INSERT INTO user_profile (id, name, email, status, role, created_at) VALUES (?, ?, ?, ?, ?, ?)";
            DBContext.getJdbcTemplate().update(
                    sql,
                    userProfileData.getId(),
                    userProfileData.getName(),
                    userProfileData.getEmail(),
                    userProfileData.getStatus().name(),
                    userProfileData.getRole().name(),
                    LocalDateTime.now()
            );
            return true;
        }
        return false;
    }

    public List<UserProfile> getAllUserProfiles(String name, String email, String status, String role) {
        StringBuilder sql = new StringBuilder("SELECT * FROM user_profile WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }

        if (email != null && !email.isEmpty()) {
            sql.append(" AND email LIKE ?");
            params.add("%" + email + "%");
        }

        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status.toUpperCase());
        }

        if (role != null && !role.isEmpty()) {
            sql.append(" AND role = ?");
            params.add(role.toUpperCase());
        }

        return DBContext.getJdbcTemplate().query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setId(rs.getInt("id"));
                    userProfile.setName(rs.getString("name"));
                    userProfile.setEmail(rs.getString("email"));
                    userProfile.setStatus(Status.valueOf(rs.getString("status")));
                    userProfile.setRole(Role.valueOf(rs.getString("role")));
                    userProfile.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return userProfile;
                }
        );
    }

    public boolean updateUserProfile(UserProfile newUserProfile) {
        if (this.getUserProfileByEmail(newUserProfile.getEmail()) == null) {
            String sql = "UPDATE user_profile SET name = ?, email = ?, status = ?, role = ? WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    newUserProfile.getName(),
                    newUserProfile.getEmail(),
                    newUserProfile.getStatus(),
                    newUserProfile.getRole(),
                    newUserProfile.getId()
            );
            return row == 1;
        }
        return false;
    }

    public boolean suspendUserProfile(Integer userProfileId) {
        UserProfile userProfile = this.getUserProfileById(userProfileId);
        if (userProfile != null) {
            if (userProfile.getStatus() != Status.SUSPENDED) {
                String sql = "UPDATE user_profile SET status = 'SUSPENDED' WHERE id = ?";
                DBContext.getJdbcTemplate().update(
                        sql,
                        userProfileId
                );
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean activateUserProfile(Integer userProfileId) {
        UserProfile userProfile = this.getUserProfileById(userProfileId);
        if (userProfile != null) {
            if (userProfile.getStatus() != Status.ACTIVE) {
                String sql = "UPDATE user_profile SET status = 'ACTIVE' WHERE id = ?";
                DBContext.getJdbcTemplate().update(
                        sql,
                        userProfileId
                );
                return true;
            }
            return false;
        }
        return false;
    }
}
