package indi.hjhk.taskmanager.gui;

import indi.hjhk.taskmanager.Data;
import indi.hjhk.taskmanager.IdentifiedString;
import indi.hjhk.taskmanager.IdentifiedTask;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class TlistControl {
    public ListView<IdentifiedString> lstTasks;
    public Button btnConfirm;
    public Button btnCancel;
    public Label lblFileName;
    private TlistGUI tlistGUI;

    public int getSelectedTaskId(){
        IdentifiedString selected = lstTasks.getSelectionModel().getSelectedItem();
        if (selected == null) return -1;
        return selected.id;
    }

    public void btnConfirm_clicked(ActionEvent actionEvent) {
        tlistGUI.addSelectedTasks(lstTasks.getSelectionModel().getSelectedItems());
        tlistGUI.tlistScene.getWindow().hide();
    }

    public void btnCancel_clicked(ActionEvent actionEvent) {
        tlistGUI.tlistScene.getWindow().hide();
    }

    public void init(TlistGUI tlistGUI) {
        this.tlistGUI = tlistGUI;

        lstTasks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void lstTasks_mouseClicked(MouseEvent mouseEvent) {
        int selectedTaskId = getSelectedTaskId();
        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2)
            new TaskGUI(new IdentifiedTask(selectedTaskId, tlistGUI.readTasklist.get(selectedTaskId)), TaskGUI.WindowType.VIEW).start(tlistGUI);
    }
}
