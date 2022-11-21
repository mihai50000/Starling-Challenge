package com.starling.starlingroundup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    private String accountUid;
    private String accountType;
    private String defaultCategory;
    private String currency;
    private ZonedDateTime createdAt;
    private String name;
}
