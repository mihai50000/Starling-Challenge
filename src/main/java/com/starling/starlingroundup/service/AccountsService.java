package com.starling.starlingroundup.service;

import com.starling.starlingroundup.customExceptions.HttpInternalServerException;
import com.starling.starlingroundup.customExceptions.HttpNotFoundException;
import com.starling.starlingroundup.model.Account;
import com.starling.starlingroundup.model.AccountsWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AccountsService extends ApiService {
    private static final String ACCOUNTS_URI = "/accounts";

    public AccountsWrapper getAccounts() {
        try {
            return webClient
                    .get()
                    .uri(ACCOUNTS_URI)
                    .retrieve()
                    .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new HttpInternalServerException(SERVER_ERROR_MESSAGE)))
                    .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new HttpNotFoundException(RESOURCE_NOT_FOUND_MESSAGE)))
                    .bodyToMono(AccountsWrapper.class)
                    .onErrorResume(HttpNotFoundException.class, ex -> Mono.just(new AccountsWrapper(new ArrayList<>())))
                    .block(TIMEOUT);
        } catch (HttpNotFoundException e) {
            return new AccountsWrapper(new ArrayList<>());
        }
    }

    public Optional<Account> getAccount(String accountUid) throws HttpInternalServerException {
        AccountsWrapper accountsWrapper = getAccounts();

        return accountsWrapper
                .getAccounts()
                .stream()
                .filter(account -> account.getAccountUid().equals(accountUid))
                .findFirst();
    }
}
