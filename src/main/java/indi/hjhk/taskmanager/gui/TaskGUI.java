package indi.hjhk.taskmanager.gui;

import indi.hjhk.taskmanager.Data;
import indi.hjhk.taskmanager.gui.control.TaskControl;
import indi.hjhk.taskmanager.task.IdentifiedTask;
import indi.hjhk.taskmanager.task.NormalTask;
import indi.hjhk.taskmanager.task.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TaskGUI{
    public enum WindowType{
        CREATE,
        UPDATE,
        VIEW
    }
    private final WindowType windowType;
    private final IdentifiedTask idTask;
    private TaskControl controller;
    public taskListGUI taskListGUI;
    public Scene taskScene;

    public TaskGUI(IdentifiedTask idTask, WindowType windowType) {
        this.idTask = idTask;
        this.windowType = windowType;
    }

    public void start(taskListGUI taskListGUI){
        this.taskListGUI = taskListGUI;

        Stage stage = new Stage();
        stage.getIcons().add(MainGUI.appIcon);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(taskListGUI.getScene().getWindow());

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
                controller.showTaskOnPane(idTask.task);
            }
            case UPDATE -> {
                stage.setTitle("修改任务");
                controller.showTaskOnPane(idTask.task);
            }
        }

        stage.setScene(taskScene);
        stage.show();

        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
    }

    public void handleConfirmedTask(Task newTask){
        switch (windowType){
            case CREATE -> {
                Data.Tasks.addTask(newTask);
                taskListGUI.updateAll();
            }
            case UPDATE -> {
                Data.Tasks.updateTask(idTask.id, newTask);
                taskListGUI.updateAll();
            }
        }
    }
}
