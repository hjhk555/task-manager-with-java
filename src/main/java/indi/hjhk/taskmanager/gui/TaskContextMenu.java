package indi.hjhk.taskmanager.gui;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

import java.io.IOException;

public class TaskContextMenu extends ContextMenu {
    private static TaskContextMenu INSTANCE = null;
    private static MainGUI mainGUI;

    public MenuItem menuItemFinish;
    public MenuItem menuItemUpdate;
    public MenuItem menuItemDelete;

    private TaskContextMenu(){
        menuItemFinish = new MenuItem("完成任务");
        menuItemUpdate = new MenuItem("修改任务");
        menuItemDelete = new MenuItem("删除任务");

        ObservableList<MenuItem> itemList = getItems();
        itemList.add(menuItemFinish);
        itemList.add(menuItemUpdate);
        itemList.add(menuItemDelete);

        menuItemFinish.setOnAction(actionEvent -> mainGUI.finishSelectedTask());
        menuItemDelete.setOnAction(actionEvent -> mainGUI.deleteSelectedTask());
        menuItemUpdate.setOnAction(actionEvent -> mainGUI.updateSelectedTask());
    }

    public static TaskContextMenu getInstance(){
        if (INSTANCE == null)
            INSTANCE = new TaskContextMenu();
        return INSTANCE;
    }

    public static void setMainGUI(MainGUI mainGUI){
        TaskContextMenu.mainGUI = mainGUI;
    }
}
