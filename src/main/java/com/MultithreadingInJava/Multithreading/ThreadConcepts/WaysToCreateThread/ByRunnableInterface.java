package com.MultithreadingInJava.Multithreading.ThreadConcepts.WaysToCreateThread;

public class ByRunnableInterface implements Runnable {

    @Override
    public void run() {
        System.out.println("Implementing Runnable Interface");
    }
}
