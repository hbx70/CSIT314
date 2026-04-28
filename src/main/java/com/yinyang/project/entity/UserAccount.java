package com.yinyang.project.entity;

import com.yinyang.project.DBContext;
import com.yinyang.project.utils.JwtUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class UserAccount {
    private Integer id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private LocalDateTime createdAt;

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
                        userAccount.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        return userAccount;
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean createUserAccount(UserAccount userAccountData) {
        if (this.getUserAccountByUsername(userAccountData.getUsername()) == null) {
            String sql = "INSERT INTO user_account (username, password, created_at) values (?, ?, ?)";
            DBContext.getJdbcTemplate().update(
                    sql,
                    userAccountData.getUsername(),
                    userAccountData.getPassword(),
                    LocalDateTime.now()
            );
            return true;
        }
        return false;
    }

    public boolean updateUserAccount(UserAccount newUserAccountData) {
        if (this.getUserAccountByUsername(newUserAccountData.getUsername()) == null) {
            String sql = "UPDATE user_account SET username = ?, password = ? WHERE id = ?";
            int row = DBContext.getJdbcTemplate().update(
                    sql,
                    newUserAccountData.getUsername(),
                    newUserAccountData.getPassword(),
                    newUserAccountData.getId()
            );
            return row == 1;
        }
        return false;
    }

    public boolean deleteUserAccount(Integer userAccountId) {
        String sql = "DELETE FROM user_account WHERE id = ?";
        int row = DBContext.getJdbcTemplate().update(
                sql,
                userAccountId
        );
        return row == 1;
    }

    public String login(String username, String password) {
        UserAccount userAccount = this.getUserAccountByUsername(username);
        if (userAccount != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(password, userAccount.getPassword())) {
                UserProfile userProfile = new UserProfile();
                UserProfile currentUserProfile = userProfile.getUserProfileById(userAccount.getId());
                if (currentUserProfile.getStatus() == UserProfile.Status.ACTIVE) {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("id", userAccount.getId());
                    claims.put("username", userAccount.getUsername());
                    claims.put("role", currentUserProfile.getRole());

                    // generate JWT token
                    String token = JwtUtil.genToken(claims);

                    // store toke into redis
                    StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
                    ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
                    SetOperations<String, String> setOperations = stringRedisTemplate.opsForSet();

                    String tokenKey = "token:" + token;
                    operations.set(tokenKey, "1", 6, TimeUnit.HOURS);

                    String userTokensKey = "user:tokens:" + userAccount.getId();
                    setOperations.add(userTokensKey, token);

                    stringRedisTemplate.expire(userTokensKey, 6, TimeUnit.HOURS);
                    return token;
                }
                return null;
            }
            return null;
        }
        return null;
    }
}
