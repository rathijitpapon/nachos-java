package nachos.threads;

import nachos.machine.*;

import javax.crypto.Mac;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {

        boolean initStatus = Machine.interrupt().disable();
    	
        while (!waitQueue.isEmpty()) {
            PriorThread priorThread = waitQueue.peek();
            if (priorThread.priority <= Machine.timer().getTime())
            { 
                waitQueue.poll();
                priorThread.thread.ready();
            }
            else
                break;
            
        }
        
        Machine.interrupt().restore(initStatus);
        KThread.currentThread().yield();

    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
	//long wakeTime = Machine.timer().getTime() + x;
	//while (wakeTime > Machine.timer().getTime())
       // KThread.yield();
       
       boolean initStatus = Machine.interrupt().disable();
	
       long wakeTime = Machine.timer().getTime() + x;

       
       KThread currentThread = KThread.currentThread();
       PriorThread priorThread = new PriorThread(currentThread, wakeTime);
       waitQueue.add(priorThread);
       currentThread.sleep();

       Machine.interrupt().restore(initStatus);
    }

    private PriorityQueue<PriorThread> waitQueue = new PriorityQueue(new PriorThread.Comp());

    
}

class PriorThread {
    public KThread thread;
    public long priority;

    public PriorThread(KThread t, long p) {
        thread = t;
        priority = p;
    }

    public static class Comp implements Comparator<PriorThread> {
        public int compare(PriorThread a, PriorThread b) {
            if (a.priority > b.priority)
                return 1;
            else if (a.priority < b.priority)
                return -1;
            return 0;
        }
    }
}


