package indi.hjhk.taskmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

public abstract class Task{
    public String title;
    public String content;
    public boolean isAlerted;
    public static Comparator<Task> comparator = new Comparator<Task>() {
        @Override
        // if t1 should be sorted before t2, return negative
        public int compare(Task t1, Task t2) {
            boolean t1Done = t1.isDone();
            boolean t2Done = t2.isDone();
            if (t1Done != t2Done){
                return t1Done ? 1 : -1;
            }
            LocalDateTime t1Expire = t1.getExpireDate();
            LocalDateTime t2Expire = t2.getExpireDate();
            if (t1Expire.isEqual(t2Expire)) return 0;
            if (t1Done){
                return t1Expire.isBefore(t2Expire) ? 1 : -1;
            }else{
                return t1Expire.isAfter(t2Expire) ? 1 : -1;
            }
        }
    };

    protected static String getEmergeTag(LocalDateTime curTime, LocalDateTime expireDate){
        if (expireDate.isBefore(curTime)) return "⚠⚠⚠";
        long daysLeft = Duration.between(curTime, expireDate).toDays();
        if (daysLeft < Data.Config.ALERT_LEVEL3_THRESHOLD) return "★★★";
        if (daysLeft < Data.Config.ALERT_LEVEL2_THRESHOLD) return "★★";
        if (daysLeft < Data.Config.ALERT_LEVEL1_THRESHOLD) return "★";
        return "";
    }

    public static ObservableList<String> getTaskTypeList(){
        ObservableList<String> taskTypeList = FXCollections.observableArrayList();
        taskTypeList.add(NormalTask.TASK_TYPE_SEQ, NormalTask.TASK_TYPE_NAME);
        taskTypeList.add(UnlimitedTask.TASK_TYPE_SEQ, UnlimitedTask.TASK_TYPE_NAME);
        return taskTypeList;
    }

    public void cloneSharedFrom(Task from){
        title = from.title;
        content = from.content;
        isAlerted = from.isAlerted;
    }

    public static Task readTaskExternal(ObjectInput in){
        try{
            int typeSeq = in.readInt();
            Task newTask = switch (typeSeq){
                case NormalTask.TASK_TYPE_SEQ -> new NormalTask();
                case UnlimitedTask.TASK_TYPE_SEQ -> new UnlimitedTask();
                default -> null;
            };
            if (newTask == null) return null;
            newTask.readExternal(in);
            return newTask;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeSharedExternal(ObjectOutput out) throws IOException {
        out.writeInt(getTypeSeq());
        MathUtils.writeEncodedString(title, out);
        MathUtils.writeEncodedString(content, out);
        out.writeBoolean(isAlerted);
    }

    public void readSharedExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        title = MathUtils.readEncodeedString(in);
        content = MathUtils.readEncodeedString(in);
        isAlerted = in.readBoolean();
    }

    public abstract void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;
    public abstract void writeExternal(ObjectOutput out) throws IOException;
    public abstract Task clone();
    public abstract String toString(LocalDateTime curTime);
    public abstract void finish();
    public abstract boolean isDone();
    public abstract LocalDateTime getExpireDate();
    public abstract String getTypeName();
    public abstract int getTypeSeq();
}