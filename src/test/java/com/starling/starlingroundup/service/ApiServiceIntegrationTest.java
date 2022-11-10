package com.starling.starlingroundup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ApiServiceIntegrationTest {
    protected static final ObjectMapper mapper = new ObjectMapper();

    private static final int OK_CODE = 200;
    private static final int NOT_FOUND_CODE = 404;
    private static final int BAD_GATEWAY_CODE = 502;
    private static final int BODY_DELAY = 1;
    protected static final String GET_METHOD = "GET";
    protected static final String PUT_METHOD = "PUT";

    static {
        mapper.registerModule(new JavaTimeModule());
    }

    protected MockWebServer webServer;

    protected WebClient.Builder getWebClientBuilder() {
        return WebClient
                .builder()
                .baseUrl(String.valueOf(webServer.url("")))
                .defaultHeader("Authorization", "Bearer token")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json");
    }

    protected MockResponse getOkMockResponse(Object body) throws JsonProcessingException {
        return new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(mapper.writeValueAsString(body))
                .setResponseCode(OK_CODE)
                .setBodyDelay(BODY_DELAY, TimeUnit.SECONDS);
    }

    protected MockResponse get4xxMockResponse() {
        return new MockResponse()
                .setResponseCode(NOT_FOUND_CODE)
                .setBodyDelay(BODY_DELAY, TimeUnit.SECONDS);
    }

    protected MockResponse get5xxMockResponse() {
        return new MockResponse()
                .setResponseCode(BAD_GATEWAY_CODE)
                .setBodyDelay(BODY_DELAY, TimeUnit.SECONDS);
    }

    @BeforeAll
    public static void beforeAll() {
        TimeZone.setDefault(TimeZone.getTimeZone("Z"));
    }
}
