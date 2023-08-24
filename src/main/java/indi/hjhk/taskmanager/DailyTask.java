package indi.hjhk.taskmanager;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DailyTask extends RepeatTask{
    public static final int TASK_TYPE_SEQ = 2;
    public static final String TASK_TYPE_NAME = "每天更新任务";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public LocalTime repeatTime;

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public String getExpireDateTag() {
        return String.format("[每天%s更新]", TIME_FORMATTER.format(repeatTime));
    }

    @Override
    public LocalDateTime getExpireDate() {
        if (doneTime.toLocalTime().isBefore(repeatTime))
            return LocalDateTime.of(doneTime.toLocalDate(), repeatTime);
        else{
            return LocalDateTime.of(doneTime.toLocalDate().plusDays(1), repeatTime);
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(repeatTime);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        repeatTime = (LocalTime) in.readObject();
    }

    @Override
    public Task clone() {
        DailyTask newTask = (DailyTask) super.clone();
        newTask.repeatTime = repeatTime;
        return newTask;
    }

    @Override
    public String getTypeName() {
        return TASK_TYPE_NAME;
    }

    @Override
    public int getTypeSeq() {
        return TASK_TYPE_SEQ;
    }
}
