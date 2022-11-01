package com.starling.starlingroundup.customExceptions;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class HttpNotFoundException extends CustomException {
    public HttpNotFoundException(String message) {
        super(message);
    }
}
