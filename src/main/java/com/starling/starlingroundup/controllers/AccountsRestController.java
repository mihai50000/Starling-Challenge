package com.starling.starlingroundup.controllers;

import com.starling.starlingroundup.customExceptions.HttpNotFoundException;
import com.starling.starlingroundup.model.AccountsWrapper;
import com.starling.starlingroundup.service.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("accounts"))
public class AccountsRestController {
    private final AccountsService accountsService;

    @Autowired
    public AccountsRestController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @GetMapping
    @RequestMapping
    public ResponseEntity<?> getAccounts() {
        try {
            AccountsWrapper accountsWrapper = accountsService.getAccounts();

            return ResponseEntity.ok(accountsWrapper);
        } catch (HttpNotFoundException e) {
          return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ResponseEntity.internalServerError().build();
    }
}
