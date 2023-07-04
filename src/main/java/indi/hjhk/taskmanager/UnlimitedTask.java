package indi.hjhk.taskmanager;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;

public class UnlimitedTask extends Task{

    public static final int TASK_TYPE_SEQ = 1;
    public static final String TASK_TYPE_NAME = "不限时任务";
    public boolean done;

    @Override
    public Task clone() {
        UnlimitedTask newTask = (UnlimitedTask) super.clone();
        newTask.done = done;
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
    public String getCompletionTag(LocalDateTime curTime) {
        return done ? "[✔已完成]" : "[✘未完成]";
    }

    @Override
    public String getExpireDateTag(LocalDateTime curTime) {
        return "[无时限]";
    }

    @Override
    public LocalDateTime getExpireDate() {
        return LocalDateTime.MAX;
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
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        done = in.readBoolean();
    }
}
