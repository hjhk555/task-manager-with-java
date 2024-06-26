package indi.hjhk.taskmanager.task;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NormalTask extends Task{
    public static final int TASK_TYPE_SEQ = 0;
    public static final String TASK_TYPE_NAME = "单次任务";
    private static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");
    public LocalDateTime expireDate;
    public boolean done;

    @Override
    public Task clone() {
        NormalTask newTask = (NormalTask) super.clone();
        newTask.done = done;
        newTask.expireDate = expireDate;
        return newTask;
    }

    @Override
    public void finish() {
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public String getCompletionTag() {
        return done ? "[✔已完成]" : String.format("[%s未完成]", getEmergeTag(expireDate));
    }

    @Override
    public String getExpireDateTag() {
        return String.format("[%s到期]", DATE_FORMAT.format(expireDate));
    }

    @Override
    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    @Override
    public String getTypeName() {
        return TASK_TYPE_NAME;
    }

    @Override
    public int getTypeSeq() {
        return TASK_TYPE_SEQ;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeBoolean(done);
        out.writeObject(expireDate);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        done = in.readBoolean();
        expireDate = (LocalDateTime) in.readObject();
    }
}
