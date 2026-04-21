package com.nmleytem.githubuserlookup.common.http.config;

import org.apache.hc.client5.http.impl.cache.CacheConfig;
import org.apache.hc.client5.http.impl.cache.CachingHttpClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("http-client")
public class HttpClientConfig {

    private int maxCacheEntries;
    private int maxObjectSize;
    private String userAgent;

    public HttpClientConfig(){}

    @Bean
    public CloseableHttpClient getHttpClient() {
        CacheConfig cacheConfig = CacheConfig.custom()
                .setMaxCacheEntries(maxCacheEntries)
                .setMaxObjectSize(maxObjectSize)
                .build();

        return CachingHttpClients.custom()
                .setCacheConfig(cacheConfig)
                .setUserAgent(userAgent)
                .build();
    }

    public void setCacheMaxEntries(int maxCacheEntries) {
        this.maxCacheEntries = maxCacheEntries;
    }

    public void setCacheMaxObjectSize(int maxObjectSize) {
        this.maxObjectSize = maxObjectSize;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
