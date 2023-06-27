package indi.hjhk.taskmanager;

import java.time.LocalDateTime;

public class UnlimitedTask extends Task{

    public boolean done;

    @Override
    public String toString(LocalDateTime curTime) {
        StringBuilder stringBuilder = new StringBuilder();
        if (done){
            stringBuilder.append("[✔已完成]");
        }else{
            stringBuilder.append("[未完成][无时限]");
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
        return LocalDateTime.MAX;
    }
}
