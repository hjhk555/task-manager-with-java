package indi.hjhk.taskmanager;

import indi.hjhk.global.GlobalConfig;
import indi.hjhk.taskmanager.gui.MainGUI;
import indi.hjhk.taskmanager.task.IdentifiedTask;
import indi.hjhk.taskmanager.task.RepeatTask;
import indi.hjhk.taskmanager.task.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Data {
    public static String appVersion = "1.3.0";
    public static String appPubDate = "2024/5/24";
    public static String authIdentifier = "github.com/hjhk555";
    public static String appInfo = "制作人："+authIdentifier+"\n版本："+appVersion+"\n发行日期："+appPubDate;
    private static LocalDateTime curTime = getCurrentTimeToMinute();
    public static synchronized void updateCurrentTime(){
        curTime = getCurrentTimeToMinute();
    }

    public static synchronized LocalDateTime getCurrentTime(){
        return curTime;
    }

    public static LocalDateTime getCurrentTimeToMinute(){
        LocalDateTime curTime = LocalDateTime.now();
        return LocalDateTime.of(curTime.toLocalDate(), LocalTime.of(curTime.getHour(), curTime.getMinute()));
    }

    public static class Constants{
        public static final ObservableList<String> dayOfWeekName = FXCollections.observableArrayList("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日");
        public static final ObservableList<String> hourList = FXCollections.observableArrayList(IntStream.range(0, 24).mapToObj(num -> String.format("%02d", num)).collect(Collectors.toList()));
        public static final ObservableList<String> minuteList = FXCollections.observableArrayList(IntStream.range(0, 60).mapToObj(num -> String.format("%02d", num)).collect(Collectors.toList()));
        public static final ObservableList<String> dayOfMonthList = FXCollections.observableArrayList(IntStream.range(1, 32).mapToObj(String::valueOf).collect(Collectors.toList()));
        public static final FileChooser.ExtensionFilter defaultFilter = new FileChooser.ExtensionFilter("全部文件", "*.*");
        public static final FileChooser.ExtensionFilter taskListFilter = new FileChooser.ExtensionFilter("任务文件", "*"+ Tasks.taskFileSuffix);
        public static final File userDesktop = new File(System.getProperty("user.home"), "/Desktop");
        public static final File appRoot = new File(".");
    }

    public static class Config{
        public static String configFileName = "settings.conf";
        public static GlobalConfig config = new GlobalConfig(configFileName);
        public static int ALERT_INTERVAL;           // minutes
        public static int ACTIVE_THRESHOLD;         // minutes
        public static int LEAVE_THRESHOLD;          // minutes
        public static int PAUSE_LENGTH;             // minutes
        public static int CURSOR_DIFF;              // pixels
        public static int ALERT_LEVEL1_THRESHOLD;   // days
        public static int ALERT_LEVEL2_THRESHOLD;   // days
        public static int ALERT_LEVEL3_THRESHOLD;   // days
        public static String DEFAULT_EXPORT_FILENAME;

        static {
            readConfig();
        }
        
        public static void readConfig(){
            config.read();
            ALERT_INTERVAL = config.getIntOrDefault("alert_interval", 5);
            ACTIVE_THRESHOLD = config.getIntOrDefault("active_threshold", 60);
            LEAVE_THRESHOLD = config.getIntOrDefault("leave_threshold", 10);
            PAUSE_LENGTH = config.getIntOrDefault("pause_length", 30);
            CURSOR_DIFF = config.getIntOrDefault("cursor_diff", 1);
            ALERT_LEVEL1_THRESHOLD = config.getIntOrDefault("alert_level1_threshold", 7);
            ALERT_LEVEL2_THRESHOLD = config.getIntOrDefault("alert_level2_threshold", 3);
            ALERT_LEVEL3_THRESHOLD = config.getIntOrDefault("alert_level3_threshold", 1);
            DEFAULT_EXPORT_FILENAME = config.getStringOrDefault("default_export_filename", Tasks.taskFilePrimaryName);
        }
        
        public static void writeConfig(){
            config.setInt("alert_interval", ALERT_INTERVAL);
            config.setInt("active_threshold", ACTIVE_THRESHOLD);
            config.setInt("leave_threshold", LEAVE_THRESHOLD);
            config.setInt("pause_length", PAUSE_LENGTH);
            config.setInt("cursor_diff", CURSOR_DIFF);
            config.setInt("alert_level1_threshold", ALERT_LEVEL1_THRESHOLD);
            config.setInt("alert_level2_threshold", ALERT_LEVEL2_THRESHOLD);
            config.setInt("alert_level3_threshold", ALERT_LEVEL3_THRESHOLD);
            config.setString("default_export_filename", DEFAULT_EXPORT_FILENAME);
            config.write();
        }
    }

    public static class Tasks {
        public static ArrayList<Task> taskList;
        public static String taskFilePrimaryName = "tasks";
        public static String taskFileSuffix = ".tlist";
        public static String taskFileName = taskFilePrimaryName + taskFileSuffix;

        static {
            taskList = readTasksFromFile(taskFileName);
        }

        public static ArrayList<Task> readTasksFromFile(String fileName){
            try {
                ArrayList<Task> tasks = new ArrayList<>();
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName));
                int taskSize = objectInputStream.readInt();
                MathUtils.setCode(taskSize);
                for (int i=0; i<taskSize; i++) {
                    Task newTask = Task.readTaskExternal(objectInputStream);
                    tasks.add(newTask);
                }
                objectInputStream.close();
                return tasks;
            } catch (IOException e) {
                return new ArrayList<>();
            }
        }

        public static void addTask(Task newTask){
            History.addActionRecord(new UpdateTaskRecord(null, newTask, taskList.size()));
            taskList.add(newTask);
            writeTasksToFile(taskFileName);
        }

        public static void updateTask(int index, Task newTask){
            History.addActionRecord(new UpdateTaskRecord(taskList.get(index), newTask, index));
            taskList.set(index, newTask);
            writeTasksToFile(taskFileName);
        }

        public static void removeTask(int index){
            if (taskList.get(index) == null) return;
            updateTask(index, null);
        }

        public static void writeTasksToFile(String fileName){
            try{
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
                int taskSize = taskList.stream().mapToInt(value -> value == null ? 0 : 1).sum();
                MathUtils.setCode(taskSize);
                objectOutputStream.writeInt(taskSize);
                for (Task task : taskList)
                    if (task != null) {
                        objectOutputStream.writeInt(task.getTypeSeq());
                        task.writeExternal(objectOutputStream);
                    }
                objectOutputStream.close();
            } catch (IOException ignored) {
            }
        }

        public static IdentifiedTask getIdentifiedTask(int id){
            Task task = taskList.get(id);
            if (task == null) return null;
            return new IdentifiedTask(id, task);
        }

        public static ObservableList<IdentifiedString> getSortedTaskInfo(List<Task> taskList){
            ArrayList<IdentifiedTask> sortedList = new ArrayList<>();
            for (int i=0; i<taskList.size(); i++){
                Task task = taskList.get(i);
                if (task != null) sortedList.add(new IdentifiedTask(i, taskList.get(i)));
            }
            sortedList.sort(IdentifiedTask.comparator);
            ObservableList<IdentifiedString> taskInfo = FXCollections.observableArrayList();
            for (IdentifiedTask identifiedTask: sortedList)
                taskInfo.add(new IdentifiedString(identifiedTask.id, identifiedTask.task.toString()));
            return taskInfo;
        }

        public static class EmergeTaskList{
            public ArrayList<Task> emergeTasks = new ArrayList<>();
            public ArrayList<Task> exceedTasks = new ArrayList<>();
            public boolean requireAlert = false;
        }

        public static EmergeTaskList getEmergeTaskList(){
            EmergeTaskList emergeTaskList = new EmergeTaskList();
            for (Task task : taskList){
                if (task == null || task.isDone()) continue;
                LocalDateTime expiredDate = task.getExpireDate();
                if (!expiredDate.isAfter(getCurrentTime())){
                    emergeTaskList.exceedTasks.add(task);
                    if (task.isAlerted) emergeTaskList.requireAlert = true;
                }else if (!(task instanceof RepeatTask) && Duration.between(getCurrentTime(), expiredDate).toDays() < Config.ALERT_LEVEL2_THRESHOLD)
                    emergeTaskList.emergeTasks.add(task);
            }
            return emergeTaskList;
        }
    }

    public static class Active {
        public static int preCursorX = 0;
        public static int preCursorY = 0;
        public static LocalDateTime activeStartTime;
        public static LocalDateTime leaveStartTime;
        public enum ActiveStatus{
            ACTIVE,
            LEAVING,
            LEFT
        }
        public static ActiveStatus activeStatus;

        static {
            activeStartTime = getCurrentTime();
            leaveStartTime = getCurrentTime();
            activeStatus = ActiveStatus.LEFT;
        }

        public static void updateCursor(MainGUI app, int curX, int curY) {
            int diffX = preCursorX - curX;
            int diffY = preCursorY - curY;
            if (diffX < -Config.CURSOR_DIFF || diffY < -Config.CURSOR_DIFF || diffX > Config.CURSOR_DIFF || diffY > Config.CURSOR_DIFF){
                // active
                leaveStartTime = getCurrentTime();
                switch (activeStatus){
                    case LEAVING -> {
                        activeStatus = ActiveStatus.ACTIVE;
                        app.updateActiveMsg();
                    }
                    case LEFT -> {
                        activeStatus = ActiveStatus.ACTIVE;
                        activeStartTime = getCurrentTime();
                        app.updateActiveMsg();
                    }
                }
            }else{
                // leave
                switch (activeStatus){
                    case ACTIVE -> {
                        if (Duration.between(leaveStartTime, getCurrentTime()).toMinutes() > 0){
                            activeStatus = ActiveStatus.LEAVING;
                            app.updateActiveMsg();
                        }
                    }
                    case LEAVING -> {
                        if (Duration.between(leaveStartTime, getCurrentTime()).toMinutes() >= Config.LEAVE_THRESHOLD){
                            activeStatus = ActiveStatus.LEFT;
                            app.updateActiveMsg();
                        }
                    }
                }
            }
            preCursorX = curX;
            preCursorY = curY;
        }

        public static long getActiveMinutes(){
            if (activeStatus != ActiveStatus.ACTIVE) return 0L;
            return Duration.between(activeStartTime, getCurrentTime()).toMinutes();
        }
    }

    public static class History{
        private static ArrayList<ActionRecord> history = new ArrayList<>();
        private static int cursor = 0;

        private static void addActionRecord(ActionRecord record){
            if (cursor < history.size()){
                // clear history after cursor
                history.subList(cursor, history.size()).clear();
            }
            history.add(cursor, record);
            cursor++;
        }

        public static boolean canRedo(){
            return cursor < history.size();
        }

        public static boolean canUndo(){
            return cursor > 0;
        }

        public static boolean redo(){
            if (cursor >= history.size()) return false;
            history.get(cursor).redo();
            cursor++;
            Tasks.writeTasksToFile(Tasks.taskFileName);
            return true;
        }

        public static boolean undo(){
            if (cursor <= 0) return false;
            cursor--;
            history.get(cursor).undo();
            Tasks.writeTasksToFile(Tasks.taskFileName);
            return true;
        }
    }

    public static class Alert{
        public static boolean isAlertActive = false;
        public static boolean pauseAlert = false;
        public static LocalDateTime pauseStart = LocalDateTime.MIN;
        public static LocalDateTime alertStart = LocalDateTime.MIN;
        public static LocalDateTime lastAlert = LocalDateTime.MIN;
        public static javafx.scene.control.Alert curAlert = null;

        public static javafx.scene.control.Alert getIconedAlert(javafx.scene.control.Alert.AlertType alertType, String content){
            javafx.scene.control.Alert newAlert = new javafx.scene.control.Alert(alertType, content);
            ((Stage) newAlert.getDialogPane().getScene().getWindow()).getIcons().add(MainGUI.appIcon);
            return newAlert;
        }

        public static long getPauseMinutesLeft(){
            return Config.PAUSE_LENGTH - Duration.between(pauseStart, getCurrentTime()).toMinutes();
        }

        public static void setAlertAlwaysOnTop(javafx.scene.control.Alert alert){
            ((Stage) alert.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
        }

        public static void setAlertConcurrent(javafx.scene.control.Alert alert){
            ((Stage) alert.getDialogPane().getScene().getWindow()).initModality(Modality.WINDOW_MODAL);
        }

        public static void closeAlert(){
            if (curAlert != null)
                curAlert.close();
            curAlert = null;
        }
    }
}
