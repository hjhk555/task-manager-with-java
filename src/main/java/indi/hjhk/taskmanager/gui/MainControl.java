package indi.hjhk.taskmanager.gui;

import indi.hjhk.taskmanager.Data;
import indi.hjhk.taskmanager.IdentifiedString;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.time.LocalDateTime;

public class MainControl {
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
        TaskContextMenu.getInstance().hide();
        int selectedTaskId = getSelectedTaskId();
        if (selectedTaskId < 0) return;
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (mouseEvent.getClickCount() == 2){
                // double click
            }
        }else if (mouseEvent.getButton() == MouseButton.SECONDARY){
            TaskContextMenu taskContextMenu = TaskContextMenu.getInstance();
            taskContextMenu.menuItemFinish.setDisable(Data.Tasks.taskList.get(selectedTaskId).isDone());
            taskContextMenu.show(lstTasks, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    }

    public void btnPause_mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) switchAlert();
    }

    public void switchAlert(){
        if (!Data.Alert.pauseAlert){
            Data.Alert.resetAlert();
            Data.Alert.pauseAlert = true;
            Data.Alert.pauseStart = LocalDateTime.now();
            btnPause.setText("恢复警报");
        }else{
            Data.Alert.pauseAlert = false;
            btnPause.setText("暂停警报");
        }
        mainGUI.updateAll(LocalDateTime.now());
    }

    public void menuSetting_selected(ActionEvent actionEvent) {
        try {
            new SettingGUI().start(mainGUI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void menuNewTask_clicked(ActionEvent actionEvent) {
        try{
            new TaskGUI(-1, TaskGUI.WindowType.CREATE).start(mainGUI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
