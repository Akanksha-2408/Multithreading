package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Repository;

public interface AccountRepo {   //throw exception

    public Integer validateUser();

    public void createAccount();

    void deposit(int pin, double amount) throws InterruptedException;

    void withdraw(int pin, double amount) throws InterruptedException;

    double checkBalance(int pin) throws InterruptedException;

    public void TransferMoney(long fromAcc, long toAcc, double amount) throws InterruptedException;
}
