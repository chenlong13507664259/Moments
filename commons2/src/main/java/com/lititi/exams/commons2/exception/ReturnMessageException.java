package com.lititi.exams.commons2.exception;


public class ReturnMessageException extends RuntimeException {

    private static final long serialVersionUID = -4055805895986102100L;

    public ReturnMessageException(String message) {
        super(message);
    }

    public ReturnMessageException() {}
}
