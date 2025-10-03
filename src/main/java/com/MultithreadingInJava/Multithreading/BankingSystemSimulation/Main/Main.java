package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Main;

import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Entity.Account;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.ExceptionHandler.BankingException;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.ExceptionHandler.ErrorCode;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Service.AccountService;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    static AccountService accountService = new AccountService();
    static Scanner sc = new Scanner(System.in);
    static List<Account> accountList = AccountService.accountList;

    static List<Callable<String>> concurrentTasks = new ArrayList<>();

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        try {
            while (true) {
                Boolean existingAccount = accountService.NewOrExistingAccount();
                if (existingAccount == null) {
                    System.out.print("Exiting...");
                    break;
                }

                Integer PIN = null;

                if (existingAccount) {
                    System.out.println("Enter your 4-digit PIN: ");
                    try {
                        PIN = sc.nextInt();
                        sc.nextLine();
                    } catch (NumberFormatException e) {
                        throw new BankingException(ErrorCode.INVALID_PIN);
                    }

                    // Check if PIN exists
                    Account userAccount = null;
                    for (Account acc : accountList) {
                        if (acc.getPin().equals(PIN)) {
                            userAccount = acc;
                            break;
                        }
                    }

                    if (userAccount != null) {
                        System.out.println("WELCOME " + userAccount.getName() + " !!");
                    } else {
                        throw new BankingException(ErrorCode.PIN_NOT_FOUND);
                    }

                    try {
                        int choice = 0;
                        while (choice != 5) {
                            System.out.println("\n-----MAIN MENU-----\n" +
                                    "1. Deposit\n" +
                                    "2. Withdraw\n" +
                                    "3. Transfer Money\n" +
                                    "4. Check Balance\n" +
                                    "5. Exit\n");
                            System.out.println("Enter your Choice: ");
                            try {
                                choice = sc.nextInt();
                                sc.nextLine();
                            } catch (InputMismatchException e) {
                                throw new BankingException(ErrorCode.INVALID_CHOICE);
                            }

                            switch (choice) {
                                case 1:
                                    System.out.println("Enter deposit Amount: ");
                                    double dAmount = sc.nextDouble();
                                    sc.nextLine();
                                    Integer depositPIN = PIN;

                                    long accNum = 0;
                                    for (Account account : accountList) {
                                        if(account.getPin().equals(depositPIN)) {
                                            accNum = account.getAccNo();
                                        }
                                    }

                                    long finalAccNum = accNum;
                                    concurrentTasks.add(() -> {
                                        accountService.deposit(depositPIN, dAmount);
                                        return "Amount : " + dAmount + " deposited successfully in " + finalAccNum;
                                    });
                                    break;

                                case 2:
                                    System.out.println("Enter withdraw Amount: ");
                                    double wAmount = sc.nextDouble();
                                    sc.nextLine();
                                    Integer withdrawPIN = PIN;

                                    long accNo = 0;
                                    for (Account account : accountList) {
                                        if(account.getPin().equals(withdrawPIN)) {
                                            accNo = account.getAccNo();
                                        }
                                    }

                                    long finalAccNo = accNo;
                                    concurrentTasks.add(() -> {
                                        accountService.withdraw(withdrawPIN, wAmount);
                                        return "Amount : " + wAmount + " Withdrawn successfully from " + finalAccNo;
                                    });
                                    break;

                                case 3:
                                    Account sender = null;
                                    Account receiver = null;
                                    for (Account acc : accountList) {
                                        if (acc.getPin().equals(PIN)) {
                                            sender = acc;
                                        }
                                    }
                                    if (sender == null) {
                                        throw new BankingException(ErrorCode.PIN_NOT_FOUND);
                                    }

                                    long senderAccountNumber = sender.getAccNo(); // sender's account number
                                    System.out.println("Enter the account number to transfer money: ");
                                    long receiverAccountNumber = sc.nextLong();

                                    boolean receiverFound = false;
                                    for (Account account : accountList) {
                                        if (account.getAccNo() == receiverAccountNumber) {
                                            receiver = account;
                                            receiverFound = true;
                                        }
                                    }

                                    if (!receiverFound) {
                                        throw new BankingException(ErrorCode.ACCOUNT_NOT_FOUND);
                                    }

                                    receiverAccountNumber = receiver.getAccNo();

                                    System.out.println("Enter the amount to Transfer: ");
                                    double amount = sc.nextDouble();
                                    sc.nextLine();
                                    long finalReceiverAccountNumber = receiverAccountNumber;
                                    concurrentTasks.add(() -> {
                                        accountService.TransferMoney(senderAccountNumber, finalReceiverAccountNumber, amount);
                                        return "Transfer successful from " + senderAccountNumber + " to " + finalReceiverAccountNumber + " : " + amount;
                                    });
                                    break;

                                case 4:
                                    Integer balancePIN = PIN;
                                    Callable<Double> balanceTask = () -> accountService.checkBalance(balancePIN);
                                    Future<Double> future = executorService.submit(balanceTask);
                                    try {
                                        double balance = future.get();
                                        System.out.println("Current Balance: " + balance);
                                    } catch (InterruptedException | ExecutionException e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;

                                case 5:
                                    if (concurrentTasks.isEmpty()) {
                                        System.out.println("No transactions to process!");
                                    } else {
                                        List<Future<String>> result = executorService.invokeAll(concurrentTasks);
                                        for (Future<String> res : result) {
                                            System.out.println(res.get());
                                        }
                                    }
                                    concurrentTasks.clear(); //execute only once
                                    System.out.println("Thank you!");
                                    break;

                                default:
                                    System.out.println("Wrong Choice");
                                    break;
                            }
                        }
                    } catch (BankingException e) {
                        System.err.println(e.getMessage());
                    } catch (InputMismatchException e) {
                        System.err.println("Enter a valid number");
                        sc.nextLine();
                    }
                    catch (Exception e) {
                        System.err.println("The program encountered an error.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

    }
}