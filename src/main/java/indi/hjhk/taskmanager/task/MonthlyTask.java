package indi.hjhk.taskmanager.task;

import indi.hjhk.taskmanager.Data;
import indi.hjhk.taskmanager.utils.DateTimeUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MonthlyTask extends RepeatTask{
    public static final int TASK_TYPE_SEQ = 4;
    public static final String TASK_TYPE_NAME = "每月更新任务";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public LocalTime repeatTime;
    private int dayOfMonth = 1;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(dayOfMonth);
        out.writeObject(repeatTime);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        dayOfMonth = in.readInt();
        repeatTime = (LocalTime) in.readObject();
    }

    @Override
    public Task clone() {
        MonthlyTask newTask = (MonthlyTask) super.clone();
        newTask.repeatTime = repeatTime;
        newTask.dayOfMonth = dayOfMonth;
        return newTask;
    }

    @Override
    public String getExpireDateTag() {
        return String.format("[每月%d日%s%s更新]", dayOfMonth, getNextReadyTag(), TIME_FORMATTER.format(repeatTime));
    }

    @Override
    public LocalDateTime getExpireDate() {
        return DateTimeUtils.nextDayTime(doneTime, dayOfMonth, repeatTime);
    }

    @Override
    public String getTypeName() {
        return TASK_TYPE_NAME;
    }

    @Override
    public int getTypeSeq() {
        return TASK_TYPE_SEQ;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        if (dayOfMonth < 1 || dayOfMonth > 31) return;
        this.dayOfMonth = dayOfMonth;
    }
}
