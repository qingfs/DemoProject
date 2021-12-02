package com.example.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author xuyongjia
 * @date 2020/7/29
 */
public class LockTest {

    public void testReentrantLock() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock(Boolean.FALSE);
        int i = 10;
        for (int i1 = 0; i1 < i; i1++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + "进入" );
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + "执行中...");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            },"Thread"+i1).start();
        }

        Thread.sleep(100000);
    }


    public void testReentrantReadWriteLock(){
        // 先占用读锁，那么写锁被阻塞
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        new Thread(()->{
            lock.readLock().lock();
            try {
                System.out.println(Thread.currentThread().getName() + "执行中...");
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName() + "释放..");
                lock.readLock().unlock();
            }
        }, "读锁").start();


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(()->{
            lock.writeLock().lock();
            try {
                System.out.println(Thread.currentThread().getName() + "执行中...");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName() + "释放..");
                lock.writeLock().unlock();
            }
        }, "写锁").start();

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        String address = "http://sports.sina.com.cn/nba/live.html?id=2015050405";
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        InputStreamReader input = new InputStreamReader(connection.getInputStream(), "utf-8");
        BufferedReader reader = new BufferedReader(input);
        String line = "";
        StringBuffer stringBuffer = new StringBuffer();
        while((line = reader.readLine()) != null){
            stringBuffer.append(line);
        }

        String string = stringBuffer.toString();

    }


}
