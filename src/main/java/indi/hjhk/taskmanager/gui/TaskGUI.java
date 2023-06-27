package indi.hjhk.taskmanager.gui;

import indi.hjhk.taskmanager.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class TaskGUI{
    public enum WindowType{
        CREATE,
        EDIT,
        VIEW
    }
    private final WindowType windowType;
    private Task assignedTask;
    private TaskControl controller;

    public TaskGUI(Task assignedTask, WindowType windowType) {
        this.assignedTask = assignedTask;
        this.windowType = windowType;
    }

    public void start(Window parentWindow) throws IOException {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentWindow);

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("task_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        controller = fxmlLoader.getController();
        controller.init(this);

        stage.setScene(scene);
        stage.showAndWait();
    }
}
