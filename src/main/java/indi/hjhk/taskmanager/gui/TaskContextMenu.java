package indi.hjhk.taskmanager.gui;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class TaskContextMenu extends ContextMenu {
    private static TaskContextMenu INSTANCE = null;
    private static MainGUI mainGUI;

    public MenuItem menuItemFinish;
    public MenuItem menuItemEdit;
    public MenuItem menuItemDelete;

    private TaskContextMenu(){
        menuItemFinish = new MenuItem("完成任务");
        menuItemEdit = new MenuItem("修改任务");
        menuItemDelete = new MenuItem("删除任务");

        ObservableList<MenuItem> itemList = getItems();
        itemList.add(menuItemFinish);
        itemList.add(menuItemEdit);
        itemList.add(menuItemDelete);

        menuItemFinish.setOnAction(actionEvent -> mainGUI.finishSelectedTask());
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
