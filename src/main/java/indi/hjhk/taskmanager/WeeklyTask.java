package indi.hjhk.taskmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WeeklyTask extends RepeatTask{
    public static final int TASK_TYPE_SEQ = 3;
    public static final String TASK_TYPE_NAME = "每周更新任务";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");


    public LocalTime repeatTime;
    private int dayOfWeek = 1;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(dayOfWeek);
        out.writeObject(repeatTime);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        dayOfWeek = in.readInt();
        repeatTime = (LocalTime) in.readObject();
    }

    @Override
    public Task clone() {
        WeeklyTask newTask = (WeeklyTask) super.clone();
        newTask.repeatTime = repeatTime;
        newTask.dayOfWeek = dayOfWeek;
        return newTask;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public String getExpireDateTag() {
        return String.format("[%s%s更新]", Data.Constants.dayOfWeekName.get(dayOfWeek-1), TIME_FORMATTER.format(repeatTime));
    }

    @Override
    public LocalDateTime getExpireDate() {
        if (doneTime.getDayOfWeek().equals(DayOfWeek.of(dayOfWeek))){
            // same day of week, look into time
            if (doneTime.toLocalTime().isBefore(repeatTime))
                return LocalDateTime.of(doneTime.toLocalDate(), repeatTime);
            else
                return LocalDateTime.of(doneTime.toLocalDate().plusDays(7), repeatTime);
        }else{
            int doneDayOfWeek = doneTime.getDayOfWeek().getValue();
            int daysInterval = (dayOfWeek - doneDayOfWeek + 7) % 7;
            return LocalDateTime.of(doneTime.toLocalDate().plusDays(daysInterval), repeatTime);
        }
    }

    @Override
    public String getTypeName() {
        return TASK_TYPE_NAME;
    }

    @Override
    public int getTypeSeq() {
        return TASK_TYPE_SEQ;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) return;
        this.dayOfWeek = dayOfWeek;
    }
}
