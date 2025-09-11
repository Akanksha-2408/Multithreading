package com.MultithreadingInJava.Multithreading.ThreadConcepts.WaysToCreateThread;

public class ByThreadClass extends Thread{
    @Override
    public void run() {
        System.out.println("Extending Thread class");
    }
}
