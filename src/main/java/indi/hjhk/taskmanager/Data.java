package indi.hjhk.taskmanager;

import indi.hjhk.global.GlobalConfig;
import indi.hjhk.taskmanager.gui.MainGUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Data {
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
            config.write();
        }
    }

    public static class Tasks {
        public static ArrayList<Task> taskList = new ArrayList<>();
        public static String taskFileName = "tasks.list";

        static {
            taskList.addAll(readTasksFromFile(taskFileName));
        }

        public static ArrayList<Task> readTasksFromFile(String fileName){
            try {
                ArrayList<Task> tasks = new ArrayList<>();
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName));
                Object object = objectInputStream.readObject();
                if (object instanceof List<?> list){
                    for (Object obj : list){
                        if (obj instanceof Task task){
                            tasks.add(task);
                        }
                    }
                }
                objectInputStream.close();
                return tasks;
            } catch (IOException | ClassNotFoundException e) {
                return new ArrayList<>();
            }
        }

        public static void writeTasksToFile(String fileName){
            try{
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
                objectOutputStream.writeObject(taskList);
                objectOutputStream.close();
            } catch (IOException ignored) {
            }
        }

        public static ObservableList<IdentifiedString> getSortedTaskInfo(LocalDateTime curTime){
            ArrayList<IdentifiedTask> sortedList = new ArrayList<>();
            for (int i=0; i<taskList.size(); i++){
                sortedList.add(new IdentifiedTask(i, taskList.get(i)));
            }
            sortedList.sort(IdentifiedTask.comparator);
            ObservableList<IdentifiedString> taskInfo = FXCollections.observableArrayList();
            for (IdentifiedTask identifiedTask: sortedList)
                taskInfo.add(new IdentifiedString(identifiedTask.id, identifiedTask.task.toString(curTime)));
            return taskInfo;
        }

        public static class EmergeTaskList{
            public ArrayList<Task> emergeTasks = new ArrayList<>();
            public ArrayList<Task> exceedTasks = new ArrayList<>();
            public boolean requireAlert = false;
        }

        public static EmergeTaskList getEmergeTaskList(LocalDateTime curTime){
            EmergeTaskList emergeTaskList = new EmergeTaskList();
            for (Task task : taskList){
                LocalDateTime expiredDate = task.getExpireDate();
                if (expiredDate.isBefore(curTime)){
                    emergeTaskList.exceedTasks.add(task);
                    if (task.isAlerted) emergeTaskList.requireAlert = true;
                }else if (Duration.between(curTime, expiredDate).toDays() < Config.ALERT_LEVEL2_THRESHOLD)
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
            activeStartTime = LocalDateTime.now();
            leaveStartTime = LocalDateTime.now();
            activeStatus = ActiveStatus.LEFT;
        }

        public static void updateCursor(MainGUI app, LocalDateTime dateTime, int curX, int curY) {
            int diffX = preCursorX - curX;
            int diffY = preCursorY - curY;
            if (diffX < -Config.CURSOR_DIFF || diffY < -Config.CURSOR_DIFF || diffX > Config.CURSOR_DIFF || diffY > Config.CURSOR_DIFF){
                // active
                leaveStartTime = dateTime;
                switch (activeStatus){
                    case LEAVING -> {
                        activeStatus = ActiveStatus.ACTIVE;
                        app.updateActiveMsg(dateTime);
                    }
                    case LEFT -> {
                        activeStatus = ActiveStatus.ACTIVE;
                        activeStartTime = dateTime;
                        app.updateActiveMsg(dateTime);
                    }
                }
            }else{
                // leave
                switch (activeStatus){
                    case ACTIVE -> {
                        if (Duration.between(leaveStartTime, dateTime).toMinutes() > 0){
                            activeStatus = ActiveStatus.LEAVING;
                            app.updateActiveMsg(dateTime);
                        }
                    }
                    case LEAVING -> {
                        if (Duration.between(leaveStartTime, dateTime).toMinutes() >= Config.LEAVE_THRESHOLD){
                            activeStatus = ActiveStatus.LEFT;
                            app.updateActiveMsg(dateTime);
                        }
                    }
                }
            }
            preCursorX = curX;
            preCursorY = curY;
        }

        public static long getActiveMinutes(LocalDateTime curTime){
            if (activeStatus != ActiveStatus.ACTIVE) return 0L;
            return Duration.between(activeStartTime, curTime).toMinutes();
        }
    }

    public static class History{

    }

    public static class Alert{
        public static boolean preAlert = false;
        public static boolean pauseAlert = false;
        public static LocalDateTime pauseStart;
        public static LocalDateTime lastAlert;
        public static javafx.scene.control.Alert curAlert = null;

        public static long getPauseMinutesLeft(LocalDateTime curTime){
            return Config.PAUSE_LENGTH - Duration.between(pauseStart, curTime).toMinutes();
        }

        public static void closeAlert(){
            if (curAlert != null)
                curAlert.close();
            curAlert = null;
        }

        public static void resetAlert(){
            closeAlert();
            preAlert = false;
        }
    }
}
