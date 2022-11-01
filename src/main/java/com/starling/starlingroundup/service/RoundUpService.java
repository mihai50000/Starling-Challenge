package com.starling.starlingroundup.service;

import com.starling.starlingroundup.customExceptions.HttpInternalServerException;
import com.starling.starlingroundup.customExceptions.HttpNotFoundException;
import com.starling.starlingroundup.model.*;
import com.starling.starlingroundup.utils.Generators;
import com.starling.starlingroundup.utils.Math;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RoundUpService extends ApiService {
    private static final String SAVINGS_GOAL_URI_FORMAT = "/feed/account/%s/round-up";
    private static final String PUT_MONEY_SAVINGS_GOAL_URI_FORMAT = "/account/%s/savings-goals/%s/add-money/%s";

    @Value(value = "${starling.roundup.time_interval:7}")
    private int timeWindowLength;

    private TemporalAmount temporalAmount;
    private final FeedService feedService;

    @Autowired
    public RoundUpService(FeedService feedService) {
        this.feedService = feedService;
    }

    @PostConstruct
    public void initializeParameters() {
        temporalAmount = Duration.ofDays(timeWindowLength);
    }

    public RoundUpWindow getRoundUpWindow(Account account,
                                          @Nullable ZonedDateTime startTime,
                                          @Nullable ZonedDateTime endTime) throws HttpNotFoundException, HttpInternalServerException {
        if (endTime == null) {
            endTime = ZonedDateTime.now(ZoneId.of(TIMEZONE_ID));
        }

        if (startTime == null) {
            startTime = endTime.minus(temporalAmount);
        }

        FeedItems feedItems = feedService.getSettledFeedItems(account.getAccountUid(), startTime, endTime);

        CurrencyAndAmount roundUpCurrencyAndAmount = feedItems.feedItems().stream()
                .reduce(CurrencyAndAmount.getEmpty(account.getCurrency()),
                        (subtotal, feedItem) -> subtotal.add(getFeedItemRoundUp(feedItem)), CurrencyAndAmount::add);

        return new RoundUpWindow(roundUpCurrencyAndAmount, startTime, endTime);
    }

    public RoundUpGoalResponse getSavingsGoal(String accountUid) throws HttpNotFoundException, HttpInternalServerException {
        return webClient.get()
                .uri(String.format(SAVINGS_GOAL_URI_FORMAT, accountUid))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new HttpNotFoundException(RESOURCE_NOT_FOUND_MESSAGE)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new HttpInternalServerException(SERVER_ERROR_MESSAGE)))
                .bodyToMono(RoundUpGoalResponse.class)
                .block(TIMEOUT);
    }

    public PutMoneyIntoSavingsResponse addMoneyToSavings(Account account, String savingsGoalUid, long value)
            throws HttpNotFoundException, HttpInternalServerException {

        TopUpRequestV2 top = new TopUpRequestV2(new CurrencyAndAmount(account.getCurrency(), value));

        return webClient.put()
                .uri(String.format(PUT_MONEY_SAVINGS_GOAL_URI_FORMAT, account.getAccountUid(), savingsGoalUid, Generators.generateRandomUid()))
                .bodyValue(top)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new HttpNotFoundException(RESOURCE_NOT_FOUND_MESSAGE)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new HttpInternalServerException(SERVER_ERROR_MESSAGE)))
                .bodyToMono(PutMoneyIntoSavingsResponse.class)
                .block(TIMEOUT);
    }

    private CurrencyAndAmount getFeedItemRoundUp(FeedItem feedItem) {
        CurrencyAndAmount amount = feedItem.getAmount();
        long newSum = Math.roundLongToNearest100(amount.getMinorUnits()) - amount.getMinorUnits();
        return new CurrencyAndAmount(amount.getCurrency(), newSum);
    }
}
