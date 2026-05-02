package com.yinyang.project.boundary;

import com.yinyang.project.controller.LoginController;
import com.yinyang.project.utils.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Validated
@RequestMapping("/user/login")
public class LoginPage {

    @Autowired
    private LoginController loginController;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping
    public String login(@Valid @NotBlank String username, @NotBlank String password) {
        String token = loginController.login(username, password);
        if (token != null) {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            SetOperations<String, String> setOperations = stringRedisTemplate.opsForSet();

            String tokenKey = "token:" + token;
            operations.set(tokenKey, "1", 6, TimeUnit.HOURS);

            String userTokensKey = "user:tokens:id:" + claims.get("id");
            setOperations.add(userTokensKey, token);

            String userProfileTokensKey = "user:tokens:profile:" + claims.get("role");
            setOperations.add(userProfileTokensKey, token);

            stringRedisTemplate.expire(userTokensKey, 6, TimeUnit.HOURS);
            stringRedisTemplate.expire(userProfileTokensKey, 6, TimeUnit.HOURS);
        }
        return token;
    }
}
