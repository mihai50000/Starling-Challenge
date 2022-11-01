package com.starling.starlingroundup.customExceptions;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class HttpInternalServerException extends CustomException {
    public HttpInternalServerException(String message) {
        super(message);
    }
}
