/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncahos.proj1;

import nachos.threads.*;
import nachos.machine.*;

/**
 *
 * @author ana
 */
public class test1 {

    public static void initiate_test1(){

        new join_test.perform_test1();
        new condition2_test.perform_test1();
        new alarm_test.perform_test1();
    }

}

class join_test{
    public join_test(){

    }

    public void perform_test1(){
        System.out.println("testing for task 1 initiated");
        System.out.println("--------------------------------");

        KThread t0 = new kThread(new PingTest(0)).setName("forked thread 0");
        System.out.println("forked thread 0 and joining...");
        t0.fork();
        t0.join();
        System.out.println("joined with thread 0");

        new KThread(new PingTest(1)).setName("forked thread 1").fork();
        new KThread(new PingTest(2)).setName("forked thread 2").fork();
        new PingTest(0).run();

        System.out.println();

        KThread t1 = new kThread(new PingTest(0)).setName("forked thread 1");
        KThread t2 = new kThread(new PingTest(0)).setName("forked thread 2");

        t1.fork();
        t2.fork();

        t1.join();
        t2.join();

        new PingTest(0).run();

        System.out.println("---------------------------------------------");
        System.out.println("testing for task 1 finished");
        System.out.println("---------------------------------------------");




    }

    private static class PingTest implements Runnable {

        PingTest ( int which){
            this.which = which;
        }
        public void run(){
            for(int i = 0 ; i < 5 ; i++){
                System.out.println("*** thread" + which + "looped" + i + "times");
                KThread.yield();
            }
        }
        private final int which;

    }
}

class condition2_test {

    public condition2_test(){
        com = new Communicator();
    }

    public void perform_test1(){
        System.out.println("testing for task 2 & 4 initiated");
        System.out.println("-----------------------------------");

        KThread l1 = new KThread(new Listener(1,com)).setName("listener thread 1");
        KThread l2 = new KThread(new Listener(2,com)).setName("listener thread 2");

        KThread s1 = new KThread(new Speaker(1,com)).setName("speaker thread 1");
        KThread s2 = new KThread(new Speaker(2,com)).setName("speaker thread 2");
        KThread s3 = new KThread(new Speaker(3,com)).setName("speaker thread 3");

        l1.fork();
        l2.fork();
        s1.fork();
        s2.fork();
        s3.fork();

        l1.join();
        l2.join();
        s1.join();
        s2.join();
        s3.join();

        System.out.println("---------------------------------------------");
        System.out.println("testing for task 2 & 4 finished");
        System.out.println("---------------------------------------------");


    }

    private static class Listener implements Runnable {
        Listener(int which, Communicator com){

            this.which = which ;
            this.com = com;
        }
        public void run(){
            for(int i =0;i<3;i++){
                KThread.yield();
                com.listen();
                KThread.yield();
            }
        }

        private int which;
        private Communicator com;
    }
    Communicator com;
}

class alarm_test {
    private static class alarm_test_Runnable implements Runnable {
        alarm_test_Runnable (long time , Alarm alarm){
            this.time = time ;
            this.alarm = alarm ;
        }
        public void run(){
            System.out.println(KThread.currentThread().getName()+" rings at"+Machine.timer().getTime());
            alarm.waitUntil(time);
            System.out.println(KThread.currentThread().getName()+" rings at"+Machine.timer().getTime());
        }

        private final long time;
        private final Alarm alarm;

    }
    public void perform_test1(){
        System.out.println("testing for task 3 initiated");
        System.out.println("--------------------------------");

        long time1 = 800000;
        long time2 = 900000;
        long time3 = 500000;

        Alarm alarm = new Alarm();

        //create threads for each a test object
        KThread t1 = new KThread(new alarm_test_runnable(time1 , alarm)).setName("Alarm thread 1");
        KThread t2 = new KThread(new alarm_test_runnable(time1 , alarm)).setName("Alarm thread 2");
        KThread t3 = new KThread(new alarm_test_runnable(time1 , alarm)).setName("Alarm thread 3");

        //run threads with alarms
        alarm.waitUntil(time1);
        t2.fork();
        alarm.waitUntil(time2);
        t1.fork();
        alarm.waitUntil(time3);
        t3.fork();

        KThread.yield();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("---------------------------------------------");
        System.out.println("testing for task 3 finished");
        System.out.println("---------------------------------------------");


    }
}


