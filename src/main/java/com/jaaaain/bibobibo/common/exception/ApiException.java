package com.jaaaain.bibobibo.common.exception;

public class ApiException extends GlobalException {

    public ApiException(String message) {
        super(message);
    }
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
