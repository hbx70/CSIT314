package com.yinyang.project.controller;

import com.yinyang.project.entity.UserAccount;
import com.yinyang.project.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginController {

    private UserAccount userAccount;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String login(String username, String password) {
        userAccount = new UserAccount();
        String token = userAccount.login(username, password);
        if (token != null) {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            SetOperations<String, String> setOperations = stringRedisTemplate.opsForSet();

            String tokenKey = "token:" + token;
            operations.set(tokenKey, "1", 6, TimeUnit.HOURS);

            String userTokensKey = "user:tokens:id:" + claims.get("id");
            setOperations.add(userTokensKey, token);

            String userProfileTokensKey = "user:tokens:profile:" + claims.get("userProfileId");
            setOperations.add(userProfileTokensKey, token);

            stringRedisTemplate.expire(userTokensKey, 6, TimeUnit.HOURS);
            stringRedisTemplate.expire(userProfileTokensKey, 6, TimeUnit.HOURS);
        }
        return token;
    }
}
