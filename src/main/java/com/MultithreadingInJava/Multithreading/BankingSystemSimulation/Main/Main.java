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
    static List<Account> accountList = new ArrayList<>();

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

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
                    PIN = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
//                    System.out.println("Invalid PIN format.");  //TODO
                    throw new BankingException(ErrorCode.INVALID_PIN);
                }

                // Check if PIN exists
                if (accountList.contains(PIN)) {
                    System.out.println("WELCOME " + accountList.get(PIN).getName() + " !!");

                } else {
//                    System.out.println("PIN not Found");  //TODO
                    throw new BankingException(ErrorCode.PIN_NOT_FOUND);
                }

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
//                        System.out.println("Invalid input. Enter correct choice.");  //TODO
                        throw new BankingException(ErrorCode.INVALID_CHOICE);
                    }

                    switch (choice) {
                        case 1:
                            System.out.println("Enter deposit Amount: ");
                            double dAmount = sc.nextDouble();
                            sc.nextLine();
                            Integer depositPIN = PIN;
                            Future<?> future = executorService.submit(() -> {
                                try {
                                    accountService.deposit(depositPIN, dAmount);
                                } catch(InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            try {
                                future.get();
                                System.out.println("Amount Deposited Successfully!");
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                            break;

                        case 2:
                            System.out.println("Enter withdraw Amount: ");
                            double wAmount = sc.nextDouble();
                            sc.nextLine();
                            Integer withdrawPIN = PIN;
                            future = executorService.submit(() -> {
                                try {
                                    accountService.withdraw(withdrawPIN, wAmount);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            try {
                                future.get();
                                System.out.println("Amount Withdrawn Successfully!");
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                            break;

                        case 3:
                            long senderAccountNumber = accountList.get(PIN).getAccNo();  // sender's account number
                            System.out.println("Enter the name to transfer money: ");
                            String name = sc.nextLine();

                            long receiverAccountNumber = 0;
                            boolean receiverFound = false;
                            for (Account account : accountList) {
                                if (account.getName().equalsIgnoreCase(name)) {
                                    receiverAccountNumber = account.getAccNo();
                                    receiverFound = true;
                                    break;
                                }
                            }

                            if (!receiverFound) {
                                System.out.println("Receiver not found.");
                                break;
                            }

                            System.out.println("Enter the amount to Transfer: ");
                            double amount = sc.nextDouble();
                            sc.nextLine();
                            long finalReceiverAccountNumber = receiverAccountNumber;
                            future = executorService.submit(() -> {
                                try {
                                    accountService.TransferMoney(senderAccountNumber, finalReceiverAccountNumber, amount);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            try {
                                future.get();
                                System.out.println("Amount Transferred Successfully!");
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                            break;

                        case 4:
                            Integer finalPIN = PIN;
                            Callable<Double> balanceTask = () -> {
                                Integer balancePIN = finalPIN;
                                double balance = accountService.checkBalance(balancePIN);
                                System.out.println("Current Balance: " + balance);
                                return balance;
                            };
                            future = executorService.submit(balanceTask);
                            try {
                                future.get();
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                            break;

                        case 5:
                            System.out.println("Thank you !!");
                            break;

                        default:
                            System.out.println("Wrong Choice");
                    }
                }
            }
        }
        executorService.shutdown();
        try {
            if(executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
