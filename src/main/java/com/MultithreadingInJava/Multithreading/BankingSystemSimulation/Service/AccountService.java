package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Service;

import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Entity.Account;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.ExceptionHandler.BankingException;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.ExceptionHandler.ErrorCode;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Repository.AccountRepo;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AccountService implements AccountRepo {

    Scanner sc = new Scanner(System.in);
    public static List<Account> accountList = new ArrayList<>();  //actual

    @Override
    public Integer validateUser() {
        System.out.print("Enter your PIN: ");
        Integer pin;
        pin = sc.nextInt();
        sc.nextLine();

        if (accountList.get(pin) != null) {
            return pin;
        } else {
            throw new BankingException(ErrorCode.INVALID_PIN);
        }
    }

    @Override
    public void createAccount() {
        System.out.print("Set your 4-digit PIN: \n");
        int PIN = sc.nextInt();
        sc.nextLine();
        if(null != accountList) {
            for (Account account : accountList) {
                if (account.getPin() == PIN) {
                    throw new BankingException(ErrorCode.CREATE_ACCOUNT);
                }
            }
        }
        System.out.print("Enter Account holder name: \n");
        String name = sc.nextLine();
        double initialBalance = 0.0;
        Account account = new Account(initialBalance, name, PIN);
        accountList.add(account);
        System.out.println("New Account created successfully !\n" +
                "Exiting...\n");
    }

    @Override
    public void deposit(int pin, double amount) throws InterruptedException {
        Account account = null;
        for(Account acc : accountList) {
            if (acc.getPin() == pin) {
                account = acc;
            }
        }
        
        if (account == null) {
            throw new BankingException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if (account.getLock().tryLock(3, TimeUnit.SECONDS)) {
            try {
                account.setBalance(account.getBalance() + amount);
            } finally {
                account.getLock().unlock();
            }
        } else {
            throw new BankingException(ErrorCode.DEPOSIT_FAILED);
        }
    }

    @Override
    public void withdraw(int pin, double amount) throws InterruptedException {
        Account account = null;
        for(Account acc : accountList) {
            if (acc.getPin() == pin) {
                account = acc;
            }
        }
        if (account == null) {
            throw new BankingException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if (account.getLock().tryLock(2, TimeUnit.SECONDS)) {
            try {
                if (account.getBalance() < amount) {
                    throw new BankingException(ErrorCode.INSUFFICIENT_BALANCE);
                } else {
                    account.setBalance(account.getBalance() - amount);
                }
            } finally {
                account.getLock().unlock();
            }
        }
    }

    @Override
    public double checkBalance(int pin) throws InterruptedException {
        Account account = null;
        for(Account acc : accountList) {
            if (acc.getPin() == pin) {
                account = acc;
            }
        }
        double balance = 0.0;
        if (account == null) {
            return 0;
        }
        if (account.getLock().tryLock(2, TimeUnit.SECONDS)) {
            try {
                balance = account.getBalance();
            } finally {
                account.getLock().unlock();
            }
        }
        return balance;
    }

    @Override
    public void TransferMoney(long fromAccNum, long toAccNum, double amount) throws InterruptedException {
        Account from = getAccountByAccountNumber(fromAccNum);
        Account to = getAccountByAccountNumber(toAccNum);

        if (from == null || to == null) {
            throw new BankingException(ErrorCode.INVALID_ACCOUNT);
        }

        if(from == to) {
            throw new BankingException(ErrorCode.INVALID_TRANSACTION);
        }

        // Lock in consistent order to avoid deadlock
        Account first = fromAccNum < toAccNum ? from : to;
        Account second = fromAccNum < toAccNum ? to : from;

        boolean firstLocked = false;
        boolean secondLocked = false;

        try {
            // Try to acquire both locks with timeout
            firstLocked = first.getLock().tryLock(2, TimeUnit.SECONDS);
            if (!firstLocked) {
                throw new BankingException(ErrorCode.LOCK_UNAVAILABLE);
            }

            secondLocked = second.getLock().tryLock(2, TimeUnit.SECONDS);
            if (!secondLocked) {
                throw new BankingException(ErrorCode.LOCK_UNAVAILABLE);
            }

            // Now both locks are acquired
            if (from.getBalance() < amount) {
                throw new BankingException(ErrorCode.INSUFFICIENT_BALANCE);
            }

            from.setBalance(from.getBalance() - amount);
            to.setBalance(to.getBalance() + amount);

        } finally {
            if (secondLocked) {
                second.getLock().unlock();
            }
            if (firstLocked) {
                first.getLock().unlock();
            }
        }
    }


    //Helper methods
    public Boolean NewOrExistingAccount() {
        System.out.println("\n\n1. New Account\n" +
                "2. Existing Account\n" +
                "3. Close\n");
        System.out.print("Enter your choice: ");
        int choice = 0;
        try {
            choice = sc.nextInt();
        } catch(InputMismatchException e) {
            throw new BankingException(ErrorCode.INVALID_CHOICE);
        }
        sc.nextLine();
        return switch (choice) {
            case 1 -> {
                createAccount();
                yield false;
            }
            case 2 -> true;
            case 3 -> null;
            default -> {
                System.out.println("Wrong choice");
                yield NewOrExistingAccount();
            }
        };
    }

    public Account getAccountByAccountNumber(long accountNumber) {
        Account acc = null;
        for (Account account : accountList) {
            if (account.getAccNo() == accountNumber) {
                acc =  account;
                break;
            }
        }
        return acc;
    }
}
