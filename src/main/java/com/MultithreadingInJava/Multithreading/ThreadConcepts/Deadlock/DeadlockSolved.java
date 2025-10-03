package com.MultithreadingInJava.Multithreading.ThreadConcepts.Deadlock;

import java.util.concurrent.locks.ReentrantLock;

public class DeadlockSolved {

    private static int counter=0;

    private static final ReentrantLock lock1 = new ReentrantLock();
    private static final ReentrantLock lock2 = new ReentrantLock();

    public static void main(String[] args) {
        // Thread 1
        Thread thread1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("\nThread 1: Holding Lock1...");

                try { Thread.sleep(100); } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                synchronized (lock2) {
                    int temp=counter;
                    counter = temp + 1;
                    System.out.println("\nUpdated Counter = " +  counter);
                    System.out.println("\nThread 1: Acquired Lock2!");
                }
                System.out.println("\nThread 1: Released Lock2!");
            }
            System.out.println("\nThread 1: Released Lock1!");
        });

        // Thread 2
        Thread thread2 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("\nThread 2: Holding Lock1...");

                try { Thread.sleep(100); } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                synchronized (lock1) {
                    int temp=counter;
                    counter = temp + 1;
                    System.out.println("\nUpdated Counter = " +  counter);
                    System.out.println("\nThread 2: Acquired Lock2!");
                }
                System.out.println("\nThread 2: Released Lock2!");
            }
            System.out.println("\nThread 2: Released Lock1!");
        });

        thread1.start();
        thread2.start();
    }
}

