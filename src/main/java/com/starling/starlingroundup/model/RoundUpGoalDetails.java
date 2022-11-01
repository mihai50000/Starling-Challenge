package com.starling.starlingroundup.model;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class RoundUpGoalDetails {
    private String primaryCategoryUid;
    private String roundUpGoalUid;
    private int roundUpMultiplier;
    private ZonedDateTime activatedAt;
    private String activatedBy;
}
