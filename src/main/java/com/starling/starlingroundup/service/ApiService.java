package com.starling.starlingroundup.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.function.Consumer;

@Service
public abstract class ApiService {
    protected static final String TIMEZONE_ID = "Z";
    protected static final String ACCEPT_HEADER = "Accept";
    protected static final String AUTHORIZATION_HEADER = "Authorization";
    protected static final String CONTENT_TYPE_HEADER = "Content-Type";
    protected static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
    protected static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found";
    protected static final String SERVER_ERROR_MESSAGE = "Server error";
    protected static final Duration TIMEOUT = Duration.ofSeconds(3);

    protected WebClient webClient;

    @Value("${starling.base_url}")
    protected String baseUrl;

    @Value("${starling.authentication.access_token}")
    protected String token;

    @Value("${starling.customer.account_holder_UID}")
    protected String accountHolderUID;

    protected HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.add(ACCEPT_HEADER, APPLICATION_JSON_CONTENT_TYPE);
        headers.add(CONTENT_TYPE_HEADER, APPLICATION_JSON_CONTENT_TYPE);
        headers.add(AUTHORIZATION_HEADER, token);

        return headers;
    }

    protected Consumer<HttpHeaders> defaultHeadersConsumer() {
        return httpHeaders -> httpHeaders.addAll(getDefaultHeaders());
    }

    @PostConstruct
    protected void initializeWebClient() {
        webClient = WebClient
                .builder()
                .baseUrl(baseUrl)
                .defaultHeaders(defaultHeadersConsumer())
                .exchangeStrategies(ExchangeStrategies.builder().codecs(c ->
                        c.defaultCodecs().enableLoggingRequestDetails(true)).build()
                )
                .build();
    }
}
