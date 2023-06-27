package indi.hjhk.taskmanager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NormalTask extends Task{
    private static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");
    public LocalDateTime expireDate;
    public boolean done;

    @Override
    public String toString(LocalDateTime curTime) {
        StringBuilder stringBuilder = new StringBuilder();
        if (done){
            stringBuilder.append("[✔已完成]");
        }else{
            stringBuilder.append(String.format("[%s未完成][%s到期]", getEmergeTag(curTime, expireDate), DATE_FORMAT.format(expireDate)));
        }
        stringBuilder.append(title);
        return stringBuilder.toString();
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public LocalDateTime getExpireDate() {
        return expireDate;
    }
}
