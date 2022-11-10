package com.starling.starlingroundup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.starling.starlingroundup.customExceptions.HttpInternalServerException;
import com.starling.starlingroundup.customExceptions.HttpNotFoundException;
import com.starling.starlingroundup.model.Account;
import com.starling.starlingroundup.model.AccountsWrapper;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class AccountsServiceIntegrationTest extends ApiServiceIntegrationTest {
    private static final String ACCOUNTS_URI = "/accounts";
    private static final String MOCK_UID_1 = "UID1";
    private static final String MOCK_UID_2 = "UID2";
    private static final String MOCK_UID_3 = "UID3";
    private static final ZonedDateTime time = ZonedDateTime.parse("2022-11-09T20:41:28.721067Z[UTC]");
    private static final Account mockAccount1 = new Account(MOCK_UID_1, "PERSONAL", "CAT", "GBP", time, "NAME");
    private static final Account mockAccount2 = new Account(MOCK_UID_2, "PERSONAL", "CAT", "USD", time, "NAME");
    private static final AccountsWrapper mockAccountsWrapper = new AccountsWrapper(List.of(mockAccount1, mockAccount2));

    private AccountsService accountsService;

    @BeforeEach
    public void setUp() throws IOException {
        webServer = new MockWebServer();
        webServer.start();
        accountsService = new AccountsService();
        accountsService.setWebclientBuilder(getWebClientBuilder());
    }

    @AfterEach
    public void cleanUp() throws IOException {
        webServer.shutdown();
    }

    @Test
    public void getAccountsHappyFlow() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(getOkMockResponse(mockAccountsWrapper));

        AccountsWrapper receivedAccountsWrapper = accountsService.getAccounts();
        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertEquals(ACCOUNTS_URI, request.getPath());
        assertEquals(0, request.getBody().size());
        assertEquals(mockAccountsWrapper, receivedAccountsWrapper);
    }

    @Test
    public void getAccounts4xxResponse() throws InterruptedException {
        webServer.enqueue(get4xxMockResponse());

        assertThrows(HttpNotFoundException.class, accountsService::getAccounts);
        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertEquals(ACCOUNTS_URI, request.getPath());
        assertEquals(0, request.getBody().size());
    }

    @Test
    public void getAccounts5xxResponse() throws InterruptedException {
        webServer.enqueue(get5xxMockResponse());

        assertThrows(HttpInternalServerException.class, accountsService::getAccounts);
        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertEquals(ACCOUNTS_URI, request.getPath());
        assertEquals(0, request.getBody().size());
    }

    @Test
    public void getAccountHappyFlow() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(getOkMockResponse(mockAccountsWrapper));

        Optional<Account> receivedAccount = accountsService.getAccount(MOCK_UID_1);
        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertEquals(ACCOUNTS_URI, request.getPath());
        assertEquals(0, request.getBody().size());
        assertTrue(receivedAccount.isPresent());
        assertEquals(mockAccount1, receivedAccount.get());
    }

    @Test
    public void getAccountNotFound() throws InterruptedException, JsonProcessingException {
        webServer.enqueue(getOkMockResponse(mockAccountsWrapper));

        Optional<Account> receivedAccount = accountsService.getAccount(MOCK_UID_3);
        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertEquals(ACCOUNTS_URI, request.getPath());
        assertEquals(0, request.getBody().size());
        assertTrue(receivedAccount.isEmpty());
    }

    @Test
    public void getAccount4xxResponse() throws InterruptedException {
        webServer.enqueue(get4xxMockResponse());

        Optional<Account> receivedAccount = accountsService.getAccount(MOCK_UID_1);
        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertEquals(ACCOUNTS_URI, request.getPath());
        assertEquals(0, request.getBody().size());
        assertTrue(receivedAccount.isEmpty());
    }

    @Test
    public void getAccount5xxResponse() throws InterruptedException {
        webServer.enqueue(get5xxMockResponse());

        assertThrows(HttpInternalServerException.class, () -> accountsService.getAccount(MOCK_UID_1));
        RecordedRequest request = webServer.takeRequest();
        assertEquals(GET_METHOD, request.getMethod());
        assertEquals(ACCOUNTS_URI, request.getPath());
        assertEquals(0, request.getBody().size());
    }
}
