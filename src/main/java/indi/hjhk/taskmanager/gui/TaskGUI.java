package indi.hjhk.taskmanager.gui;

import indi.hjhk.taskmanager.Data;
import indi.hjhk.taskmanager.NormalTask;
import indi.hjhk.taskmanager.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    public void start(MainGUI mainGUI) throws IOException {
        this.mainGUI = mainGUI;

        Stage stage = new Stage();
        stage.initOwner(mainGUI.mainScene.getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("task_view.fxml"));
        taskScene = new Scene(fxmlLoader.load());

        controller = fxmlLoader.getController();
        controller.init(this);

        switch (windowType){
            case CREATE -> {
                stage.setTitle("新建任务");
                controller.selTaskType.getSelectionModel().select(NormalTask.TASK_TYPE_SEQ);
                controller.chkTaskAlerted.setSelected(true);
            }
            case VIEW -> {

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
