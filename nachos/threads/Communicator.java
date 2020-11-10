package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>, and multiple
 * threads can be waiting to <i>listen</i>. But there should never be a time
 * when both a speaker and a listener are waiting, because the two threads can
 * be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */

    private boolean spoken = false;
    private int word;

    private Lock lock = new Lock();
    private Condition2 speaker = new Condition2(lock);
    private Condition2 listener = new Condition2(lock);

    public Communicator() {
        this.spoken = false;
        this.lock = new Lock();
        this.speaker = new Condition2(lock);
        this.listener = new Condition2(lock);
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param word the integer to transfer.
     */
    public void speak(int word) {

        this.lock.acquire();

        while (this.spoken) {
            this.listener.wakeAll();
            this.speaker.sleep();
        }

        this.word = word;
        this.spoken = true;
        this.listener.wakeAll();
        this.speaker.sleep();

        this.lock.release();

    }

    /**
     * Wait for a thread to speak through this communicator, and then return the
     * <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return the integer transferred.
     */
    public int listen() {

        this.lock.acquire();

        while (!this.spoken) {
            this.listener.sleep();
        }

        int listenedWord = this.word;
        this.speaker.wakeAll();
        this.spoken = false;

        this.lock.release();

        return listenedWord;
    }
}
