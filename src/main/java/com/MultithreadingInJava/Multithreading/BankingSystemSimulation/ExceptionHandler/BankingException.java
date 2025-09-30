package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.ExceptionHandler;

public class BankingException extends RuntimeException {

    private final int errorCode;

    public BankingException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode.getCode();
    }
}
