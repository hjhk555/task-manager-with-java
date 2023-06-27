module indi.hjhk.taskmanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    exports indi.hjhk.taskmanager.gui;
    opens indi.hjhk.taskmanager.gui to javafx.fxml;
}