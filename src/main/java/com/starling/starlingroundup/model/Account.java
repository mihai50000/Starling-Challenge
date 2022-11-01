package com.starling.starlingroundup.model;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Account {
    private String accountUid;
    private String accountType;
    private String defaultCategory;
    private String currency;
    private ZonedDateTime createdAt;
    private String name;
}
