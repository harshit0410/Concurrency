package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello and welcome!");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        BathroomProblem b = new BathroomProblem(3);

        executorService.submit(() -> {
            b.useBathroomD();
        });
        executorService.submit(() -> {
            b.useBathroomD();
        });
        executorService.submit(() -> {
            b.useBathroomD();
        });

        executorService.submit(() -> {
            b.useBathroomR();
        });
        executorService.submit(() -> {
            b.useBathroomR();
        });
        executorService.submit(() -> {
            b.useBathroomD();
        });

//        try {
//            executorService.awaitTermination(10, TimeUnit.SECONDS);
//        }
//
//        catch (Exception e) {
//            System.out.println(e);
//        }

        executorService.shutdown();
    }
}



class BathroomProblem {
    Semaphore s;
    int type;
    ReentrantLock l;
    int n;
    AtomicInteger ai;
    Condition c;

    BathroomProblem (int n) {
        this.n = n;
        s = new Semaphore(n);
        type = 0;
        l = new ReentrantLock();
        ai = new AtomicInteger(n);
        c= l.newCondition();
    }

    // Eg: R1, R2, R3, D1, D2, R4, D3, R5, D4

    public void useBathroom(int type) throws InterruptedException {
        l.lock();
        System.out.println("Acquired");
        while (true) {
            if ((ai.get() < 3 && this.type != type) || ai.get() ==0) {
                System.out.println("Waiting");
                c.await();
            }
            else {
                this.type= type;
                ai.decrementAndGet();
                break;
            }
        }
        System.out.println("Using Bathroom for type" + type);
        l.unlock();



        Thread.sleep(1000);


        l.lock();
        System.out.println("Acquired later");
        ai.incrementAndGet();
        if (ai.get() == n) {
            this.type = 0;
        }
        c.signalAll();
        l.unlock();
    }

    public void useBathroomD () {
        try {
            useBathroom(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void useBathroomR () {
        try {
            useBathroom(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
