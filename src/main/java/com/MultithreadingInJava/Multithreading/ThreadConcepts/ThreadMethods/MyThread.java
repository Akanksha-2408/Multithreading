package com.MultithreadingInJava.Multithreading.ThreadConcepts.ThreadMethods;

public class MyThread {
    public static void main(String[] args) {

//        MyThread t1 = new MyThread();
//        MyThread t2 = new MyThread();
//
//        t1.setName("Thread-1");
//        t2.setName("Thread-2");

        //t1.join()-
        /*t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted Exception at t1.join()");
        }
        t2.start(); */ //1st thread1 will complete and then thread2 will start


        //t1.sleep()-
//        t1.start();
//        try {
//            t1.sleep(1000);
//        } catch (InterruptedException e) {
//            System.out.println("Exception in sleep method");
//        }
//        t2.start();

        //wait() method-
        waitMethod wm = new waitMethod();

        Thread producer = new Thread(() -> {
            try {
                wm.produce();
            } catch (InterruptedException e) {
                System.out.println("Producer interrupted: " + e.getMessage());
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                wm.consume();
            } catch (InterruptedException e) {
                System.out.println("Consumer interrupted: " + e.getMessage());
            }
        });

        producer.start();
        consumer.start();


        //interrupt() method-
//        Thread worker = new Thread(() -> {
//            try {
//                for (int i = 1; i <= 10; i++) {
//                    System.out.println("Working... " + i);
//                    Thread.sleep(1000); // 1 sec
//                }
//            } catch (InterruptedException e) {
//                System.out.println("Worker thread was interrupted!");
//            }
//
//            System.out.println("Worker thread exiting.");
//        });
//
//        worker.start();
//
//        try {
//            Thread.sleep(3000); // Let the worker run for 3 seconds
//        } catch (InterruptedException e) {
//            System.out.println("Main thread interrupted.");
//        }
//
//        System.out.println("Main thread interrupting the worker...");
//        worker.interrupt(); // Interrupt the worker thread
    }
}
