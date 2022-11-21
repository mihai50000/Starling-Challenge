package com.starling.starlingroundup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PutMoneyIntoSavingsResponse {
    private String transferUid;
    private boolean success;
    private ErrorDetail[] errors;
}
