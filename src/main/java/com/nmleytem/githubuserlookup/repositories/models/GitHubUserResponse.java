package com.nmleytem.githubuserlookup.repositories.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubUserResponse(
        String login,
        String name,
        @JsonProperty("avatar_url")
        String avatarUrl,
        String location,
        String email,
        String url,
        @JsonProperty("created_at")
        String createdAt
) {
}
