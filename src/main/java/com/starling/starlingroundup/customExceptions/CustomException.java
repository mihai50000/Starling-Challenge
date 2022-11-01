package com.starling.starlingroundup.customExceptions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class CustomException extends RuntimeException {
    private final String message;
}
