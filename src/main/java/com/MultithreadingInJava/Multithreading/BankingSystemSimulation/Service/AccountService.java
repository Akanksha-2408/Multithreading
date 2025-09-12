package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Service;

import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Entity.Account;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Repository.AccountRepo;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class AccountService implements AccountRepo {

    Scanner sc = new Scanner(System.in);
    public static Map<Integer, Account> map = new HashMap<>();

    @Override
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
        System.out.print("Set your 4-digit PIN: \n");
        int PIN = sc.nextInt();
        sc.nextLine();
        for(Map.Entry<Integer, Account> entry : map.entrySet()) {
            if (entry.getKey() == PIN) {
                System.out.println("Account already exists !");
                return;
            }
        }
        System.out.print("Enter Account holder name: \n");
        String name = sc.nextLine();
        double initialBalance = 0.0;
        Account account = new Account(initialBalance, name);
        map.put(PIN, account);
        System.out.println("New Account created successfully !\n" +
                "Exiting...\n");
    }

    @Override
    public void deposit(int pin, double amount) {
        Account account = map.get(pin);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        try {
            if (account.getLock().tryLock(3, TimeUnit.SECONDS)) {
                try {
                    account.setBalance(account.getBalance() + amount);
                    System.out.println("Amount deposited successfully!\n");
                } finally {
                    account.getLock().unlock();
                }
            } else {
                System.out.println("Deposit failed: Account is currently in use. Try again later.\n");
            }
        } catch (InterruptedException e) {
            System.out.println("Deposit interrupted.");
            Thread.currentThread().interrupt(); // Restore interrupt status
        }
    }


    @Override
    public void withdraw(int pin, double amount) {
        Account account = map.get(pin);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }
        try {
            if (account.getLock().tryLock(2, TimeUnit.SECONDS)) {
                try {
                    if (account.getBalance() < amount) {
                        System.out.println("Insufficient balance");
                    } else {
                        account.setBalance(account.getBalance() - amount);
                        System.out.println("Amount Withdrawn Successfully");
                    }
                } finally {
                    account.getLock().unlock();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Deposit interrupted.");
            Thread.currentThread().interrupt(); // Restore interrupt status
        }
    }

    @Override
    public double checkBalance(int pin) {
        Account account = map.get(pin);
        Double balance = 0.0;
        if (account == null) {
            return 0;
        }
        try {
            if (account.getLock().tryLock(2, TimeUnit.SECONDS)) {
                try {
                    balance = account.getBalance();
                } finally {
                    account.getLock().unlock();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Deposit interrupted.");
            Thread.currentThread().interrupt(); // Restore interrupt status
        }
        return balance;
    }

    @Override
    public void TransferMoney(long fromAccNum, long toAccNum, double amount) {
        Account from = getAccountByAccountNumber(fromAccNum);
        Account to = getAccountByAccountNumber(toAccNum);

        if (from == null || to == null) {
            System.out.println("Invalid account(s)");
            return;
        }

        if(from == to) {
            System.out.println("You cannot transfer money to yourself !");
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
                System.out.println("Transfer failed: Could not acquire lock for your account.");
                return;
            }

            secondLocked = second.getLock().tryLock(2, TimeUnit.SECONDS);
            if (!secondLocked) {
                System.out.println("Transfer failed: Could not acquire lock for reciever's account.");
                return;
            }

            // Now both locks are acquired
            if (from.getBalance() < amount) {
                System.out.println("Transfer failed: Insufficient balance");
                return;
            }

            from.setBalance(from.getBalance() - amount);
            to.setBalance(to.getBalance() + amount);
            System.out.println("Transfer successful");

        } catch (InterruptedException e) {
            System.out.println("Transfer interrupted.");
            Thread.currentThread().interrupt(); // Restore interrupt flag

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
    public boolean NewOrExistingAccount() {
        boolean flag = false;
        System.out.println("\n\n1. New Account\n" +
                "2. Existing Account\n" +
                "3. Close\n");
        System.out.print("Enter your choice: ");
        int choice = 0;
        try {
            choice = sc.nextInt();
        } catch(InputMismatchException e) {
            System.out.println("Invalid choice.");
        }
        sc.nextLine();
        switch (choice) {
            case 1:
                createAccount();
                break;
            case 2:
                flag = true;
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Wrong choice");
                break;
        }
        return flag;
    }

    public Account getAccountByAccountNumber(long accountNumber) {
        Account acc = null;
        for (Account account : map.values()) {
            if (account.getAccno() == accountNumber) {
                acc =  account;
                break;
            }
        }
        return acc;
    }
}
