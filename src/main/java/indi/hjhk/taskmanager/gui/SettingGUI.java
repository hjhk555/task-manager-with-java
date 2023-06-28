package indi.hjhk.taskmanager.gui;

import indi.hjhk.taskmanager.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class SettingGUI {
    public Scene settingScene;
    public MainGUI mainGUI;
    private SettingControl controller;

    public void start(MainGUI mainGUI) throws IOException {
        this.mainGUI = mainGUI;
        Stage stage = new Stage();
        stage.initOwner(mainGUI.mainScene.getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("setting_view.fxml"));
        settingScene = new Scene(fxmlLoader.load());

        controller = fxmlLoader.getController();
        controller.init(this);

        stage.setTitle("设置");
        stage.setScene(settingScene);
        stage.showAndWait();
    }
}
