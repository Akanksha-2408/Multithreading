package com.MultithreadingInJava.Multithreading.ThreadConcepts.ExecutorFramework;

import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class Executor_Service {
    public static void main(String[] args) throws InterruptedException, IllegalMonitorStateException {

//        System.out.println("\nSingleThreadPool\n");
//
//        // Step 1: Create a simple Runnable task
//        Runnable task1 = () -> {
//            System.out.println("Task is running in thread: " + Thread.currentThread().getName());
//            try {
//                sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        };
//
//        // Step 2: Create an ExecutorService with a single thread
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//
//        // Step 3: Submit the task for execution
//
//        executor.submit(task1);  //returns future
//
//        // Step 4: Shutdown the executor (important!)
//        executor.shutdown();

//        System.out.println("\nFixedThreadPool\n");
//
//        Runnable task2 = () -> {
//            System.out.println(Thread.currentThread().getName() + " = Task-1");
//        };
//
//        Runnable task3 = () -> {
//            System.out.println(Thread.currentThread().getName() + " = Task-2");
//        };
//
//        Runnable task4 = () -> {
//            System.out.println(Thread.currentThread().getName() + " = Task-3");
//        };
//
//        Runnable task5 = () -> {
//            System.out.println(Thread.currentThread().getName() + " = Task-4");
//        };
//
//        Runnable task6 = () -> {
//            System.out.println(Thread.currentThread().getName() + " = Task-5");
//        };
//
//        Callable<Integer> callableTask = () -> {
//            int sum = 0;
//            for(int i = 0; i < 1000; i++) {
//                sum += i;
//            }
//            return sum;
//        };
//
//        ExecutorService fixedPool = Executors.newFixedThreadPool(5);
//        fixedPool.submit(task2);
//        fixedPool.submit(task3);
//        fixedPool.submit(task4);
//        fixedPool.submit(task5);
//        fixedPool.submit(task6);
//        Future<Integer> future =  fixedPool.submit(callableTask);
//        fixedPool.shutdown();
//
//        try {
//            System.out.println(future.get());
//        } catch (InterruptedException | ExecutionException e) {
//            System.out.println(e.getCause().getMessage());
//        }
//
//        Callable task = () -> {
//            int product = 1;
//            for(int i = 1; i < 10; i++) {
//                product *= i;
//            }
//            return product;
//        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // 1. One-time task after 3 seconds
        Runnable oneTimeTask = () -> System.out.println("One-time task executed at: " + System.currentTimeMillis());
        scheduler.schedule(oneTimeTask, 3, TimeUnit.SECONDS);

        // 2. Repeating task with fixed delay (starts 2s after completion of previous task)
        Runnable fixedDelayTask = () -> {
            System.out.println("Fixed delay task started at: " + System.currentTimeMillis());
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        scheduler.scheduleWithFixedDelay(fixedDelayTask, 1, 5, TimeUnit.SECONDS);

        // 3. Repeating task at fixed rate (starts every 5s regardless of previous run duration)
        Runnable fixedRateTask = () -> {
            System.out.println("Fixed rate task running at: " + System.currentTimeMillis());
        };
        scheduler.scheduleAtFixedRate(fixedRateTask, 1, 5, TimeUnit.SECONDS);

        // Shutdown after 30 seconds
        scheduler.schedule(() -> {
            System.out.println("Shutting down scheduler...");
            scheduler.shutdown();
        }, 5, TimeUnit.SECONDS);
    }
}