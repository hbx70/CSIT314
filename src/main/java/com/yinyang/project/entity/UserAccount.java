package com.yinyang.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yinyang.project.DBContext;
import com.yinyang.project.dto.MultiTrendDataResponse;
import com.yinyang.project.dto.TrendDataResponse;
import com.yinyang.project.utils.JwtUtil;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;

@Setter
@Getter
public class UserAccount {
    private Integer id;
    @NotBlank
    @Size(min = 3, max = 16)
    private String username;
    @NotBlank
    @Size(min = 6)
    private String password;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String address;
    @NotNull
    private UserProfile.Name userProfileName;
    private Status status;
    private LocalDateTime createdAt;

    public enum Status {
        ACTIVE, SUSPENDED
    }

    public UserAccount getUserAccountById(Integer userAccountId) {
        String sql = "SELECT * FROM user_account where id = ?";
        try {
            return DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{userAccountId},
                    (rs, rowNum) -> {
                        UserAccount userAccount = new UserAccount();
                        userAccount.setId(rs.getInt("id"));
                        userAccount.setUsername(rs.getString("username"));
                        userAccount.setPassword(rs.getString("password"));
                        userAccount.setEmail(rs.getString("email"));
                        userAccount.setAddress(rs.getString("address"));
                        userAccount.setStatus(Status.valueOf(rs.getString("status")));
                        userAccount.setUserProfileName(UserProfile.Name.valueOf(rs.getString("user_profile_name")));
                        userAccount.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        return userAccount;
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UserAccount getUserAccountByUsername(String username) {
        String sql = "SELECT * FROM user_account where username = ?";
        try {
            return DBContext.getJdbcTemplate().queryForObject(
                    sql,
                    new Object[]{username},
                    (rs, rowNum) -> {
                        UserAccount userAccount = new UserAccount();
                        userAccount.setId(rs.getInt("id"));
                        userAccount.setUsername(rs.getString("username"));
                        userAccount.setPassword(rs.getString("password"));
                        userAccount.setEmail(rs.getString("email"));
                        userAccount.setAddress(rs.getString("address"));
                        userAccount.setStatus(Status.valueOf(rs.getString("status")));
                        userAccount.setUserProfileName(UserProfile.Name.valueOf(rs.getString("user_profile_name")));
                        userAccount.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        return userAccount;
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean isUsernameTakenByOthers(String username, int id) {
        String sql = "SELECT COUNT(*) FROM user_account WHERE username = ? AND id != ?";
        Integer count = DBContext.getJdbcTemplate().queryForObject(
                sql,
                Integer.class,
                username,
                id
        );
        return count != null && count > 0;
    }

    public boolean isEmailTakenByOthers(String email, int id) {
        String sql = "SELECT COUNT(*) FROM user_account WHERE email = ? AND id != ?";
        Integer count = DBContext.getJdbcTemplate().queryForObject(
                sql,
                Integer.class,
                email,
                id
        );
        return count != null && count > 0;
    }

    public boolean isUserAccountExists(String username, String email) {
        String sql = "SELECT COUNT(*) FROM user_account WHERE username = ? OR email = ?";
        Integer count = DBContext.getJdbcTemplate().queryForObject(
                sql,
                Integer.class,
                username,
                email
        );
        return count != null && count > 0;
    }

    public boolean createUserAccount(UserAccount userAccountData) {
        if (!this.isUserAccountExists(userAccountData.getUsername(), userAccountData.getEmail())) {
            String sql = "INSERT INTO user_account (username, password, email, address, status, user_profile_name, created_at) values (?, ?, ?, ?, ?, ?, ?)";
            DBContext.getJdbcTemplate().update(
                    sql,
                    userAccountData.getUsername(),
                    userAccountData.getPassword(),
                    userAccountData.getEmail(),
                    userAccountData.getAddress(),
                    userAccountData.getStatus().name(),
                    userAccountData.getUserProfileName().name(),
                    LocalDateTime.now()
            );
            return true;
        }
        return false;
    }

    @JsonIgnore
    public List<UserAccount> getAllUserAccounts() {
        String sql = "SELECT * FROM user_account ORDER BY created_at DESC";
        return DBContext.getJdbcTemplate().query(
                sql,
                (rs, rowNum) -> {
                    UserAccount userAccount = new UserAccount();
                    userAccount.setId(rs.getInt("id"));
                    userAccount.setUsername(rs.getString("username"));
                    userAccount.setPassword(null);
                    userAccount.setAddress(rs.getString("address"));
                    userAccount.setEmail(rs.getString("email"));
                    userAccount.setStatus(Status.valueOf(rs.getString("status")));
                    userAccount.setUserProfileName(UserProfile.Name.valueOf(rs.getString("user_profile_name")));
                    userAccount.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return userAccount;
                }
        );
    }


    public boolean updateUserAccount(UserAccount newUserAccountData) {
        if (!this.isUsernameTakenByOthers(newUserAccountData.getUsername(), newUserAccountData.getId()) && !this.isEmailTakenByOthers(newUserAccountData.getEmail(), newUserAccountData.getId())) {
            String sql = "UPDATE user_account SET username = ?, password = ?, email = ?, address = ?, user_profile_name = ? WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    newUserAccountData.getUsername(),
                    newUserAccountData.getPassword(),
                    newUserAccountData.getEmail(),
                    newUserAccountData.getAddress(),
                    newUserAccountData.getUserProfileName().name(),
                    newUserAccountData.getId()
            );
            return row == 1;
        }
        return false;
    }

    public List<UserAccount> searchUserAccounts(String username, String email, String address, UserProfile.Name userProfileName, Status status, String order) {
        StringBuilder sql = new StringBuilder("SELECT * FROM user_account WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (username != null && !username.isEmpty()) {
            sql.append(" AND username LIKE ?");
            params.add("%" + username + "%");
        }

        if (email != null && !email.isEmpty()) {
            sql.append(" AND email LIKE ?");
            params.add("%" + email + "%");
        }

        if (address != null && !address.isEmpty()) {
            sql.append(" AND address LIKE ?");
            params.add("%" + address + "%");
        }

        if (userProfileName != null) {
            sql.append(" AND user_profile_name = ? ");
            params.add(userProfileName.name());
        }

        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.name());
        }

        String orderDirection = "DESC";
        if ("asc".equalsIgnoreCase(order)) {
            orderDirection = "ASC";
        }
        sql.append(" ORDER BY created_at ").append(orderDirection);

        return DBContext.getJdbcTemplate().query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    UserAccount userAccount = new UserAccount();
                    userAccount.setId(rs.getInt("id"));
                    userAccount.setUsername(rs.getString("username"));
                    userAccount.setPassword(null);
                    userAccount.setEmail(rs.getString("email"));
                    userAccount.setAddress(rs.getString("address"));
                    userAccount.setStatus(Status.valueOf(rs.getString("status")));
                    userAccount.setUserProfileName(UserProfile.Name.valueOf(rs.getString("user_profile_name")));
                    userAccount.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return userAccount;
                }
        );
    }

    public boolean suspendUserAccount(Integer userAccountId) {
        UserAccount userAccount = this.getUserAccountById(userAccountId);
        if (userAccount != null && userAccount.getStatus() != Status.SUSPENDED) {
            String sql = "UPDATE user_account SET status = 'SUSPENDED' WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    userAccountId
            );
            return row == 1;
        }
        return false;
    }

    public boolean activateUserAccount(Integer userAccountId) {
        UserAccount userAccount = this.getUserAccountById(userAccountId);
        if (userAccount != null && userAccount.getStatus() != Status.ACTIVE) {
            String sql = "UPDATE user_account SET status = 'ACTIVE' WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    userAccountId
            );
            return row == 1;
        }
        return false;
    }

    public String login(String username, String password) {
        UserAccount userAccount = this.getUserAccountByUsername(username);
        if (userAccount != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(password, userAccount.getPassword())) {
                UserProfile userProfile = new UserProfile();
                UserProfile currentUserProfile = userProfile.getUserProfileByName(userAccount.getUserProfileName());
                if (userAccount.getStatus() == Status.ACTIVE && currentUserProfile.getStatus() == UserProfile.Status.ACTIVE) {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("id", userAccount.getId());
                    claims.put("username", userAccount.getUsername());
                    claims.put("role", currentUserProfile.getName().name());
                    // generate JWT token
                    return JwtUtil.genToken(claims);
                }
                return null;
            }
            return null;
        }
        return null;
    }
}
