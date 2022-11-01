package com.starling.starlingroundup.model;

import lombok.Data;

@Data
public class PutMoneyIntoSavingsResponse {
    private String transferUid;
    private boolean success;
    private ErrorDetail[] errors;
}
