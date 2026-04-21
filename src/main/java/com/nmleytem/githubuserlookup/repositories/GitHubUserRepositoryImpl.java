package com.nmleytem.githubuserlookup.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nmleytem.githubuserlookup.exceptions.InternalServerError;
import com.nmleytem.githubuserlookup.exceptions.RateLimitException;
import com.nmleytem.githubuserlookup.exceptions.UserNotFoundException;
import com.nmleytem.githubuserlookup.repositories.models.GitHubUserReposResponse;
import com.nmleytem.githubuserlookup.repositories.models.GitHubUserResponse;
import org.apache.hc.client5.http.cache.CacheResponseStatus;
import org.apache.hc.client5.http.cache.HttpCacheContext;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

/**
 * Repository component responsible for making HTTP requests to the GitHub API.
 * Utilizes an Apache HTTP Client with caching capabilities to optimize requests
 * and avoid rate limiting.
 */
@Component
public class GitHubUserRepositoryImpl implements GitHubUserRepository {
    CloseableHttpClient httpClient;
    ObjectMapper objectMapper =  new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(GitHubUserRepositoryImpl.class);
    public GitHubUserRepositoryImpl(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Fetches the user profile information from the GitHub API.
     *
     * @param username The GitHub username.
     * @return A {@link GitHubUserResponse} containing raw user profile data.
     * @throws UserNotFoundException If the user is not found (404 status).
     * @throws InternalServerError If an unexpected error occurs during the HTTP request.
     */
    public GitHubUserResponse getGitHubUserData(String username) throws RuntimeException {
        HttpGet httpGet = new HttpGet("https://api.github.com/users/" + username);
        httpGet.addHeader("Accept", "application/json");
        HttpCacheContext cacheContext = HttpCacheContext.create();
        try {
            return httpClient.execute(httpGet, cacheContext, response -> {
                handleCacheResponse(response, cacheContext);
                var entity = EntityUtils.toString(response.getEntity());
                return objectMapper.readValue(entity, GitHubUserResponse.class);
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerError("Internal Server Error");
        }
    }

    /**
     * Fetches the list of repositories for a given GitHub user.
     *
     * @param username The GitHub username.
     * @return A {@link GitHubUserReposResponse} containing a list of repositories.
     * @throws InternalServerError If an unexpected error occurs during the HTTP request.
     * @throws UserNotFoundException if the user is not found (404 status)
     */
    public GitHubUserReposResponse getGitHubUserRepos(String username) throws RuntimeException {
        HttpGet httpGet = new HttpGet("https://api.github.com/users/" + username + "/repos");
        httpGet.addHeader("Accept", "application/json");
        HttpCacheContext cacheContext = HttpCacheContext.create();
        try {
            return httpClient.execute(httpGet, cacheContext, response -> {
                handleCacheResponse(response, cacheContext);
                var entity = EntityUtils.toString(response.getEntity());
                GitHubUserReposResponse.RepoInformation[] repos = objectMapper.readValue(entity, GitHubUserReposResponse.RepoInformation[].class);
                return new GitHubUserReposResponse(Arrays.asList(repos));
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerError("Internal server error encountered");
        }
    }

    private void handleCacheResponse(ClassicHttpResponse response, HttpCacheContext cacheContext) {
        if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
            logger.error(response.getReasonPhrase());
            throw new RateLimitException("Rate limited");
        }
        CacheResponseStatus responseStatus = cacheContext.getCacheResponseStatus();
        if (responseStatus == null) {
            responseStatus = CacheResponseStatus.CACHE_MISS;
        }

        switch (responseStatus) {
            case CACHE_HIT:
                logger.info("A response was generated from the cache");
                break;
            case CACHE_MISS:
                logger.info("This response came from GitHub");
                if (response.getCode() == HttpStatus.SC_NOT_FOUND) {
                    throw new UserNotFoundException("User not; found");
                }
            case VALIDATED:
                logger.info("Response was generated from the cache after validating with the server");
                break;
            case FAILURE:
                logger.info("Response was generated from the server after a cache failure");
                break;
            default:
                break;
        }
    }
}
