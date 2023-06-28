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
        UnlimitedTask newTask = new UnlimitedTask();
        newTask.cloneSharedFrom(this);
        newTask.done = done;
        return newTask;
    }

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
    public void finish() {
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
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
        writeSharedExternal(out);
        out.writeBoolean(done);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        readSharedExternal(in);
        done = in.readBoolean();
    }
}
