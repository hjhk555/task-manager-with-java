package indi.hjhk.taskmanager.gui;

import indi.hjhk.taskmanager.NormalTask;
import indi.hjhk.taskmanager.Task;
import indi.hjhk.taskmanager.UnlimitedTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TaskControl {
    public TextArea txtContent;
    public TextField txtTitle;
    public ChoiceBox<String> selTaskType;
    public CheckBox chkTaskDone;
    public CheckBox chkTaskAlerted;
    public StackPane paneTypedTask;
    public AnchorPane paneNormalTask;
    public AnchorPane paneUnlimitedTask;
    public DatePicker dateNormal;
    public ChoiceBox<Integer> selNormalHour;
    public ChoiceBox<Integer> selNormalMinute;
    private TaskGUI taskGUI;
    private Pane shownPane = null;

    public void init(TaskGUI taskGUI){
        this.taskGUI = taskGUI;

        selTaskType.setItems(Task.getTaskTypeList());
        paneTypedTask.getChildren().forEach(node -> node.setVisible(false));

        ObservableList<Integer> hourList = FXCollections.observableArrayList(IntStream.range(0, 24).boxed().collect(Collectors.toList()));
        selNormalHour.setItems(hourList);

        ObservableList<Integer> minuteList = FXCollections.observableArrayList(IntStream.range(0, 60).boxed().collect(Collectors.toList()));
        selNormalMinute.setItems(minuteList);

        setNormalTaskPaneContent(null);
        setUnlimitedTaskPaneContent(null);
    }

    public void showTaskOnPane(Task task){
        if (task == null) return;

        txtTitle.setText(task.title);
        txtContent.setText(task.content);
        chkTaskAlerted.setSelected(task.isAlerted);
        chkTaskDone.setSelected(task.isDone());
        selTaskType.getSelectionModel().select(task.getTypeSeq());
        if (task instanceof NormalTask normalTask)
            setNormalTaskPaneContent(normalTask);
        else if (task instanceof UnlimitedTask unlimitedTask)
            setUnlimitedTaskPaneContent(unlimitedTask);
    }

    public void switchTaskPane(int taskTypeSeq){
        if (shownPane != null) shownPane.setVisible(false);

        shownPane = switch (taskTypeSeq){
            case NormalTask.TASK_TYPE_SEQ -> switchNormalTaskPane();
            case UnlimitedTask.TASK_TYPE_SEQ -> switchUnlimitedTaskPane();
            default -> null;
        };

        if (shownPane != null) shownPane.setVisible(true);
    }

    public Pane switchNormalTaskPane(){
        chkTaskDone.setVisible(true);
        return paneNormalTask;
    }

    public void setNormalTaskPaneContent(NormalTask task){
        if (task == null) {
            dateNormal.setValue(LocalDate.now());
            selNormalHour.getSelectionModel().select(0);
            selNormalMinute.getSelectionModel().select(0);
        }else{
            dateNormal.setValue(task.expireDate.toLocalDate());
            selNormalHour.getSelectionModel().select(task.expireDate.getHour());
            selNormalMinute.getSelectionModel().select(task.expireDate.getMinute());
        }
    }

    public Pane switchUnlimitedTaskPane(){
        chkTaskDone.setVisible(false);
        return paneUnlimitedTask;
    }

    public void setUnlimitedTaskPaneContent(UnlimitedTask task){
    }

    public void btnConfirm_clicked(ActionEvent actionEvent) {
        if (txtTitle.getText().length() == 0){
            new Alert(Alert.AlertType.ERROR, "任务名称不得为空").showAndWait();
            return;
        }
        Task confirmedTask;
        switch (selTaskType.getSelectionModel().getSelectedIndex()){
            case NormalTask.TASK_TYPE_SEQ -> {
                NormalTask confirmedNormalTask = new NormalTask();
                confirmedNormalTask.done = chkTaskDone.isSelected();
                confirmedNormalTask.expireDate = LocalDateTime.of(dateNormal.getValue(),
                        LocalTime.of(selNormalHour.getSelectionModel().getSelectedIndex(),
                                selNormalMinute.getSelectionModel().getSelectedIndex()));
                confirmedTask = confirmedNormalTask;
            }
            case UnlimitedTask.TASK_TYPE_SEQ -> {
                UnlimitedTask confirmedUnlimitedTask = new UnlimitedTask();
                confirmedUnlimitedTask.done = chkTaskDone.isSelected();
                confirmedTask = confirmedUnlimitedTask;
            }
            default -> {
                new Alert(Alert.AlertType.ERROR, "未知的任务类型").showAndWait();
                return;
            }
        }
        confirmedTask.title = txtTitle.getText();
        confirmedTask.content = txtContent.getText();
        confirmedTask.isAlerted = chkTaskAlerted.isSelected();
        taskGUI.handleConfirmedTask(confirmedTask);

        taskGUI.taskScene.getWindow().hide();
    }

    public void btnCancel_clicked(ActionEvent actionEvent) {
        taskGUI.taskScene.getWindow().hide();
    }

    public void selTaskType_onChange(ActionEvent actionEvent) {
        switchTaskPane(selTaskType.getSelectionModel().getSelectedIndex());
    }
}
