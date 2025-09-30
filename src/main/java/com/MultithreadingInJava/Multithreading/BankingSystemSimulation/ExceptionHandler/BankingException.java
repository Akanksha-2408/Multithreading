package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.ExceptionHandler;

//Error code- exception (error code: alphanumeric/ numeric- should not match generic http codes)
//Study and use , Where and why use logs, loggers ?

public class BankingException extends RuntimeException {

    private final int errorCode;

    public BankingException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode.getCode();
    }
}
