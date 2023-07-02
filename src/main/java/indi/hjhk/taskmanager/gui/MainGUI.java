package indi.hjhk.taskmanager.gui;

import indi.hjhk.global.GlobalLock;
import indi.hjhk.taskmanager.Data;
import indi.hjhk.taskmanager.Task;
import indi.hjhk.taskmanager.TimedTrigger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class MainGUI extends Application {
    private static final GlobalLock APP_LOCK = new GlobalLock("app");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static final Image appIcon = new Image(MainGUI.class.getResourceAsStream("icon.png"));
    public Scene mainScene;
    private final Robot robot = new Robot();
    private MainControl controller;
    private ArrayList<Thread> threads = new ArrayList<>();

    private void startThread(Runnable runnable){
        Thread newThread = new Thread(runnable);
        newThread.start();
        threads.add(newThread);
    }

    private void interruptThreads(){
        for (Thread thread : threads){
            thread.interrupt();
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        TaskContextMenu.setMainGUI(this);
        Platform.runLater(() -> {
            Data.Active.preCursorX = (int) robot.getMouseX();
            Data.Active.preCursorY = (int) robot.getMouseY();
        });
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.getIcons().add(appIcon);

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("main_view.fxml"));
        mainScene = new Scene(fxmlLoader.load());
        controller = fxmlLoader.getController();
        controller.init(this);

        updateAll(LocalDateTime.now());

        TimedTrigger alignedMinuteTrigger = new TimedTrigger(-1, 60000, true);
        alignedMinuteTrigger.registerCall(dateTime -> Platform.runLater(() -> updateAll(dateTime)));
        startThread(alignedMinuteTrigger);

        TimedTrigger secondTrigger = new TimedTrigger(-1, 1000, false);
        secondTrigger.registerCall(dateTime -> Platform.runLater(() -> Data.Active.updateCursor(this, dateTime, (int) robot.getMouseX(), (int) robot.getMouseY())));
        startThread(secondTrigger);

        stage.setTitle("任务管理器");
        stage.setScene(mainScene);
        stage.show();
    }

    public void onConfigChange(){
        LocalDateTime curTime = LocalDateTime.now();
        Data.Alert.closeAlert();
        updateAll(curTime);
    }

    public void updateAll(LocalDateTime curTime){
        controller.lblCurTime.setText(DATE_FORMAT.format(curTime));
        controller.lstTasks.setItems(Data.Tasks.getSortedTaskInfo(curTime));
        Data.Tasks.EmergeTaskList emergeTaskList = Data.Tasks.getEmergeTaskList(curTime);
        checkAndPerformAlert(curTime, emergeTaskList);
        updateActiveMsg(curTime);
        updateTaskMsg(curTime, emergeTaskList);
        updateUI();
    }

    private void updateUI(){
        controller.menuUndo.setDisable(!Data.History.canUndo());
        controller.menuRedo.setDisable(!Data.History.canRedo());
    }

    public void checkAndPerformAlert(LocalDateTime curTIme, Data.Tasks.EmergeTaskList emergeTaskList){
        if (Data.Alert.pauseAlert){
            if (Data.Alert.getPauseMinutesLeft(curTIme) <= 0){
                controller.switchAlert();
            }else return;
        }
        // check alert
        long activeMinutes = Data.Active.getActiveMinutes(curTIme);
        boolean activeExceed = Data.Active.activeStatus == Data.Active.ActiveStatus.ACTIVE &&
                                activeMinutes >= Data.Config.ACTIVE_THRESHOLD;
        boolean requireAlert = activeExceed || emergeTaskList.requireAlert;
        if (requireAlert){
            if (!Data.Alert.isAlertActive || Duration.between(Data.Alert.lastAlert, curTIme).toMinutes() >= Data.Config.ALERT_INTERVAL){
                // trigger alert
                if (!Data.Alert.isAlertActive){
                    Data.Alert.alertStart = curTIme;
                    Data.Alert.isAlertActive = true;
                }
                StringBuilder stringBuilder = new StringBuilder();
                if (emergeTaskList.requireAlert) {
                    stringBuilder.append("以下任务已逾期:\n");
                    for (Task task : emergeTaskList.exceedTasks) {
                        if (task.isAlerted)
                            stringBuilder.append("    ").append(task.title).append("\n");
                    }
                    stringBuilder.append("\n");
                }

                if (activeExceed){
                    stringBuilder.append("长时间使用计算机：\n");
                    stringBuilder.append(String.format("\t已使用计算机%d时%d分，建议离座休息\n", activeMinutes/60, activeMinutes%60));
                    stringBuilder.append(String.format("\t需要休息%d分钟以解除警报", Data.Config.LEAVE_THRESHOLD));
                }
                Data.Alert.closeAlert();

                Alert newAlert = Data.Alert.getIconedAlert(Alert.AlertType.WARNING, stringBuilder.toString());
                newAlert.setTitle("警报");
                newAlert.setHeaderText(String.format("来自任务管理器的警报\n\t%s ~ %s",
                        TIME_FORMAT.format(Data.Alert.alertStart),
                        TIME_FORMAT.format(curTIme)));
                Data.Alert.setAlertAlwaysOnTop(newAlert);
                Data.Alert.setAlertConcurrent(newAlert);
                newAlert.show();

                Data.Alert.curAlert = newAlert;
                Data.Alert.lastAlert = curTIme;
            }
        }else{
            Data.Alert.closeAlert();
            // remove alert flag
            if (Data.Active.activeStatus != Data.Active.ActiveStatus.LEAVING)
                Data.Alert.isAlertActive = false;
        }
    }

    public void updateActiveMsg(LocalDateTime curTime){
        switch (Data.Active.activeStatus){
            case LEFT -> {
                controller.lblActiveMsg.setTextFill(Color.GREEN);
                controller.lblActiveMsg.setText("当前未使用计算机");
            }
            case LEAVING -> {
                controller.lblActiveMsg.setTextFill(Color.OLIVE);
                controller.lblActiveMsg.setText(String.format("已离开计算机%d分钟", Duration.between(Data.Active.leaveStartTime, curTime).toMinutes()));
            }
            case ACTIVE -> {
                long activeMinutes = Data.Active.getActiveMinutes(curTime);
                if (activeMinutes < Data.Config.ACTIVE_THRESHOLD){
                    controller.lblActiveMsg.setTextFill(Color.DARKORANGE);
                }else{
                    controller.lblActiveMsg.setTextFill(Color.RED);
                }
                controller.lblActiveMsg.setText(String.format("已使用计算机%d小时%d分钟", activeMinutes/60, activeMinutes%60));
            }
        }
    }

    public void updateTaskMsg(LocalDateTime curTime, Data.Tasks.EmergeTaskList emergeTaskList){
        if (Data.Alert.pauseAlert){
            controller.lblTaskMsg.setText(String.format("警报已暂停，将在%d分钟后恢复", Data.Alert.getPauseMinutesLeft(curTime)));
            controller.lblTaskMsg.setTextFill(Color.DARKORANGE);
        }else{
            int exceeds = emergeTaskList.exceedTasks.size();
            int emerges = emergeTaskList.emergeTasks.size();
            if (exceeds == 0 && emerges == 0){
                controller.lblTaskMsg.setTextFill(Color.GREEN);
                controller.lblTaskMsg.setText("任务进度正常");
            }else{
                StringBuilder stringBuilder = new StringBuilder();
                if (exceeds > 0)
                    stringBuilder.append(String.format("%d个任务逾期 ", exceeds));
                if (emerges > 0)
                    stringBuilder.append(String.format("%d个任务紧急 ", emerges));
                controller.lblTaskMsg.setTextFill((emergeTaskList.requireAlert ? Color.RED : Color.OLIVE));
                controller.lblTaskMsg.setText(stringBuilder.toString());
            }
        }
    }

    public void finishSelectedTask(){
        int selectedTaskId = controller.getSelectedTaskId();
        if (selectedTaskId < 0) return;
        Task oldTask = Data.Tasks.taskList.get(selectedTaskId);
        if (oldTask.isDone()) return;
        Task newTask = oldTask.clone();
        newTask.finish();
        Data.Tasks.updateTask(selectedTaskId, newTask);
        updateAll(LocalDateTime.now());
    }

    public void deleteSelectedTask(){
        int selectedTaskId = controller.getSelectedTaskId();
        if (selectedTaskId < 0) return;
        Data.Tasks.removeTask(selectedTaskId);
        updateAll(LocalDateTime.now());
    }

    public void updateSelectedTask(){
        int selectedTaskId = controller.getSelectedTaskId();
        if (selectedTaskId < 0) return;
        new TaskGUI(selectedTaskId, TaskGUI.WindowType.UPDATE).start(this);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        interruptThreads();
        APP_LOCK.unlock();
    }

    public static void main(String[] args) {
        if (!APP_LOCK.lock()) {
            Platform.exit();
            return;
        }
        launch();
    }
}