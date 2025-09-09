package com.MultithreadingInJava.Multithreading.ThreadConcepts.ThreadMethods;

public class MyThread extends Thread {
    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + ":" + i);
        }
        System.out.println("END");
    }

    public static void main(String[] args) {
        MyThread t1 = new MyThread();
        MyThread t2 = new MyThread();

        t1.setName("Thread-1");
        t2.setName("Thread-2");

        //t1.join()
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted Exception at t1.join()");
        }
        t2.start();  //1st thread1 will complete and then thread2 will start


        //t1.sleep()
        t1.start();
        try {
            t1.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Exception in sleep method");
        }
        t2.start();

    }
}
