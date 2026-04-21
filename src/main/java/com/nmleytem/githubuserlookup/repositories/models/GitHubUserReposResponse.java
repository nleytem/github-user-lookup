package com.nmleytem.githubuserlookup.repositories.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubUserReposResponse(
        List<RepoInformation> repos
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RepoInformation(
            String url,
            String name
    ){}
}
