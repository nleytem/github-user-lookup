package com.nmleytem.githubuserlookup.repositories;

import com.nmleytem.githubuserlookup.exceptions.RateLimitException;
import com.nmleytem.githubuserlookup.exceptions.UserNotFoundException;
import com.nmleytem.githubuserlookup.repositories.models.GitHubUserReposResponse;
import com.nmleytem.githubuserlookup.repositories.models.GitHubUserResponse;
import org.apache.hc.client5.http.cache.HttpCacheContext;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GitHubUserRepositoryImplTest {
    private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    private final GitHubUserRepositoryImpl repository = new GitHubUserRepositoryImpl(httpClient);


    @Test
    void getGitHubUserData_Success() throws IOException {
        String username = "testuser";
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        HttpEntity entity = new StringEntity("""
                {
                    "login": "testuser",
                    "name": "Test User",
                    "created_at": "2023-01-01T12:00:00Z"
                }
                """, ContentType.APPLICATION_JSON);
        
        when(response.getEntity()).thenReturn(entity);
        when(response.getCode()).thenReturn(200);
        when(httpClient.execute(any(ClassicHttpRequest.class), any(HttpCacheContext.class), any(HttpClientResponseHandler.class)))
                .thenAnswer(invocation -> {
                    HttpClientResponseHandler<?> handler = invocation.getArgument(2);
                    return handler.handleResponse(response);
                });

        GitHubUserResponse result = repository.getGitHubUserData(username);

        assertNotNull(result);
        assertEquals("testuser", result.login());
    }

    @Test
    void getGitHubUserData_NotFound() throws IOException {
        String username = "nonexistent";
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        
        when(response.getCode()).thenReturn(404);
        when(httpClient.execute(any(ClassicHttpRequest.class), any(HttpCacheContext.class), any(HttpClientResponseHandler.class)))
                .thenAnswer(invocation -> {
                    HttpClientResponseHandler<?> handler = invocation.getArgument(2);
                    return handler.handleResponse(response);
                });

        assertThrows(UserNotFoundException.class, () -> repository.getGitHubUserData(username));
    }

    @Test
    void getGitHubUserRepos_Success() throws IOException {
        String username = "testuser";
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        HttpEntity entity = new StringEntity("""
                [
                    {
                        "url": "http://repo1",
                        "name": "repo1"
                    }
                ]
                """, ContentType.APPLICATION_JSON);

        when(response.getEntity()).thenReturn(entity);
        when(response.getCode()).thenReturn(200);
        when(httpClient.execute(any(ClassicHttpRequest.class), any(HttpCacheContext.class), any(HttpClientResponseHandler.class)))
                .thenAnswer(invocation -> {
                    HttpClientResponseHandler<?> handler = invocation.getArgument(2);
                    return handler.handleResponse(response);
                });

        GitHubUserReposResponse result = repository.getGitHubUserRepos(username);

        assertNotNull(result);
        assertEquals(1, result.repos().size());
        assertEquals("repo1", result.repos().get(0).name());
    }

    @Test
    void getGitHubUserRepos_NotFound() throws IOException {
        String username = "testuser";
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getCode()).thenReturn(404);
        when(httpClient.execute(any(ClassicHttpRequest.class), any(HttpCacheContext.class),any(HttpClientResponseHandler.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> repository.getGitHubUserRepos(username));
    }

    @Test
    void getGitHubUserRepos_RateLimited() throws IOException {
        String username = "testuser";
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        when(response.getCode()).thenReturn(403);
        when(httpClient.execute(any(ClassicHttpRequest.class), any(HttpCacheContext.class),any(HttpClientResponseHandler.class)))
                .thenThrow(RateLimitException.class);

        assertThrows(RateLimitException.class, () -> repository.getGitHubUserRepos(username));
    }
}
