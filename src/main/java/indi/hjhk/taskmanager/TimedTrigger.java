package indi.hjhk.taskmanager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class TimedTrigger implements Runnable {
    private int repeats;
    private final int interval;
    private final boolean aligned;

    private final ArrayList<TimedCall> timedCalls = new ArrayList<>();

    /**
     * create a timed trigger
     * @param repeats repeat counts, negative for infinite
     * @param interval repeat interval in ms
     * @param aligned aligned trigger
     */
    public TimedTrigger(int repeats, int interval, boolean aligned) {
        this.repeats = repeats;
        this.interval = interval;
        this.aligned = aligned;
    }

    public void registerCall(TimedCall timedcall){
        synchronized (timedCalls) {
            timedCalls.add(timedcall);
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted() && repeats != 0){
            try {
                if (aligned) {
                    Thread.sleep(interval - System.currentTimeMillis() % interval);
                } else {
                    Thread.sleep(interval);
                }
            }catch (InterruptedException e){
                return;
            }

            LocalDateTime curTime = LocalDateTime.now();

            for (TimedCall timedCall: timedCalls){
                timedCall.call(curTime);
            }

            if (repeats>0) repeats--;
        }
    }
}
