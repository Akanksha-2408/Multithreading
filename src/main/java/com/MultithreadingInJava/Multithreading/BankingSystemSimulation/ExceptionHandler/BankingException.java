package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.ExceptionHandler;

public class BankingException extends RuntimeException {

    private final ErrorCode errorCode;

    public BankingException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }


    @Override
    public String toString() {
        return "BankingException [" + errorCode.getCode() + "]: " + getMessage();
    }
}