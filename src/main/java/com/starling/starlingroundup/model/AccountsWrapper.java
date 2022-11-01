package com.starling.starlingroundup.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class AccountsWrapper {
    private List<Account> accounts;
    public AccountsWrapper(List<Account> accounts) {
        this.accounts = accounts;
    }
}
