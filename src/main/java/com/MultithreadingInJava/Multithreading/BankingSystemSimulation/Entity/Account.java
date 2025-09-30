package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Entity;

import java.util.concurrent.locks.ReentrantLock;

public class Account {
    static long count = 2025202500;
    final long accNo;
    Integer pin;
    double balance;
    String name;
    ReentrantLock lock = new ReentrantLock();

    public Account(double balance, String name, Integer pin) {
        this.accNo = ++count;
        this.balance = balance;
        this.name = name;
        this.pin = pin;
    }

    public long getAccNo() {
        return accNo;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public String toString() {
        return "\nAccount Number : " + accNo + "\nBalance : " + balance + "\nAccount Holder Name : " + name;
    }
}

