package com.invoice.task.invoicetask.exception;

public class RefundExceededException extends RuntimeException {
    public RefundExceededException(String message) {
        super(message);
    }
}
