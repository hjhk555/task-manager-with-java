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
        long callTime;
        long curTime = System.currentTimeMillis();
        if (aligned) {
            callTime = (curTime /interval +1) *interval;
        } else {
            callTime = curTime +interval;
        }

        while (!Thread.interrupted() && repeats != 0){
            curTime = System.currentTimeMillis();
            try {
                if (callTime > curTime) Thread.sleep(callTime - curTime);
            }catch (InterruptedException e){
                return;
            }

            curTime = System.currentTimeMillis();
            if (callTime <= curTime) {
                for (TimedCall timedCall : timedCalls) {
                    timedCall.call();
                }
                if (repeats > 0) repeats--;

                if (aligned){
                    callTime = callTime + interval;
                }else{
                    callTime = curTime + interval;
                }
            }
        }
    }
}
