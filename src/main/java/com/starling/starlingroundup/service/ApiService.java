package com.starling.starlingroundup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
public abstract class ApiService {
    protected static final String TIMEZONE_ID = "Z";
    protected static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found";
    protected static final String SERVER_ERROR_MESSAGE = "Server error";
    protected static final Duration TIMEOUT = Duration.ofSeconds(3);

    protected WebClient webClient;

    @Value("${starling.customer.account_holder_UID}")
    protected String accountHolderUID;

    @Autowired
    public void setWebclientBuilder(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
}
