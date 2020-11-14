
package nachos.threads;

import nachos.machine.*;

public class Test {

    public static void initiateTest() {
        JoinTest.performTest();
        Condition2Test.performTest();
        AlarmTest.performTest();
    }

}

class JoinTest {
    public JoinTest() {

    }

    public static void performTest() {

        System.out.println();
        System.out.println("--------------------------------");
        System.out.println("testing for task 1 initiated");
        System.out.println("--------------------------------");

        KThread t1 = new KThread(new PingTest(1)).setName("forked thread 1");
        t1.fork();
        KThread t2 = new KThread(new PingTest(2)).setName("forked thread 2");
        t2.fork();

        t1.join();

        KThread t3 = new KThread(new PingTest(3)).setName("forked thread 3");
        t3.fork();

        KThread t4 = new KThread(new PingTest(4)).setName("forked thread 4");
        t4.fork();

        t3.join();
        t2.join();
        t4.join();

        System.out.println("---------------------------------------------");
        System.out.println("testing for task 1 finished");
        System.out.println("---------------------------------------------");

    }

    private static class PingTest implements Runnable {

        PingTest(int which) {
            this.which = which;
        }

        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.println("*** thread " + which + " looped " + i + " times");
                KThread.currentThread().yield();
            }
        }

        private final int which;

    }
}

class Condition2Test {

    private static Communicator communicator = new Communicator();

    public Condition2Test() {
        communicator = new Communicator();
    }

    public static void performTest() {
        System.out.println("testing for task 2 & 4 initiated");
        System.out.println("-----------------------------------");

        KThread l1 = new KThread(new Listener(1, communicator)).setName("listener thread 1");
        KThread l2 = new KThread(new Listener(2, communicator)).setName("listener thread 2");
        KThread l3 = new KThread(new Listener(2, communicator)).setName("listener thread 2");

        KThread s1 = new KThread(new Speaker(1, communicator)).setName("speaker thread 1");
        KThread s2 = new KThread(new Speaker(2, communicator)).setName("speaker thread 2");
        KThread s3 = new KThread(new Speaker(3, communicator)).setName("speaker thread 3");

        l1.fork();
        l2.fork();
        l3.fork();

        s1.fork();
        s2.fork();
        s3.fork();

        l1.join();
        l2.join();
        l3.join();

        s1.join();
        s2.join();
        s3.join();

        System.out.println("---------------------------------------------");
        System.out.println("testing for task 2 & 4 finished");
        System.out.println("---------------------------------------------");

    }

    private static class Listener implements Runnable {
        Listener(int which, Communicator communicator) {

            this.which = which;
            this.communicator = communicator;
        }

        public void run() {
            for (int i = 0; i < 5; i++) {
                KThread.yield();
                int listenedValue = communicator.listen();
                // System.out.println(KThread.currentThread().getName() + " listened " +
                // listenedValue);
                KThread.yield();
            }
        }

        private int which;
        private Communicator communicator;
    }

    private static class Speaker implements Runnable {
        Speaker(int which, Communicator communicator) {

            this.which = which;
            this.communicator = communicator;
        }

        public void run() {
            for (int i = 0; i < 5; i++) {
                KThread.yield();

                communicator.speak(i);
                // System.out.println(KThread.currentThread().getName() + " spoke " + i);
                KThread.yield();
            }
        }

        private int which;
        private Communicator communicator;
    }
}

class AlarmTest {

    private static class AlarmTestRunnable implements Runnable {

        AlarmTestRunnable(long time, Alarm alarm) {
            this.time = time;
            this.alarm = alarm;
        }

        public void run() {
            System.out.println(KThread.currentThread().getName() + " rings at " + Machine.timer().getTime());
            alarm.waitUntil(time);
            System.out.println(KThread.currentThread().getName() + " rings at " + Machine.timer().getTime());
        }

        private final long time;
        private final Alarm alarm;

    }

    public static void performTest() {
        System.out.println("testing for task 3 initiated");
        System.out.println("--------------------------------");

        long time1 = 800000;
        long time2 = 900000;
        long time3 = 500000;

        Alarm alarm = ThreadedKernel.alarm;

        // create threads for each a test object
        KThread t1 = new KThread(new AlarmTestRunnable(time1, alarm)).setName("Alarm thread 1");
        KThread t2 = new KThread(new AlarmTestRunnable(time2, alarm)).setName("Alarm thread 2");
        KThread t3 = new KThread(new AlarmTestRunnable(time3, alarm)).setName("Alarm thread 3");

        // run threads with alarms
        t1.fork();
        t2.fork();
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
