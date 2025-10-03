package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.ExceptionHandler;

public enum ErrorCode {

    INVALID_PIN(1001, "Invalid Pin Format"),
    PIN_NOT_FOUND(1002,"Pin not found in Memory"),
    INVALID_CHOICE(1003,"Invalid Choice made"),
    CREATE_ACCOUNT(1004, "Account already exist"),
    ACCOUNT_NOT_FOUND(1005, "Account not found"),
    DEPOSIT_FAILED(1006, "Deposit failed: Account is currently in use. Try again later."),
    INSUFFICIENT_BALANCE(1007, "Insufficient Balance"),
    INVALID_ACCOUNT(1008, "Invalid Account"),
    INVALID_TRANSACTION(1009, "You cannot transfer money to yourself"),
    LOCK_UNAVAILABLE(1010, "Transfer failed: Could not acquire lock");

    private int code;
    private String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
