package com.yinyang.project.boundary;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/user/logout")
public class LogoutPage {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping
    public boolean logout(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String key = "token:" + token;
        stringRedisTemplate.delete(key);
        return true;
    }
}
