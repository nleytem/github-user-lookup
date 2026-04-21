package com.nmleytem.githubuserlookup.controllers;

import com.nmleytem.githubuserlookup.exceptions.RateLimitException;
import com.nmleytem.githubuserlookup.exceptions.UserNotFoundException;
import com.nmleytem.githubuserlookup.models.GitHubUserInformation;
import com.nmleytem.githubuserlookup.services.GitHubUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GitHubUserService userService;

    @Test
    void getUser_Success() throws Exception {
        String username = "testuser";
        GitHubUserInformation userInfo = new GitHubUserInformation(
                "testuser", "Test User", "http://avatar", "Test Location", "test@email.com", "http://url", "Sun, 1 Jan 2023 12:00:00 Z", new ArrayList<>()
        );

        when(userService.getUserData(username)).thenReturn(userInfo);

        mockMvc.perform(get("/users/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_name").value("testuser"))
                .andExpect(jsonPath("$.display_name").value("Test User"));
    }

    @Test
    void getUser_NotFound() throws Exception {
        String username = "nonexistent";
        when(userService.getUserData(username)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/{username}", username))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUser_RateLimited() throws Exception {
        String username = "ratelimiter";

        when(userService.getUserData(username)).thenThrow(new RateLimitException("Rate limited"));

        mockMvc.perform(get("/users/{username}", username))
                .andExpect(status().isForbidden());
    }

}
