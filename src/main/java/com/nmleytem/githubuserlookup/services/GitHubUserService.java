package com.nmleytem.githubuserlookup.services;

import com.nmleytem.githubuserlookup.exceptions.InternalServerError;
import com.nmleytem.githubuserlookup.exceptions.UserNotFoundException;
import com.nmleytem.githubuserlookup.models.GitHubRepoInformation;
import com.nmleytem.githubuserlookup.models.GitHubUserInformation;
import com.nmleytem.githubuserlookup.repositories.models.GitHubUserReposResponse;
import com.nmleytem.githubuserlookup.repositories.models.GitHubUserResponse;
import com.nmleytem.githubuserlookup.repositories.GitHubUserRepositoryImpl;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Service class responsible for orchestrating the retrieval of GitHub user data.
 * It combines data from the user profile and the user's repositories into a single response.
 */
@Service
public class GitHubUserService {
    GitHubUserRepositoryImpl gitHubUserRepository;

    public GitHubUserService(GitHubUserRepositoryImpl userRepository) {
        gitHubUserRepository = userRepository;
    }

    /**
     * Retrieves and aggregates GitHub user profile data and their repositories.
     * Formats the user's creation date to RFC_1123_DATE_TIME format.
     *
     * @param username The GitHub username to search for.
     * @return A {@link GitHubUserInformation} containing aggregated user data.
     * @throws UserNotFoundException If the GitHub user does not exist.
     * @throws InternalServerError If there is an issue communicating with the GitHub API.
     * @throws com.nmleytem.githubuserlookup.exceptions.RateLimitException if GitHub returns a 403.
     */
    public GitHubUserInformation getUserData(String username) throws RuntimeException {
        GitHubUserResponse userResponse = gitHubUserRepository.getGitHubUserData(username);
        GitHubUserReposResponse reposResponse = gitHubUserRepository.getGitHubUserRepos(username);
        var repos = reposResponse
                .repos()
                .stream()
                .map(
                        it -> new GitHubRepoInformation(it.url(), it.name())
                ).toList();
        OffsetDateTime date = OffsetDateTime.parse(userResponse.createdAt(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String formattedDate = date.format(DateTimeFormatter.RFC_1123_DATE_TIME);

        // Custom serialization could be preferable here. Or a builder.
        return new GitHubUserInformation(
                userResponse.login(),
                userResponse.name(),
                userResponse.avatarUrl(),
                userResponse.location(),
                userResponse.email(),
                userResponse.url(),
                formattedDate,
                repos
        );
    }


}
