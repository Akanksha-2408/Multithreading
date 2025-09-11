package com.MultithreadingInJava.Multithreading.ThreadConcepts.ThreadMethods;

public class waitMethod {
    boolean isAvailable = false;

    synchronized void produce() throws InterruptedException {
        while (isAvailable) {
            wait();  // Wait if already produced
        }
        System.out.println("Production in progress...");
        isAvailable = true;
        notify();  // Wake up consumer
    }

    synchronized void consume() throws InterruptedException {
        while (!isAvailable) {
            wait();  // Wait if nothing produced yet
        }
        System.out.println("Consumption in progress...");
        isAvailable = false;
        notify();  // Wake up producer
    }
}
