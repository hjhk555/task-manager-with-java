package indi.hjhk.taskmanager.gui;

import indi.hjhk.taskmanager.Data;
import indi.hjhk.taskmanager.IdentifiedString;
import indi.hjhk.taskmanager.Task;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TlistGUI implements taskListGUI {
    public File readFile;
    public ArrayList<Task> readTasklist;
    public MainGUI mainGUI;
    private TlistControl controller;
    public Scene tlistScene;

    public TlistGUI(File readFile){
        this.readFile = readFile;
    }

    public void start(MainGUI mainGUI){
        if (readFile == null) return;
        this.mainGUI = mainGUI;

        Stage stage = new Stage();
        stage.getIcons().add(MainGUI.appIcon);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainGUI.mainScene.getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("tlist_view.fxml"));
        try {
            tlistScene = new Scene(fxmlLoader.load());
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        controller = fxmlLoader.getController();
        controller.init(this);

        readTasklist = Data.Tasks.readTasksFromFile(readFile.getPath());
        controller.lblFileName.setText(readFile.getName());
        controller.lstTasks.setItems(Data.Tasks.getSortedTaskInfo(readTasklist));

        stage.setTitle("导入任务");
        stage.setScene(tlistScene);
        stage.show();

        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
    }

    public void addSelectedTasks(ObservableList<IdentifiedString> selectedItems){
        if (selectedItems.size() == 0) return;
        for (IdentifiedString idString : selectedItems){
            Data.Tasks.addTask(readTasklist.get(idString.id));
        }
        mainGUI.updateAll();
    }

    @Override
    public void updateAll() {
    }

    @Override
    public Scene getScene() {
        return tlistScene;
    }
}
