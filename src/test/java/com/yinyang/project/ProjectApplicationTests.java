package com.yinyang.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginSuccess() throws Exception {
        String token = performLogin("validUser", "123456");
        assertNotNull(token);
    }

    @Test
    void loginWrongPassword() throws Exception {
        String token = performLogin("validUser2", "wrongPassword");
        assertTrue(token.isEmpty());
    }

    @Test
    void loginUserNotFound() throws Exception {
        String token = performLogin("notExistUser", "123456");
        assertTrue(token.isEmpty());
    }

    @Test
    void loginAccountSuspended() throws Exception {
        String token = performLogin("suspendedUser", "123456");
        assertTrue(token.isEmpty());
    }

    @Test
    void loginRoleSuspended() throws Exception {
        String token = performLogin("roleSuspendedUser", "123456");
        assertTrue(token.isEmpty());
    }

    private String performLogin(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/user/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andReturn();

        return result.getResponse().getContentAsString();
    }
}
