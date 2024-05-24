package indi.hjhk.taskmanager.task;

import java.util.Comparator;

public class IdentifiedTask {
    public int id;
    public Task task;

    public IdentifiedTask(int id, Task task) {
        this.id = id;
        this.task = task;
    }

    public static Comparator<IdentifiedTask> comparator = new Comparator<IdentifiedTask>() {
        @Override
        public int compare(IdentifiedTask o1, IdentifiedTask o2) {
            return Task.comparator.compare(o1.task, o2.task);
        }
    };
}
