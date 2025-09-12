package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Main;

import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Entity.Account;
import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Service.AccountService;
import org.aspectj.weaver.ast.Call;

import java.awt.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

    static AccountService accountService = new AccountService();
    static Scanner sc = new Scanner(System.in);
    static Map<Integer, Account> map = AccountService.map;

    static List<Runnable> runnables = new ArrayList<>();

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        while (true) {
            boolean existingAccount = accountService.NewOrExistingAccount();
            Integer PIN = null;

            if (existingAccount) {
                System.out.println("Enter your 4-digit PIN: ");
                try {
                    PIN = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid PIN format.");
                    continue;
                }

                // Check if PIN exists
                if (map.containsKey(PIN)) {
                    System.out.println("WELCOME " + map.get(PIN).getName() + " !!");
                } else {
                    System.out.println("PIN not Found");
                    continue;
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
                        System.out.println("Invalid input. Enter a number between 1 and 5.");
                        sc.nextLine();
                        continue;
                    }

                    switch (choice) {
                        case 1:
                            System.out.println("Enter deposit Amount: ");
                            double dAmount = sc.nextDouble();
                            sc.nextLine();
                            Integer depositPIN = PIN;
                            executorService.submit(() -> accountService.deposit(depositPIN, dAmount));
                            break;

                        case 2:
                            System.out.println("Enter withdraw Amount: ");
                            double wAmount = sc.nextDouble();
                            sc.nextLine();
                            Integer withdrawPIN = PIN;
                            executorService.submit(() -> accountService.withdraw(withdrawPIN, wAmount));
                            break;

                        case 3:
                            long senderAccountNumber = map.get(PIN).getAccno();  // sender's account number
                            System.out.println("Enter the name to transfer money: ");
                            String name = sc.nextLine();
                            long receiverAccountNumber = 0;

                            boolean receiverFound = false;
                            for (Map.Entry<Integer, Account> entry : map.entrySet()) {
                                if (entry.getValue().getName().equalsIgnoreCase(name)) {
                                    receiverAccountNumber = entry.getValue().getAccno();
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
                            executorService.submit(() -> accountService.TransferMoney(senderAccountNumber, finalReceiverAccountNumber, amount));
                            break;

                        case 4:
                            Integer finalPIN = PIN;
                            Callable<Double> balanceTask = () -> {
                                Integer balancePIN = finalPIN;
                                double balance = accountService.checkBalance(balancePIN);
                                System.out.println("Current Balance: " + balance);
                                return balance;
                            };
                            Future<Double> future = executorService.submit(balanceTask);
                            try {
                                future.get(); // wait for completion (optional)
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
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
            executorService.shutdown();
        }

    }
}
