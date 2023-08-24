package indi.hjhk.taskmanager.gui;

import indi.hjhk.taskmanager.Data;
import indi.hjhk.taskmanager.IdentifiedString;
import indi.hjhk.taskmanager.RepeatTask;
import indi.hjhk.taskmanager.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.time.LocalDateTime;

public class MainControl {
    public MenuItem menuUndo;
    public MenuItem menuRedo;
    private MainGUI mainGUI;
    public Button btnPause;
    public Label lblActiveMsg;
    public Label lblTaskMsg;
    public Label lblCurTime;
    public ListView<IdentifiedString> lstTasks;

    public void init(MainGUI mainGUI){
        this.mainGUI = mainGUI;
    }

    public int getSelectedTaskId(){
        IdentifiedString selected = lstTasks.getSelectionModel().getSelectedItem();
        if (selected == null) return -1;
        return selected.id;
    }

    public void lstTask_mouseClicked(MouseEvent mouseEvent) {
        TaskContextMenu taskContextMenu = TaskContextMenu.getInstance();
        taskContextMenu.hide();
        int selectedTaskId = getSelectedTaskId();
        if (selectedTaskId < 0) return;
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (mouseEvent.getClickCount() == 2)
                new TaskGUI(selectedTaskId, TaskGUI.WindowType.VIEW).start(mainGUI);
        }else if (mouseEvent.getButton() == MouseButton.SECONDARY){
            boolean showDoneMenu;
            Task selectedTask = Data.Tasks.taskList.get(selectedTaskId);
            if (selectedTask instanceof RepeatTask){
                showDoneMenu = !Data.curTime.isBefore(selectedTask.getExpireDate());
            }else{
                showDoneMenu = !selectedTask.isDone();
            }
            taskContextMenu.menuItemFinish.setDisable(!showDoneMenu);

            taskContextMenu.show(lstTasks, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    }

    public void btnPause_mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) switchAlert(true);
    }

    public void switchAlert(boolean update){
        if (!Data.Alert.pauseAlert){
            Data.Alert.closeAlert();
            Data.Alert.pauseAlert = true;
            Data.Alert.pauseStart = Data.curTime;
            btnPause.setText("恢复警报");
            System.out.println("alert paused");
        }else{
            Data.Alert.pauseAlert = false;
            btnPause.setText("暂停警报");
            System.out.println("alert resumed");
        }
        if (update) mainGUI.updateAll();
    }

    public void menuSetting_selected(ActionEvent actionEvent) {
        try {
            new SettingGUI().start(mainGUI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void menuNewTask_clicked(ActionEvent actionEvent) {
        new TaskGUI(-1, TaskGUI.WindowType.CREATE).start(mainGUI);
    }

    public void menuUndo_clicked(ActionEvent actionEvent) {
        if (Data.History.undo())
            mainGUI.updateAll();
    }

    public void menuRedo_clicked(ActionEvent actionEvent) {
        if (Data.History.redo())
            mainGUI.updateAll();
    }

    public void btnAbout_clicked(ActionEvent actionEvent) {
        Alert infoAlert = Data.Alert.getIconedAlert(Alert.AlertType.INFORMATION, Data.appInfo);
        infoAlert.setHeaderText("任务管理器");
        infoAlert.setTitle("关于");
        Data.Alert.setAlertConcurrent(infoAlert);
        infoAlert.show();
    }
}
