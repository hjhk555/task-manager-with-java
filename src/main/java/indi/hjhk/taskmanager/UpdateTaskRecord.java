package indi.hjhk.taskmanager;

import indi.hjhk.taskmanager.task.Task;

public class UpdateTaskRecord extends ActionRecord{
    private final Task oldTask;
    private final Task newTask;
    private final int taskId;

    public UpdateTaskRecord(Task oldTask, Task newTask, int taskId) {
        this.oldTask = oldTask;
        this.newTask = newTask;
        this.taskId = taskId;
    }

    @Override
    public void redo() {
        Data.Tasks.taskList.set(taskId, newTask);
    }

    @Override
    public void undo() {
        Data.Tasks.taskList.set(taskId, oldTask);
    }
}
