package com.nmleytem.githubuserlookup.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubRepoInformation(
        String url,
        String name
) {
}
