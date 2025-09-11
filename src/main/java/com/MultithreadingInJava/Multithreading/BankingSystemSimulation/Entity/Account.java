package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Entity;

import java.util.concurrent.locks.ReentrantLock;

public class Account {
    static long count = 2025202500;
    final long accno;
    double balance;
    String name;
    ReentrantLock lock = new ReentrantLock();

    public Account(double balance, String name) {
        this.accno = ++count;
        this.balance = balance;
        this.name = name;
    }

    public long getAccno() {
        return accno;
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

    public String toString() {
        return "\nAccount Number : " + accno + "\nBalance : " + balance + "\nAccount Holder Name : " + name;
    }
}

