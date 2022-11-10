package com.starling.starlingroundup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starling.starlingroundup.customExceptions.HttpInternalServerException;
import com.starling.starlingroundup.customExceptions.HttpNotFoundException;
import com.starling.starlingroundup.model.*;
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
import java.util.regex.Pattern;

import static java.net.URLDecoder.decode;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class RoundUpServiceIntegrationTest extends ApiServiceIntegrationTest {
    private static final String CURRENCY = "GBP";
    private static final long TOP_UP_AMOUNT = 133;
    private static final String ACCOUNT_UID = "UID";
    private static final String SAVINGS_UID = "SUID";
    private static final String TRANSFER_UID = "TUID";
    private static final String SAVINGS_GOAL_URI = String.format("/feed/account/%s/round-up", ACCOUNT_UID);
    private static final String SAVE_MONEY_URI = String.format("/account/%s/savings-goals/%s/add-money/", ACCOUNT_UID, SAVINGS_UID);
    private static final Pattern SAVE_MONEY_URI_PATTERN = Pattern.compile("^" + SAVE_MONEY_URI + "[\\w]{8}(-[\\w]{4}){3}-[\\w]{12}$");
    private static final Account ACCOUNT = Account.builder().accountUid(ACCOUNT_UID).currency(CURRENCY).build();
    private static final PutMoneyIntoSavingsResponse OK_SAVE_MONEY_RESPONSE = new PutMoneyIntoSavingsResponse(TRANSFER_UID, true, null);
    private static final PutMoneyIntoSavingsResponse FAIL_SAVE_MONEY_RESPONSE = new PutMoneyIntoSavingsResponse(TRANSFER_UID, false, null);

    @Value(value = "${starling.roundup.time_interval:7}")
    private int timeWindowLength;

    private RoundUpService roundUpService;

    @BeforeEach
    public void setUp() throws IOException {
        webServer = new MockWebServer();
        webServer.start();

        FeedService feedService = new FeedService();
        feedService.setWebclientBuilder(getWebClientBuilder());
        feedService.setTimeWindowLength(timeWindowLength);
        feedService.initializeParameters();

        roundUpService = new RoundUpService(feedService);
        roundUpService.setWebclientBuilder(getWebClientBuilder());
        roundUpService.setTimeWindowLength(timeWindowLength);
        roundUpService.initializeParameters();
    }

    @AfterEach
    public void cleanUp() throws IOException {
        webServer.shutdown();
    }

    @Test
    public void testGetSavingsGoalHappyFlow() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(getOkMockResponse(new RoundUpGoalResponse()));
        RoundUpGoalResponse response = roundUpService.getSavingsGoal(ACCOUNT_UID);

        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        String uri = decode(request.getPath(), StandardCharsets.UTF_8);
        assertEquals(SAVINGS_GOAL_URI, uri);
        assertEquals(0, request.getBody().size());
        assertEquals(new RoundUpGoalResponse(), response);
    }

    @Test
    public void testGetSavingsGoal4xx() throws InterruptedException {
        webServer.enqueue(get4xxMockResponse());
        assertThrows(HttpNotFoundException.class, () -> roundUpService.getSavingsGoal(ACCOUNT_UID));

        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        String uri = decode(request.getPath(), StandardCharsets.UTF_8);
        assertEquals(SAVINGS_GOAL_URI, uri);
        assertEquals(0, request.getBody().size());
    }

    @Test
    public void testGetSavingsGoal5xx() throws InterruptedException {
        webServer.enqueue(get5xxMockResponse());
        assertThrows(HttpInternalServerException.class, () -> roundUpService.getSavingsGoal(ACCOUNT_UID));

        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        String uri = decode(request.getPath(), StandardCharsets.UTF_8);
        assertEquals(SAVINGS_GOAL_URI, uri);
        assertEquals(0, request.getBody().size());
    }

    @Test
    public void testAddMoneyToSavingsHappyFlow() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(getOkMockResponse(OK_SAVE_MONEY_RESPONSE));
        PutMoneyIntoSavingsResponse response = roundUpService.addMoneyToSavings(ACCOUNT, SAVINGS_UID, TOP_UP_AMOUNT);
        assertEquals(OK_SAVE_MONEY_RESPONSE, response);

        RecordedRequest request = webServer.takeRequest();
        assertEquals(PUT_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        String uri = decode(request.getPath(), StandardCharsets.UTF_8);
        assertTrue(SAVE_MONEY_URI_PATTERN.matcher(uri).matches());
        TopUpRequestV2 requestBody = mapper.readValue(request.getBody().snapshot().utf8(), TopUpRequestV2.class);
        assertEquals(requestBody, new TopUpRequestV2(new CurrencyAndAmount(ACCOUNT.getCurrency(), TOP_UP_AMOUNT)));
    }

    @Test
    public void testAddMoneyToSavingsFailedTransfer() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(getOkMockResponse(FAIL_SAVE_MONEY_RESPONSE));
        PutMoneyIntoSavingsResponse response = roundUpService.addMoneyToSavings(ACCOUNT, SAVINGS_UID, TOP_UP_AMOUNT);
        assertEquals(FAIL_SAVE_MONEY_RESPONSE, response);

        RecordedRequest request = webServer.takeRequest();
        assertEquals(PUT_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        String uri = decode(request.getPath(), StandardCharsets.UTF_8);
        assertTrue(SAVE_MONEY_URI_PATTERN.matcher(uri).matches());
        TopUpRequestV2 requestBody = mapper.readValue(request.getBody().snapshot().utf8(), TopUpRequestV2.class);
        assertEquals(requestBody, new TopUpRequestV2(new CurrencyAndAmount(ACCOUNT.getCurrency(), TOP_UP_AMOUNT)));
    }

    @Test
    public void testAddMoneyToSavings4xx() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(get4xxMockResponse());
        assertThrows(HttpNotFoundException.class, () -> roundUpService.addMoneyToSavings(ACCOUNT, SAVINGS_UID, TOP_UP_AMOUNT));

        RecordedRequest request = webServer.takeRequest();
        assertEquals(PUT_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        String uri = decode(request.getPath(), StandardCharsets.UTF_8);
        assertTrue(SAVE_MONEY_URI_PATTERN.matcher(uri).matches());
        TopUpRequestV2 requestBody = mapper.readValue(request.getBody().snapshot().utf8(), TopUpRequestV2.class);
        assertEquals(requestBody, new TopUpRequestV2(new CurrencyAndAmount(ACCOUNT.getCurrency(), TOP_UP_AMOUNT)));
    }

    @Test
    public void testAddMoneyToSavings5xx() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(get5xxMockResponse());
        assertThrows(HttpInternalServerException.class, () -> roundUpService.addMoneyToSavings(ACCOUNT, SAVINGS_UID, TOP_UP_AMOUNT));

        RecordedRequest request = webServer.takeRequest();
        assertEquals(PUT_METHOD, request.getMethod());
        assertNotNull(request.getPath());
        String uri = decode(request.getPath(), StandardCharsets.UTF_8);
        assertTrue(SAVE_MONEY_URI_PATTERN.matcher(uri).matches());
        TopUpRequestV2 requestBody = mapper.readValue(request.getBody().snapshot().utf8(), TopUpRequestV2.class);
        assertEquals(requestBody, new TopUpRequestV2(new CurrencyAndAmount(ACCOUNT.getCurrency(), TOP_UP_AMOUNT)));
    }
}
