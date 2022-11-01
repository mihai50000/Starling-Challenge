package com.starling.starlingroundup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopUpRequestV2 {
    private CurrencyAndAmount amount;
}
