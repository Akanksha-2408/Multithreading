package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Service;

import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Entity.Account;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.ExceptionHandler.BankingException;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.ExceptionHandler.ErrorCode;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Repository.AccountRepo;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AccountService implements AccountRepo {

    Scanner sc = new Scanner(System.in);
//    public static Map<Integer, Account> map = new HashMap<>();
                                                                  //TODO: Replace Sout with throw customException(appropriate class name)
    public List<Account> accountList;

    @Override
    public Integer validateUser() {
        System.out.print("Enter your PIN: ");
        int pin = sc.nextInt();
        sc.nextLine();

        if (accountList.contains(pin)) {
            return pin;
        } else {
//            System.out.println("Invalid PIN");  //TODO
            throw new BankingException(ErrorCode.INVALID_PIN);
        }
    }

    @Override
    public void createAccount() {
        System.out.print("Set your 4-digit PIN: \n");
        int PIN = sc.nextInt();
        sc.nextLine();
        for(Account account:accountList) {
            if (account.getPin() == PIN) {
//                System.out.println("Account already exists !");  //TODO
                throw new BankingException(ErrorCode.CREATE_ACCOUNT);
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
        Account account = accountList.get(pin);
        if (account == null) {
//            System.out.println("Account not found.");  //TODO
            throw new BankingException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if (account.getLock().tryLock(3, TimeUnit.SECONDS)) {
            try {
                account.setBalance(account.getBalance() + amount);
            } finally {
                account.getLock().unlock();
            }
        } else {
//            System.out.println("Deposit failed: Account is currently in use. Try again later.\n");  //TODO
            throw new BankingException(ErrorCode.DEPOSIT_FAILED);
        }
    }

    @Override
    public void withdraw(int pin, double amount) throws InterruptedException {
        Account account = accountList.get(pin);
        if (account == null) {
//            System.out.println("Account not found.");  //TODO
            throw new BankingException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        if (account.getLock().tryLock(2, TimeUnit.SECONDS)) {
            try {
                if (account.getBalance() < amount) {
//                    System.out.println("Insufficient balance");  //TODO
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
        Account account = accountList.get(pin);
        Double balance = 0.0;
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
//            System.out.println("Invalid account(s)");  //TODO
            throw new BankingException(ErrorCode.INVALID_ACCOUNT);
        }

        if(from == to) {
//            System.out.println("You cannot transfer money to yourself !");  //TODO
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
//                System.out.println("Transfer failed: Could not acquire lock");  //TODO
                throw new BankingException(ErrorCode.LOCK_UNAVAILABLE);
            }

            secondLocked = second.getLock().tryLock(2, TimeUnit.SECONDS);
            if (!secondLocked) {
//                System.out.println("Transfer failed: Could not acquire lock for reciever's account.");  //TODO
                throw new BankingException(ErrorCode.LOCK_UNAVAILABLE);
            }

            // Now both locks are acquired
            if (from.getBalance() < amount) {
//                System.out.println("Transfer failed: Insufficient balance");  //TODO
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
//            System.out.println("Invalid choice.");  //TODO
            throw new BankingException(ErrorCode.INVALID_CHOICE);
        }
        sc.nextLine();
        switch (choice) {
            case 1:
                createAccount();
                return false;
            case 2:
                return true;
            case 3:
                return null;
            default:
                System.out.println("Wrong choice");
                return NewOrExistingAccount();
        }
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
