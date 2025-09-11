package com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Main;

import com.MultithreadingInJava.Multithreading.BankingSystemSimulation.Service.AccountService;

import java.util.Scanner;

public class Main {

    static AccountService accServ = new AccountService();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while(true) {
            boolean existingAccount = accServ.NewOrExistingAccount();

            while (existingAccount) {
                if(accServ.validateUser() != null) {
                    System.out.println("Main Menu\n" +
                            "1. Deposit\n" +
                            "2. Withdraw\n" +
                            "3. Transfer Money\n" +
                            "4. Check Balance\n" +
                            "5. Exit\n");
                    System.out.println("Enter your Choice: ");
                    int choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1:
                            System.out.println("Enter PIN: ");  // 2nd time
                            String strPin1 = sc.nextLine();
                            int pin1 = Integer.parseInt(strPin1);
                            System.out.println("Enter deposit Amount: ");
                            double damount = sc.nextDouble();
                            accServ.deposit(pin1, damount);
                            break;
                        case 2:
                            System.out.println("Enter PIN: ");
                            String strPin2 = sc.nextLine();
                            int pin2 = Integer.parseInt(strPin2);
                            System.out.println("Enter withdraw Amount: ");
                            double wamount = sc.nextDouble();
                            accServ.withdraw(pin2, wamount);
                            break;
                        case 3:
//                            System.out.println("Enter Account holder name: ");
//                            String fromName = sc.nextLine();
//                            System.out.println("Enter reciever's name(to whom you want to transfer money): ");
//                            String toName = sc.nextLine();
//                            System.out.println("Enter transfer amount: ");
//                            String transferAmount = sc.nextLine();
//                            double amount = Double.parseDouble(transferAmount);
//                            accServ.TransferMoney(fromacc, toacc, amount);
//                            break;
                        case 4:
                            System.out.println("Enter PIN: ");
                            String strPin3 = sc.nextLine();
                            int pin3 = Integer.parseInt(strPin3);
                            accServ.checkBalance(pin3);
                            break;
                        case 5:
                            System.out.println("Thankyou !!");
                            System.exit(0);
                    }
                } else {
                    System.out.println("Invalid Pin Entered !");
                }
            }
        }
    }
}
