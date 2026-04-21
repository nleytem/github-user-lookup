package com.nmleytem.githubuserlookup.controllers;

import com.nmleytem.githubuserlookup.models.GitHubUserInformation;
import com.nmleytem.githubuserlookup.services.GitHubUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling GitHub user lookups.
 * Provides endpoints to fetch combined information about a GitHub user
 * including their profile details and a list of their repositories.
 */
@RestController
@Tag(name = "Get GitHub User Information", description="Returns user information and associated repos of a provided github username")
@RequestMapping("/users")
public class UserController {
    GitHubUserService gitHubUserService;

    public UserController(GitHubUserService gitHubUserService) {
        this.gitHubUserService = gitHubUserService;
    }

    /**
     * Retrieves GitHub user information and their repositories.
     *
     * @param username The GitHub username to look up which must conform to GitHub's username validation
     * @return A {@link GitHubUserInformation} object containing the user's profile and repositories.
     * @throws RuntimeException if an error occurs during the lookup or the user is not found.
     */
    @GetMapping("{username}")
    @ResponseStatus(HttpStatus.OK)
    public GitHubUserInformation getUser(
            @PathVariable
            @Pattern(regexp = "^[a-zA-Z0-9\\-]{1,39}+$", message = "Invalid username")
            String username) throws RuntimeException {
        // Spring is stripping out anything after a #,?, and & before storing it to the username
        return gitHubUserService.getUserData(username);
    }


}
