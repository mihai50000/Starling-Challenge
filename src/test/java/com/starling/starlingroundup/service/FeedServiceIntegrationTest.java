package com.starling.starlingroundup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starling.starlingroundup.customExceptions.HttpInternalServerException;
import com.starling.starlingroundup.customExceptions.HttpNotFoundException;
import com.starling.starlingroundup.model.CurrencyAndAmount;
import com.starling.starlingroundup.model.FeedItem;
import com.starling.starlingroundup.model.FeedItems;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Pattern;

import static java.net.URLDecoder.decode;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class FeedServiceIntegrationTest extends ApiServiceIntegrationTest {
    private static final ZonedDateTime TIME_1 = ZonedDateTime.parse("2010-11-09T20:41:28.721067Z[UTC]");
    private static final ZonedDateTime TIME_2 = TIME_1.plusYears(1);
    private static final ZonedDateTime TIME_3 = TIME_2.plusYears(1);

    private static final String CURRENCY = "GBP";
    private static final CurrencyAndAmount amount1 = new CurrencyAndAmount(CURRENCY, 100002);
    private static final CurrencyAndAmount amount2 = new CurrencyAndAmount(CURRENCY, 100000);
    private static final CurrencyAndAmount amount3 = new CurrencyAndAmount(CURRENCY, 127);
    private static final FeedItem ITEM_1 = FeedItem.builder().settlementTime(TIME_1).amount(amount1).build();
    private static final FeedItem ITEM_2 = FeedItem.builder().settlementTime(TIME_2).amount(amount2).build();
    private static final FeedItem ITEM_3 = FeedItem.builder().settlementTime(TIME_3).amount(amount3).build();
    private static final FeedItems FEED_ITEMS = new FeedItems(List.of(ITEM_1, ITEM_2, ITEM_3));
    private static final String ACCOUNT_UID = "UID";
    private static final Pattern URL_PATTERN =
            Pattern.compile("^/feed/account/UID/settled-transactions-between\\?minTransactionTimestamp=(.*)&maxTransactionTimestamp=(.*)$");
    private static final String MIN_TRANSACTION_TIMESTAMP = "minTransactionTimestamp";
    private static final String MAX_TRANSACTION_TIMESTAMP = "maxTransactionTimestamp";

    private FeedService feedService;

    @Value(value = "${starling.roundup.time_interval:7}")
    private int timeWindowLength;

    @BeforeEach
    public void setUp() throws IOException {
        webServer = new MockWebServer();
        webServer.start();
        feedService = new FeedService();
        feedService.setWebclientBuilder(getWebClientBuilder());
        feedService.setTimeWindowLength(timeWindowLength);
        feedService.initializeParameters();
    }

    @AfterEach
    public void cleanUp() throws IOException {
        webServer.shutdown();
    }

    @Test
    public void getSettledFeedItemsHappyFlow() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(getOkMockResponse(FEED_ITEMS));
        FeedItems receivedItems = feedService.getSettledFeedItems(ACCOUNT_UID, TIME_1, TIME_3);

        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        String uri = decode(request.getPath(), StandardCharsets.UTF_8);
        assertTrue(URL_PATTERN.matcher(uri).matches());
        assertEquals(0, request.getBody().size());
        assertEquals(FEED_ITEMS, receivedItems);

        ZonedDateTime minTimestamp = ZonedDateTime.parse(getParameterValueFromUri(uri, MIN_TRANSACTION_TIMESTAMP));
        ZonedDateTime maxTimestamp = ZonedDateTime.parse(getParameterValueFromUri(uri, MAX_TRANSACTION_TIMESTAMP));
        assertEquals(minTimestamp, TIME_1);
        assertEquals(maxTimestamp, TIME_3);
    }

    @Test
    public void getSettledFeedItemsHappyFlowNoMaxTimestamp() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(getOkMockResponse(FEED_ITEMS));
        FeedItems receivedItems = feedService.getSettledFeedItems(ACCOUNT_UID, TIME_1, null);

        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        String uri = decode(request.getPath(), StandardCharsets.UTF_8);
        assertTrue(URL_PATTERN.matcher(uri).matches());
        assertEquals(0, request.getBody().size());
        assertEquals(FEED_ITEMS, receivedItems);

        ZonedDateTime minTimestamp = ZonedDateTime.parse(getParameterValueFromUri(uri, MIN_TRANSACTION_TIMESTAMP));
        ZonedDateTime maxTimestamp = ZonedDateTime.parse(getParameterValueFromUri(uri, MAX_TRANSACTION_TIMESTAMP));
        assertEquals(minTimestamp, TIME_1);
        assertTrue(Duration.between(maxTimestamp, ZonedDateTime.now()).compareTo(Duration.ofMinutes(1)) < 0);
    }

    @Test
    public void getSettledFeedItemsHappyFlowNoMinTimestamp() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(getOkMockResponse(FEED_ITEMS));
        FeedItems receivedItems = feedService.getSettledFeedItems(ACCOUNT_UID, null, TIME_1);

        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        String uri = decode(request.getPath(), StandardCharsets.UTF_8);
        assertTrue(URL_PATTERN.matcher(uri).matches());
        assertEquals(0, request.getBody().size());
        assertEquals(FEED_ITEMS, receivedItems);

        ZonedDateTime minTimestamp = ZonedDateTime.parse(getParameterValueFromUri(uri, MIN_TRANSACTION_TIMESTAMP));
        ZonedDateTime maxTimestamp = ZonedDateTime.parse(getParameterValueFromUri(uri, MAX_TRANSACTION_TIMESTAMP));
        assertTrue(Duration.between(minTimestamp, maxTimestamp.minusDays(timeWindowLength)).compareTo(Duration.ofMinutes(1)) < 0);
        assertEquals(maxTimestamp, TIME_1);
    }

    @Test
    public void getSettledFeedItemsHappyFlowNoTimestamps() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(getOkMockResponse(FEED_ITEMS));
        FeedItems receivedItems = feedService.getSettledFeedItems(ACCOUNT_UID, null, null);

        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        String uri = decode(request.getPath(), StandardCharsets.UTF_8);
        assertTrue(URL_PATTERN.matcher(uri).matches());
        assertEquals(0, request.getBody().size());

        ZonedDateTime minTimestamp = ZonedDateTime.parse(getParameterValueFromUri(uri, MIN_TRANSACTION_TIMESTAMP));
        ZonedDateTime maxTimestamp = ZonedDateTime.parse(getParameterValueFromUri(uri, MAX_TRANSACTION_TIMESTAMP));
        assertTrue(Duration.between(maxTimestamp, ZonedDateTime.now().minusDays(timeWindowLength)).compareTo(Duration.ofMinutes(1)) < 0);
        assertEquals(minTimestamp, maxTimestamp.minusDays(timeWindowLength));
        assertEquals(FEED_ITEMS, receivedItems);
    }

    @Test
    public void getSettledFeedItems4xx() throws InterruptedException {
        webServer.enqueue(get4xxMockResponse());
        assertThrows(HttpNotFoundException.class,
                () -> feedService.getSettledFeedItems(ACCOUNT_UID, TIME_1.minusWeeks(1), TIME_3.plusWeeks(1)));

        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        assertTrue(URL_PATTERN.matcher(request.getPath()).matches());
        System.out.println(request.getPath());
        assertEquals(0, request.getBody().size());
    }

    @Test
    public void getSettledFeedItems5xx() throws InterruptedException {
        webServer.enqueue(get5xxMockResponse());
        assertThrows(HttpInternalServerException.class,
                () -> feedService.getSettledFeedItems(ACCOUNT_UID, TIME_1.minusWeeks(1), TIME_3.plusWeeks(1)));

        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        assertTrue(URL_PATTERN.matcher(request.getPath()).matches());
        System.out.println(request.getPath());
        assertEquals(0, request.getBody().size());
    }

    public String getParameterValueFromUri(String uri, String param) {
        String[] params = uri.split("\\?")[1].split("[=&]");
        assertEquals(0, (params.length & 1));

        for (int index = 0; index < params.length; index += 2) {
            if (params[index].equals(param)) {
                return params[index + 1];
            }
        }

        return null;
    }
}
