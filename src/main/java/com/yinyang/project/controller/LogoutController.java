package com.yinyang.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogoutController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String key = "token:" + token;
        stringRedisTemplate.delete(key);
        return true;
    }
}
