package indi.hjhk.taskmanager.task;

import indi.hjhk.taskmanager.Data;
import indi.hjhk.taskmanager.MathUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    protected static String getEmergeTag(LocalDateTime expireDate){
        if (!expireDate.isAfter(Data.getCurrentTime())) return "⚠⚠⚠";
        long daysLeft = Duration.between(Data.getCurrentTime(), expireDate).toDays();
        if (daysLeft < Data.Config.ALERT_LEVEL3_THRESHOLD) return "★★★";
        if (daysLeft < Data.Config.ALERT_LEVEL2_THRESHOLD) return "★★";
        if (daysLeft < Data.Config.ALERT_LEVEL1_THRESHOLD) return "★";
        return "✘";
    }

    public static ObservableList<String> getTaskTypeList(){
        ObservableList<String> taskTypeList = FXCollections.observableArrayList();
        taskTypeList.add(NormalTask.TASK_TYPE_SEQ, NormalTask.TASK_TYPE_NAME);
        taskTypeList.add(UnlimitedTask.TASK_TYPE_SEQ, UnlimitedTask.TASK_TYPE_NAME);
        taskTypeList.add(DailyTask.TASK_TYPE_SEQ, DailyTask.TASK_TYPE_NAME);
        taskTypeList.add(WeeklyTask.TASK_TYPE_SEQ, WeeklyTask.TASK_TYPE_NAME);
        taskTypeList.add(MonthlyTask.TASK_TYPE_SEQ, MonthlyTask.TASK_TYPE_NAME);
        return taskTypeList;
    }

    public static Task newTypedTask(int seq){
        return switch (seq){
            case NormalTask.TASK_TYPE_SEQ -> new NormalTask();
            case UnlimitedTask.TASK_TYPE_SEQ -> new UnlimitedTask();
            case DailyTask.TASK_TYPE_SEQ -> new DailyTask();
            case WeeklyTask.TASK_TYPE_SEQ -> new WeeklyTask();
            case MonthlyTask.TASK_TYPE_SEQ -> new MonthlyTask();
            default -> null;
        };
    }

    public static Task readTaskExternal(ObjectInput in){
        try{
            int typeSeq = in.readInt();
            Task newTask = newTypedTask(typeSeq);
            if (newTask == null) return null;
            newTask.readExternal(in);
            return newTask;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        MathUtils.writeEncodedString(title, out);
        MathUtils.writeEncodedString(content, out);
        out.writeBoolean(isAlerted);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        title = MathUtils.readEncodeedString(in);
        content = MathUtils.readEncodeedString(in);
        isAlerted = in.readBoolean();
    }

    public Task clone(){
        Task newTask = newTypedTask(getTypeSeq());
        newTask.title = title;
        newTask.content = content;
        newTask.isAlerted = isAlerted;
        return newTask;
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getCompletionTag());
        if (!isAlerted) stringBuilder.append("[勿扰]");
        if (!isDone()) stringBuilder.append(getExpireDateTag());
        stringBuilder.append(title);
        return stringBuilder.toString();
    }

    /**
     * 将任务更新为完成状态
     */
    public abstract void finish();

    /**
     * 由于可重复任务不会永久完成，因此返回false
     * @return 该任务是否永久完成
     */
    public abstract boolean isDone();
    public abstract String getCompletionTag();
    public abstract String getExpireDateTag();
    public abstract LocalDateTime getExpireDate();
    public abstract String getTypeName();
    public abstract int getTypeSeq();
}