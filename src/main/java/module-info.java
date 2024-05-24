module indi.hjhk.taskmanager {
    requires javafx.controls;
    requires javafx.fxml;

    exports indi.hjhk.global;
    exports indi.hjhk.taskmanager;
    exports indi.hjhk.taskmanager.gui;
    exports indi.hjhk.taskmanager.task;
    opens indi.hjhk.taskmanager.gui to javafx.fxml;
    exports indi.hjhk.taskmanager.gui.control;
    opens indi.hjhk.taskmanager.gui.control to javafx.fxml;
}