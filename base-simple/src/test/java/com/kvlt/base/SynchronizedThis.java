package com.kvlt.base;

/**
 * SynchronizedThis
 *
 * @author KVLT
 * @date 2019-03-23.
 */
public class SynchronizedThis {

    static Lock lock = new Lock();

    public static void main(String[] args) {
        new Thread("t1") {
            public void run() {
                lock.m1();
            }
        }.start();

        new Thread("t2") {
            public void run() {
                lock.m2();
            }
        }.start();
    }
}

class Lock {
    public synchronized void m1() {
        try {
            System.out.println(Thread.currentThread().getName());
            Thread.sleep(10000);
        } catch (Exception e) {

        }
    }

    public synchronized void m2() {
        try {
            System.out.println(Thread.currentThread().getName());
            Thread.sleep(10000);
        } catch (Exception e) {

        }
    }
}
