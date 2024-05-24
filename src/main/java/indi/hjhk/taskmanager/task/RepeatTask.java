package indi.hjhk.taskmanager.task;

import indi.hjhk.taskmanager.Data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;

public abstract class RepeatTask extends Task{
    public LocalDateTime doneTime = Data.getCurrentTime();

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(doneTime);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        doneTime = (LocalDateTime) in.readObject();
    }

    public String getNextReadyTag(){
        LocalDateTime nextReadyTime = getExpireDate();
        if (!Data.getCurrentTime().isBefore(nextReadyTime)) return "";
        return String.format("(%d月%d日)", nextReadyTime.getMonthValue(), nextReadyTime.getDayOfMonth());
    }

    @Override
    public String getCompletionTag() {
        return !Data.getCurrentTime().isBefore(getExpireDate()) ? "[✘未完成]" : "[\uD83D\uDD04已完成]";
    }

    @Override
    public Task clone() {
        RepeatTask newTask = (RepeatTask) super.clone();
        newTask.doneTime = doneTime;
        return newTask;
    }

    @Override
    public void finish() {
        doneTime = Data.getCurrentTimeToMinute();;
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
