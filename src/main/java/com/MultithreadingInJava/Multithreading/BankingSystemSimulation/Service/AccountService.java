package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Service;

import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Entity.Account;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Repository.AccountRepo;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class AccountService implements AccountRepo {

    Scanner sc = new Scanner(System.in);
    Map<Integer, Account> map = new HashMap<>();
    ReentrantLock lock = new ReentrantLock();
    Account account = null;

    public Integer validateUser() {
        System.out.print("Enter your PIN: ");
        int pin = sc.nextInt();
        sc.nextLine();

        if (map.containsKey(pin)) {
            return pin;
        } else {
            System.out.println("Invalid PIN");
            return null;
        }
    }

    @Override
    public void createAccount() {
        System.out.println("Set your 4-digit PIN: \n");
        int PIN = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter Account holder name: \n");
        String name = sc.nextLine();
        double initialBalance = 0.0;
        Account account = new Account(initialBalance, name);
        map.put(PIN, account);
        printMap();
    }

    @Override
    public void deposit(int pin, double amount) {
        Account account = map.get(pin);
        if (account == null) {
            return;
        }
        account.getLock().lock();
        try {
            account.setBalance(account.getBalance() + amount);
        } finally {
            account.getLock().unlock();
        }
    }

    @Override
    public void withdraw(int pin, double amount) {
        Account account = map.get(pin);
        if (account == null) {
            return;
        }
        account.getLock().lock();
        try {
            if (account.getBalance() < amount) {
                System.out.println("Insufficient balance");
            } else {
                account.setBalance(account.getBalance() - amount);
                System.out.println("Withdraw Successful");
            }
        } finally {
            account.getLock().unlock();
        }
    }

    @Override
    public double checkBalance(int pin) {
        Account account = map.get(pin);
        if (account == null) {
            return 0;
        }
        account.getLock().lock();
        try {
            return account.getBalance();
        } finally {
            account.getLock().unlock();
        }
    }

    @Override
    public void TransferMoney(long fromAccNum, long toAccNum, double amount) {
        Account from = map.get(fromAccNum);
        Account to = map.get(toAccNum);

        if (from == null || to == null) {
            System.out.println("Invalid account(s)");
            return;
        }

        // Transfer A->B or B->A
        // Why compare accno ?
        // To apply and release locks in proper order to avoid deadlock condition
        Account first = fromAccNum < toAccNum ? from : to;
        Account second = fromAccNum < toAccNum ? to : from;

        first.getLock().lock();
        second.getLock().lock();

        try {
            if (from.getBalance() < amount) {
                System.out.println("Transfer failed: Insufficient balance");
                return;
            }
            from.setBalance(from.getBalance() - amount);
            to.setBalance(to.getBalance() + amount);
            System.out.println("Transfer successful");
        } finally {
            second.getLock().unlock();
            first.getLock().unlock();
        }
    }

    //Helper method
    public boolean NewOrExistingAccount() {
        boolean flag = false;
        System.out.println("1. New Account\n" +
                "2. Existing Account\n");
        System.out.println("Enter your choice: ");
        int choice = sc.nextInt();
        sc.nextLine();
        switch (choice) {
            case 1:
                createAccount();
                break;
            case 2:
                flag = true;
                break;
            default:
                System.out.println("Wrong choice");
                break;
        }
        return flag;
    }

    //for Understanding
    public void printMap() {
        for(Map.Entry<Integer, Account> entry : map.entrySet()) {
            System.out.println("PIN : " + entry.getKey() + "\nAccount Details : " + entry.getValue());
        }
    }
}
