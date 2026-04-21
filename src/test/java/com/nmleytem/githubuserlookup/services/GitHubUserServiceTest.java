package com.nmleytem.githubuserlookup.services;

import com.nmleytem.githubuserlookup.exceptions.InternalServerError;
import com.nmleytem.githubuserlookup.exceptions.UserNotFoundException;
import com.nmleytem.githubuserlookup.models.GitHubUserInformation;
import com.nmleytem.githubuserlookup.repositories.GitHubUserRepositoryImpl;
import com.nmleytem.githubuserlookup.repositories.models.GitHubUserReposResponse;
import com.nmleytem.githubuserlookup.repositories.models.GitHubUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitHubUserServiceTest {

    @Mock
    private GitHubUserRepositoryImpl userRepository;

    @InjectMocks
    private GitHubUserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserData_Success() {
        String username = "testuser";
        GitHubUserResponse userResponse = new GitHubUserResponse(
                "testuser", "Test User", "http://avatar", "Test Location", "test@email.com", "http://url", "2023-01-01T12:00:00Z"
        );
        GitHubUserReposResponse reposResponse = new GitHubUserReposResponse(List.of(
                new GitHubUserReposResponse.RepoInformation("http://repo1", "repo1")
        ));

        when(userRepository.getGitHubUserData(username)).thenReturn(userResponse);
        when(userRepository.getGitHubUserRepos(username)).thenReturn(reposResponse);

        GitHubUserInformation result = userService.getUserData(username);

        assertNotNull(result);
        assertEquals("testuser", result.userName());
        assertEquals("Test User", result.displayName());
        assertEquals("Sun, 1 Jan 2023 12:00:00 GMT", result.createdAt()); // Verify RFC_1123_DATE_TIME format
        assertEquals(1, result.repos().size());
        assertEquals("repo1", result.repos().getFirst().name());
    }

    @Test
    void getUserData_UserNotFound() {
        String username = "nonexistent";
        when(userRepository.getGitHubUserData(username)).thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> userService.getUserData(username));
    }

    @Test
    void getUserData_InternalError() {
        String username = "erroruser";
        when(userRepository.getGitHubUserData(username)).thenThrow(new InternalServerError("Internal server error"));

        assertThrows(InternalServerError.class, () -> userService.getUserData(username));
    }
}
