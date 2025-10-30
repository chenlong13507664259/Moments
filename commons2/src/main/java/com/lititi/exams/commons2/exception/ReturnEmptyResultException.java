package com.lititi.exams.commons2.exception;


public class ReturnEmptyResultException extends RuntimeException {

    private static final long serialVersionUID = 4658192327599362985L;

    public ReturnEmptyResultException(String message) {
        super(message);
    }

    public ReturnEmptyResultException() {}
}
