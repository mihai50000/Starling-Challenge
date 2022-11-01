package com.starling.starlingroundup.model;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class FeedItem {
    private String feedItemUid;
    private String categoryUid;
    private CurrencyAndAmount amount;
    private CurrencyAndAmount sourceAmount;
    private String direction;
    private ZonedDateTime updatedAt;
    private ZonedDateTime transactionTime;
    private ZonedDateTime settlementTime;
    private String source;
    private String status;
    private String transactingApplicationUserUid;
    private String counterPartyType;
    private String counterPartyUid;
    private String counterPartyName;
    private String counterPartySubEntityUid;
    private String counterPartySubEntityName;
    private String counterPartySubEntityIdentifier;
    private String counterPartySubEntitySubIdentifier;
    private String reference;
    private String country;
    private String spendingCategory;
    private boolean hasAttachment;
    private boolean hasReceipt;
    private String batchPaymentDetails;
}
