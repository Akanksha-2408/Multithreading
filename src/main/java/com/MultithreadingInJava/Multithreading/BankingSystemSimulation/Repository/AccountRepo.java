package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Repository;

public interface AccountRepo {

    public Integer validateUser();

    public void createAccount();

    void deposit(int pin, double amount);

    void withdraw(int pin, double amount);

    double checkBalance(int pin);

    public void TransferMoney(long fromAcc, long toAcc, double amount);
}
