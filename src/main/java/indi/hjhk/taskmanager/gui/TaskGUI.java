package indi.hjhk.taskmanager.gui;

import indi.hjhk.taskmanager.Data;
import indi.hjhk.taskmanager.NormalTask;
import indi.hjhk.taskmanager.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

public class TaskGUI{
    public enum WindowType{
        CREATE,
        UPDATE,
        VIEW
    }
    private final WindowType windowType;
    private final int taskId;
    private TaskControl controller;
    public MainGUI mainGUI;
    public Scene taskScene;

    public TaskGUI(int taskId, WindowType windowType) {
        this.taskId = taskId;
        this.windowType = windowType;
    }

    public void start(MainGUI mainGUI){
        this.mainGUI = mainGUI;

        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainGUI.mainScene.getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("task_view.fxml"));
        try {
            taskScene = new Scene(fxmlLoader.load());
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        controller = fxmlLoader.getController();
        controller.init(this);

        switch (windowType){
            case CREATE -> {
                stage.setTitle("新建任务");
                controller.selTaskType.getSelectionModel().select(NormalTask.TASK_TYPE_SEQ);
                controller.chkTaskAlerted.setSelected(true);
            }
            case VIEW -> {
                stage.setTitle("查看任务");
                controller.setReadOnly(true);
                controller.showTaskOnPane(Data.Tasks.taskList.get(taskId));
            }
            case UPDATE -> {
                stage.setTitle("修改任务");
                controller.showTaskOnPane(Data.Tasks.taskList.get(taskId));
            }
        }

        stage.setScene(taskScene);
        stage.showAndWait();
    }

    public void handleConfirmedTask(Task newTask){
        switch (windowType){
            case CREATE -> {
                Data.Tasks.addTask(newTask);
                mainGUI.updateAll(LocalDateTime.now());
            }
            case UPDATE -> {
                Data.Tasks.updateTask(taskId, newTask);
                mainGUI.updateAll(LocalDateTime.now());
            }
        }
    }
}
