package com.nmleytem.githubuserlookup;

import org.apache.hc.client5.http.cache.HttpCacheContext;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GitHubUserLookupE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CloseableHttpClient httpClient;

    @Test
    void testFullFlow() throws Exception {
        String username = "testuser";
        
        // Mock GitHub User API response
        CloseableHttpResponse userResponse = mock(CloseableHttpResponse.class);
        when(userResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        HttpEntity userEntity = new StringEntity("""
                {
                    "login": "testuser",
                    "name": "Test User",
                    "avatar_url": "http://avatar",
                    "location": "Test Location",
                    "email": "test@email.com",
                    "url": "http://url",
                    "created_at": "2023-01-01T12:00:00Z"
                }
                """, ContentType.APPLICATION_JSON);
        when(userResponse.getEntity()).thenReturn(userEntity);
        when(userResponse.getCode()).thenReturn(200);

        // Mock GitHub Repos API response
        CloseableHttpResponse reposResponse = mock(CloseableHttpResponse.class);
        HttpEntity reposEntity = new StringEntity("""
                [
                    {
                        "url": "http://repo1",
                        "name": "repo1"
                    }
                ]
                """, ContentType.APPLICATION_JSON);
        when(reposResponse.getEntity()).thenReturn(reposEntity);
        when(reposResponse.getCode()).thenReturn(200);

        // Configure mock client to return these responses
        // GitHubUserRepositoryImpl calls httpClient.execute(httpGet, cacheContext)
        when(httpClient.execute(any(ClassicHttpRequest.class), any(HttpCacheContext.class))).thenReturn(userResponse).thenReturn(reposResponse);

        mockMvc.perform(get("/users/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_name").value("testuser"))
                .andExpect(jsonPath("$.display_name").value("Test User"))
                .andExpect(jsonPath("$.created_at").value("Sun, 1 Jan 2023 12:00:00 GMT"))
                .andExpect(jsonPath("$.repos[0].name").value("repo1"));
    }
}
