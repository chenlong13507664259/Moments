package com.lititi.exams.commons2.exception;


public class LttHttpTimeOutException extends LttException {

    private static final long serialVersionUID = 1L;

    public LttHttpTimeOutException(String message) {
        this.errorMsg = message;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

}
