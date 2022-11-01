package com.starling.starlingroundup.model;

import lombok.Data;

@Data
public class RoundUpGoalResponse {
    private boolean active;
    private RoundUpGoalDetails roundUpGoalDetails;
}
