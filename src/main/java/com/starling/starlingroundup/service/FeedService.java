package com.starling.starlingroundup.service;

import com.starling.starlingroundup.customExceptions.HttpInternalServerException;
import com.starling.starlingroundup.customExceptions.HttpNotFoundException;
import com.starling.starlingroundup.model.FeedItems;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;

@Service
public class FeedService extends ApiService {
    private static final String SETTLED_FEED_ITEMS_URI_FORMAT = "/feed/account/%s/settled-transactions-between?minTransactionTimestamp=%s&&maxTransactionTimestamp=%s";

    @Value(value = "${starling.roundup.time_interval:7}")
    @Setter
    private int timeWindowLength;

    private TemporalAmount temporalAmount;

    @PostConstruct
    public void initializeParameters() {
        temporalAmount = Duration.ofDays(timeWindowLength);
    }

    public FeedItems getSettledFeedItems(String accountUid,
                                         @Nullable ZonedDateTime minTimestamp,
                                         @Nullable ZonedDateTime maxTimestamp) throws HttpNotFoundException, HttpInternalServerException {

        if (maxTimestamp == null) {
            maxTimestamp = ZonedDateTime.now(ZoneId.of(TIMEZONE_ID));
        }

        if (minTimestamp == null) {
            minTimestamp = maxTimestamp.minus(temporalAmount);
        }

        if (minTimestamp.isAfter(maxTimestamp)) {
            throw new RuntimeException();
        }

        return webClient.get()
                .uri(String.format(SETTLED_FEED_ITEMS_URI_FORMAT, accountUid, minTimestamp, maxTimestamp))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new HttpNotFoundException(RESOURCE_NOT_FOUND_MESSAGE)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new HttpInternalServerException(SERVER_ERROR_MESSAGE)))
                .bodyToMono(FeedItems.class)
                .block(TIMEOUT);
    }
}
