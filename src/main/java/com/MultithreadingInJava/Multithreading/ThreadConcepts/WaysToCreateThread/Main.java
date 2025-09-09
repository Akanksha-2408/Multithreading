package com.MultithreadingInJava.Multithreading.ThreadConcepts.WaysToCreateThread;

public class Main {
    public static void main(String[] args) {

        //Extends Thread class
        ByThreadClass tc = new ByThreadClass();  //thread object created
        tc.start();

        //Implements Runnable Interface
        ByRunnableInterface runnable = new ByRunnableInterface();  //Task is defined by overriding run() method
        Thread t1 = new Thread(runnable);  //thread object created
        t1.start();

        //Using lambda Expression
        Thread t2 = new Thread(() -> {
            System.out.println("Using lambda Expression");
        });
        t2.start();
    }
}
