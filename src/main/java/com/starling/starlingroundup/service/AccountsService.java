package com.starling.starlingroundup.service;

import com.starling.starlingroundup.customExceptions.HttpInternalServerException;
import com.starling.starlingroundup.customExceptions.HttpNotFoundException;
import com.starling.starlingroundup.model.Account;
import com.starling.starlingroundup.model.AccountsWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class AccountsService extends ApiService {
    private static final String ACCOUNTS_URI = "/accounts";

    public AccountsWrapper getAccounts() {
        return webClient
                .get()
                .uri(ACCOUNTS_URI)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new HttpInternalServerException(SERVER_ERROR_MESSAGE)))
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new HttpNotFoundException(RESOURCE_NOT_FOUND_MESSAGE)))
                .bodyToMono(AccountsWrapper.class)
                .block(TIMEOUT);
    }

    public Optional<Account> getAccount(String accountUid) throws HttpInternalServerException {
        AccountsWrapper accountsWrapper;

        try {
            accountsWrapper = getAccounts();
        } catch (HttpNotFoundException e) {
            return Optional.empty();
        }

        return accountsWrapper
                .getAccounts()
                .stream()
                .filter(account -> account.getAccountUid().equals(accountUid))
                .findFirst();
    }
}
