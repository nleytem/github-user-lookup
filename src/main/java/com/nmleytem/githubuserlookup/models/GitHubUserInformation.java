package com.nmleytem.githubuserlookup.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GitHubUserInformation(
        @JsonProperty("user_name")
        String userName,
        @JsonProperty("display_name")
        String displayName,
        String avatar,
        @JsonProperty("geo_location")
        String geoLocation,
        String email,
        String url,
        @JsonProperty("created_at")
        String createdAt,
        List<GitHubRepoInformation> repos
){}