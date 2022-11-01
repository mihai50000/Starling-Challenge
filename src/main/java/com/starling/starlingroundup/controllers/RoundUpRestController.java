package com.starling.starlingroundup.controllers;

import com.starling.starlingroundup.customExceptions.HttpInternalServerException;
import com.starling.starlingroundup.customExceptions.HttpNotFoundException;
import com.starling.starlingroundup.model.Account;
import com.starling.starlingroundup.model.PutMoneyIntoSavingsResponse;
import com.starling.starlingroundup.model.RoundUpGoalResponse;
import com.starling.starlingroundup.model.RoundUpWindow;
import com.starling.starlingroundup.service.AccountsService;
import com.starling.starlingroundup.service.RoundUpService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Optional;

@RestController
@RequestMapping("roundup")
@AllArgsConstructor
public class RoundUpRestController {

    private final RoundUpService roundUpService;
    private final AccountsService accountsService;

    @GetMapping
    public ResponseEntity<?> getRoundupAmount(@RequestParam String accountUid) {

        try {
            Optional<Account> possibleAccount = accountsService.getAccount(accountUid);

            if (possibleAccount.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(roundUpService.getRoundUpWindow(possibleAccount.get(), null, null));
        } catch (HttpNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (HttpInternalServerException exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }

    @GetMapping
    @RequestMapping("savings-goal")
    public ResponseEntity<?> getSavingsGoal(@RequestParam String accountUid) {
        try {
            return ResponseEntity.ok(roundUpService.getSavingsGoal(accountUid));
        } catch (HttpNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException exception) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping
    public ResponseEntity<?> addRoundUpToSavingsGoal(@RequestParam String accountUid,
                                                     @RequestParam(required = false) ZonedDateTime minTimestamp,
                                                     @RequestParam(required = false) ZonedDateTime maxTimestamp) {
        try {
            Optional<Account> possibleAccount = accountsService.getAccount(accountUid);

            if (possibleAccount.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Account account = possibleAccount.get();
            RoundUpWindow roundUpWindow = roundUpService.getRoundUpWindow(possibleAccount.get(), minTimestamp, maxTimestamp);
            RoundUpGoalResponse savingsGoal = roundUpService.getSavingsGoal(accountUid);
            PutMoneyIntoSavingsResponse response = roundUpService.addMoneyToSavings(account, savingsGoal.getRoundUpGoalDetails().getRoundUpGoalUid(), roundUpWindow.getMinorUnits());

            return ResponseEntity.ok(response);
        } catch (HttpNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException exception) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
