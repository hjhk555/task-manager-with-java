package indi.hjhk.taskmanager.gui.control;

import indi.hjhk.taskmanager.*;
import indi.hjhk.taskmanager.gui.*;
import indi.hjhk.taskmanager.task.RepeatTask;
import indi.hjhk.taskmanager.task.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class MainControl {
    public MenuItem menuUndo;
    public MenuItem menuRedo;
    public MenuItem menuReadTlist;
    public MenuItem menuWriteTlist;
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
                new TaskGUI(Data.Tasks.getIdentifiedTask(selectedTaskId), TaskGUI.WindowType.VIEW).start(mainGUI);
        }else if (mouseEvent.getButton() == MouseButton.SECONDARY){
            boolean showDoneMenu;
            Task selectedTask = Data.Tasks.taskList.get(selectedTaskId);
            if (selectedTask instanceof RepeatTask){
                showDoneMenu = !Data.getCurrentTime().isBefore(selectedTask.getExpireDate());
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
            Data.Alert.pauseStart = Data.getCurrentTime();
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
        new TaskGUI(null, TaskGUI.WindowType.CREATE).start(mainGUI);
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

    public void menuReadTlist_clicked(ActionEvent actionEvent) {
        FileChooser readFileChooser = new FileChooser();
        readFileChooser.setInitialDirectory(Data.Constants.userDesktop);
        readFileChooser.getExtensionFilters().addAll(Data.Constants.taskListFilter, Data.Constants.defaultFilter);
        File readFile = readFileChooser.showOpenDialog(mainGUI.mainScene.getWindow());
        if (readFile != null) new TlistGUI(readFile).start(mainGUI);
    }

    public void menuWriteTlist_clicked(ActionEvent actionEvent) {
        FileChooser writeFileChooser = new FileChooser();
        writeFileChooser.setInitialDirectory(Data.Constants.userDesktop);
        writeFileChooser.setInitialFileName(Data.Config.DEFAULT_EXPORT_FILENAME+Data.Tasks.taskFileSuffix);
        writeFileChooser.getExtensionFilters().addAll(Data.Constants.taskListFilter, Data.Constants.defaultFilter);
        File writeFile = writeFileChooser.showSaveDialog(mainGUI.mainScene.getWindow());
        if (writeFile != null) Data.Tasks.writeTasksToFile(writeFile.getPath());
    }
}
