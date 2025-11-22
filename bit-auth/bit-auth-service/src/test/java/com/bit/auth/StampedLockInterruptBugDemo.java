package com.bit.auth;

import java.util.concurrent.locks.StampedLock;

public class StampedLockInterruptBugDemo {
    private static final StampedLock lock = new StampedLock();

    public static void main(String[] args) throws InterruptedException {
        // 写线程持有写锁，不释放
        Thread writer = new Thread(() -> {
            long stamp = lock.writeLock();
            System.out.println("Writer acquired write lock, holding forever...");
            try {
                Thread.sleep(Long.MAX_VALUE); // 永久占有
            } catch (InterruptedException ignored) {
            } finally {
                lock.unlockWrite(stamp);
            }
        });
        writer.start();

        Thread.sleep(500); // 确保写锁被占住

        Thread reader = new Thread(() -> {
            System.out.println("Reader trying to acquire read lock...");
            long stamp = lock.readLock(); // ⚠️ 会阻塞
            try {
                System.out.println("Reader acquired read lock: " + stamp);
            } finally {
                lock.unlockRead(stamp);
            }
        });
        reader.start();

        Thread.sleep(1000); // 等读线程进入阻塞

        System.out.println("Interrupting reader thread...");
        reader.interrupt(); // 中断正在等待的线程

        Thread.sleep(10000); // 此时观察 CPU 占用率
        System.out.println("Main thread finished.");
    }
}
