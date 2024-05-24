package indi.hjhk.taskmanager.gui;

import indi.hjhk.taskmanager.gui.control.SettingControl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingGUI {
    public Scene settingScene;
    public MainGUI mainGUI;
    private SettingControl controller;

    public void start(MainGUI mainGUI) throws IOException {
        this.mainGUI = mainGUI;
        Stage stage = new Stage();
        stage.getIcons().add(MainGUI.appIcon);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainGUI.mainScene.getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("setting_view.fxml"));
        settingScene = new Scene(fxmlLoader.load());

        controller = fxmlLoader.getController();
        controller.init(this);

        stage.setTitle("设置");
        stage.setScene(settingScene);
        stage.show();

        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
    }
}
