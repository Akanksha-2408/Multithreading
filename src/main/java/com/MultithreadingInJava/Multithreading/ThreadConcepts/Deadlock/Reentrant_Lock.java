package com.MultithreadingInJava.Multithreading.ThreadConcepts.Deadlock;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class Reentrant_Lock {

    private static final ReentrantLock lock1 = new ReentrantLock();
    private static final ReentrantLock lock2 = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(new Task(lock1, lock2), "Thread-1");
        Thread t2 = new Thread(new Task(lock2, lock1), "Thread-2");

        t1.start();
        t2.start();
    }

    static class Task implements Runnable {
        private final ReentrantLock firstLock;
        private final ReentrantLock secondLock;

        public Task(ReentrantLock firstLock, ReentrantLock secondLock) {
            this.firstLock = firstLock;
            this.secondLock = secondLock;
        }

        @Override
        public void run() {
            try {
                if (firstLock.tryLock(1, TimeUnit.SECONDS)) {
                    System.out.println(Thread.currentThread().getName() + " acquired " + firstLock);

                    try {
                        Thread.sleep(100);

                        if (secondLock.tryLock(1, TimeUnit.SECONDS)) {
                            try {
                                System.out.println(Thread.currentThread().getName() + " acquired " + secondLock);
                                // Simulate work
                            } finally {
                                secondLock.unlock();
                                System.out.println(Thread.currentThread().getName() + " released " + secondLock);
                            }
                        } else {
                            System.out.println(Thread.currentThread().getName() + " could not acquire " + secondLock);
                        }

                    } catch(IllegalMonitorStateException e) {
                        System.out.println(Thread.currentThread().getName() + " is not holding " + secondLock);
                    } finally {
                        firstLock.unlock();
                        System.out.println(Thread.currentThread().getName() + " released " + firstLock);
                    }

                } else {
                    System.out.println(Thread.currentThread().getName() + " could not acquire " + firstLock);
                }
            } catch(IllegalMonitorStateException e) {
                System.out.println(Thread.currentThread().getName() + " is not holding " + firstLock);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " was interrupted.");
            }
        }
    }
}
