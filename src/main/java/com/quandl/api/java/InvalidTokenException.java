package com.quandl.api.java;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.http.client.HttpResponseException;

public class InvalidTokenException extends Exception {
    private final String token;
    
    public InvalidTokenException(String token, HttpResponseException e) {
        super("Unable to authenticate with token '"+token+"'", e);
        this.token = checkNotNull(token);
    }
    
    public String getToken() {
        return token;
    }
}
