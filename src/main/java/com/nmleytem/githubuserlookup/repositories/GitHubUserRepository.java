package com.nmleytem.githubuserlookup.repositories;

import com.nmleytem.githubuserlookup.repositories.models.GitHubUserReposResponse;
import com.nmleytem.githubuserlookup.repositories.models.GitHubUserResponse;

public interface GitHubUserRepository {

    GitHubUserResponse getGitHubUserData(String username);

    GitHubUserReposResponse getGitHubUserRepos(String username);
}
