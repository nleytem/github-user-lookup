package com.nmleytem.githubuserlookup.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RateLimitException extends RuntimeException{
    public RateLimitException(String message) {
        super(message);
    }
}
