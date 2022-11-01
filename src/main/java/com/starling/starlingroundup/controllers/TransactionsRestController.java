package com.starling.starlingroundup.controllers;

import com.starling.starlingroundup.customExceptions.HttpInternalServerException;
import com.starling.starlingroundup.customExceptions.HttpNotFoundException;
import com.starling.starlingroundup.service.FeedService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("transactions")
@AllArgsConstructor
public class TransactionsRestController {
    private final FeedService feedService;

    @GetMapping
    @RequestMapping
    public ResponseEntity<?> getSettledTransactions(@RequestParam String accountUid,
                                                    @RequestParam(required = false) ZonedDateTime minTransactionTimestamp,
                                                    @RequestParam(required = false) ZonedDateTime maxTransactionTimestamp) {
        if (minTransactionTimestamp != null && maxTransactionTimestamp != null
                && !minTransactionTimestamp.isBefore(maxTransactionTimestamp)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            return ResponseEntity.ok(feedService.getSettledFeedItems(accountUid, minTransactionTimestamp, maxTransactionTimestamp));
        } catch (HttpInternalServerException e) {
            return ResponseEntity.internalServerError().build();
        } catch (HttpNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
