package indi.hjhk.taskmanager;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

public abstract class Task implements Serializable {
    public String title;
    public String content;
    public boolean isAlerted;
    public static Comparator<Task> comparator = new Comparator<Task>() {
        @Override
        // if t1 should be sorted before t2, return negative
        public int compare(Task t1, Task t2) {
            boolean t1Done = t1.isDone();
            boolean t2Done = t2.isDone();
            if (t1Done != t2Done){
                return t1Done ? 1 : -1;
            }
            LocalDateTime t1Expire = t1.getExpireDate();
            LocalDateTime t2Expire = t2.getExpireDate();
            if (t1Expire.isEqual(t2Expire)) return 0;
            if (t1Done){
                return t1Expire.isBefore(t2Expire) ? 1 : -1;
            }else{
                return t1Expire.isAfter(t2Expire) ? 1 : -1;
            }
        }
    };

    protected static String getEmergeTag(LocalDateTime curTime, LocalDateTime expireDate){
        if (expireDate.isBefore(curTime)) return "⚠ ⚠ ⚠";
        long daysLeft = Duration.between(curTime, expireDate).toDays();
        if (daysLeft < Data.Config.ALERT_LEVEL3_THRESHOLD) return "★★★";
        if (daysLeft < Data.Config.ALERT_LEVEL2_THRESHOLD) return "★★";
        if (daysLeft < Data.Config.ALERT_LEVEL1_THRESHOLD) return "★";
        return "";
    }

    public abstract String toString(LocalDateTime curTime);
    public abstract boolean isDone();
    public abstract LocalDateTime getExpireDate();
}