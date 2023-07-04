package indi.hjhk.taskmanager;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;

public abstract class RepeatTask extends Task{
    public LocalDateTime doneTime = LocalDateTime.now();

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

    @Override
    public Task clone() {
        RepeatTask newTask = (RepeatTask) super.clone();
        newTask.doneTime = doneTime;
        return newTask;
    }

    @Override
    public void finish() {
        doneTime = LocalDateTime.now();
    }
}
