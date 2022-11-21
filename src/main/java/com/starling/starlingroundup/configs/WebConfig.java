package com.starling.starlingroundup.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.function.Consumer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    private static final String ACCEPT_HEADER = "Accept";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";

    @Value("${starling.base_url}")
    private String baseUrl;

    @Value("${starling.authentication.access_token}")
    private String token;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Bean
    public WebClient.Builder getWebclientBuilder() {
        return WebClient
                .builder()
                .baseUrl(baseUrl)
                .defaultHeaders(defaultHeadersConsumer())
                .exchangeStrategies(ExchangeStrategies.builder().codecs(c ->
                        c.defaultCodecs().enableLoggingRequestDetails(true)).build()
                );
    }

    private HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.add(ACCEPT_HEADER, APPLICATION_JSON_CONTENT_TYPE);
        headers.add(CONTENT_TYPE_HEADER, APPLICATION_JSON_CONTENT_TYPE);
        headers.add(AUTHORIZATION_HEADER, token);

        return headers;
    }

    private Consumer<HttpHeaders> defaultHeadersConsumer() {
        return httpHeaders -> httpHeaders.addAll(getDefaultHeaders());
    }
}
