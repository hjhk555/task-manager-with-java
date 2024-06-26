package indi.hjhk.taskmanager.gui.control;

import indi.hjhk.taskmanager.*;
import indi.hjhk.taskmanager.gui.TaskGUI;
import indi.hjhk.taskmanager.task.*;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
    public ChoiceBox<String> selNormalHour;
    public ChoiceBox<String> selNormalMinute;
    public AnchorPane paneDailyTask;
    public ChoiceBox<String> selDailyHour;
    public ChoiceBox<String> selDailyMinute;
    public AnchorPane paneWeeklyTask;
    public ChoiceBox<String> selWeeklyHour;
    public ChoiceBox<String> selWeeklyMinute;
    public ChoiceBox<String> selWeeklyDayOfWeek;
    public AnchorPane paneMonthlyTask;
    public ChoiceBox<String> selMonthlyHour;
    public ChoiceBox<String> selMonthlyMinute;
    public ChoiceBox<String> selMonthlyDayOfMonth;
    private TaskGUI taskGUI;

    public void init(TaskGUI taskGUI){
        this.taskGUI = taskGUI;

        selTaskType.setItems(Task.getTaskTypeList());
        paneTypedTask.getChildren().forEach(node -> node.setVisible(true));
        paneTypedTask.getChildren().clear();

        selNormalHour.setItems(Data.Constants.hourList);
        selDailyHour.setItems(Data.Constants.hourList);
        selWeeklyHour.setItems(Data.Constants.hourList);
        selMonthlyHour.setItems(Data.Constants.hourList);

        selNormalMinute.setItems(Data.Constants.minuteList);
        selDailyMinute.setItems(Data.Constants.minuteList);
        selWeeklyMinute.setItems(Data.Constants.minuteList);
        selMonthlyMinute.setItems(Data.Constants.minuteList);

        selWeeklyDayOfWeek.setItems(Data.Constants.dayOfWeekName);

        selMonthlyDayOfMonth.setItems(Data.Constants.dayOfMonthList);

        setNormalTaskPaneContent(null);
        setUnlimitedTaskPaneContent(null);
        setDailyTaskPaneContent(null);
        setWeeklyTaskPaneContent(null);
        setMonthlyTaskPaneContent(null);
    }

    public void setReadOnly(boolean readOnly){
        txtTitle.setEditable(!readOnly);
        txtContent.setEditable(!readOnly);
        selTaskType.setDisable(readOnly);
        paneTypedTask.setDisable(readOnly);
        chkTaskDone.setDisable(readOnly);
        chkTaskAlerted.setDisable(readOnly);
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
        else if (task instanceof DailyTask dailyTask)
            setDailyTaskPaneContent(dailyTask);
        else if (task instanceof WeeklyTask weeklyTask)
            setWeeklyTaskPaneContent(weeklyTask);
        else if (task instanceof MonthlyTask monthlyTask)
            setMonthlyTaskPaneContent(monthlyTask);
    }

    public void switchTaskPane(int taskTypeSeq){
        paneTypedTask.getChildren().clear();

        Pane shownPane = switch (taskTypeSeq){
            case NormalTask.TASK_TYPE_SEQ -> switchNormalTaskPane();
            case UnlimitedTask.TASK_TYPE_SEQ -> switchUnlimitedTaskPane();
            case DailyTask.TASK_TYPE_SEQ -> switchDailyTaskPane();
            case WeeklyTask.TASK_TYPE_SEQ -> switchWeeklyTaskPane();
            case MonthlyTask.TASK_TYPE_SEQ -> switchMonthlyTaskPane();
            default -> null;
        };

        if (shownPane != null) paneTypedTask.getChildren().add(shownPane);
    }

    public Pane switchNormalTaskPane(){
        chkTaskDone.setVisible(true);
        return paneNormalTask;
    }

    public void setNormalTaskPaneContent(NormalTask task){
        if (task == null) {
            dateNormal.setValue(Data.getCurrentTime().toLocalDate());
            selNormalHour.getSelectionModel().select(0);
            selNormalMinute.getSelectionModel().select(0);
        }else{
            dateNormal.setValue(task.expireDate.toLocalDate());
            selNormalHour.getSelectionModel().select(task.expireDate.getHour());
            selNormalMinute.getSelectionModel().select(task.expireDate.getMinute());
        }
    }

    public Pane switchUnlimitedTaskPane(){
        chkTaskDone.setVisible(true);
        return paneUnlimitedTask;
    }

    public void setUnlimitedTaskPaneContent(UnlimitedTask task){
    }

    public Pane switchDailyTaskPane(){
        chkTaskDone.setVisible(false);
        return paneDailyTask;
    }

    public void setDailyTaskPaneContent(DailyTask task){
        if (task == null){
            selDailyHour.getSelectionModel().select(0);
            selDailyMinute.getSelectionModel().select(0);
        }else {
            selDailyHour.getSelectionModel().select(task.repeatTime.getHour());
            selDailyMinute.getSelectionModel().select(task.repeatTime.getMinute());
        }
    }

    public Pane switchWeeklyTaskPane(){
        chkTaskDone.setVisible(false);
        return paneWeeklyTask;
    }

    public void setWeeklyTaskPaneContent(WeeklyTask task){
        if (task == null){
            selWeeklyDayOfWeek.getSelectionModel().select(0);
            selWeeklyHour.getSelectionModel().select(0);
            selWeeklyMinute.getSelectionModel().select(0);
        }else{
            selWeeklyDayOfWeek.getSelectionModel().select(task.getDayOfWeek()-1);
            selWeeklyHour.getSelectionModel().select(task.repeatTime.getHour());
            selWeeklyMinute.getSelectionModel().select(task.repeatTime.getMinute());
        }
    }
    
    public Pane switchMonthlyTaskPane(){
        chkTaskDone.setVisible(false);
        return paneMonthlyTask;
    }

    public void setMonthlyTaskPaneContent(MonthlyTask task){
        if (task == null){
            selMonthlyDayOfMonth.getSelectionModel().select(0);
            selMonthlyHour.getSelectionModel().select(0);
            selMonthlyMinute.getSelectionModel().select(0);
        }else{
            selMonthlyDayOfMonth.getSelectionModel().select(task.getDayOfMonth()-1);
            selMonthlyHour.getSelectionModel().select(task.repeatTime.getHour());
            selMonthlyMinute.getSelectionModel().select(task.repeatTime.getMinute());
        }
    }

    public void btnConfirm_clicked(ActionEvent actionEvent) {
        if (txtTitle.getText().length() == 0){
            Data.Alert.getIconedAlert(Alert.AlertType.ERROR, "任务名称不得为空").showAndWait();
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
            case DailyTask.TASK_TYPE_SEQ -> {
                DailyTask confirmedDailyTask = new DailyTask();
                confirmedDailyTask.repeatTime = LocalTime.of(selDailyHour.getSelectionModel().getSelectedIndex(),
                        selDailyMinute.getSelectionModel().getSelectedIndex());
                confirmedTask = confirmedDailyTask;
            }
            case WeeklyTask.TASK_TYPE_SEQ -> {
                WeeklyTask confirmedWeeklyTask = new WeeklyTask();
                confirmedWeeklyTask.setDayOfWeek(selWeeklyDayOfWeek.getSelectionModel().getSelectedIndex()+1);
                confirmedWeeklyTask.repeatTime = LocalTime.of(selWeeklyHour.getSelectionModel().getSelectedIndex(),
                        selWeeklyMinute.getSelectionModel().getSelectedIndex());
                confirmedTask = confirmedWeeklyTask;
            }
            case MonthlyTask.TASK_TYPE_SEQ -> {
                MonthlyTask confirmedMonthlyTask = new MonthlyTask();
                confirmedMonthlyTask.setDayOfMonth(selMonthlyDayOfMonth.getSelectionModel().getSelectedIndex()+1);
                confirmedMonthlyTask.repeatTime = LocalTime.of(selMonthlyHour.getSelectionModel().getSelectedIndex()
                        , selMonthlyMinute.getSelectionModel().getSelectedIndex());
                confirmedTask = confirmedMonthlyTask;
            }
            default -> {
                Data.Alert.getIconedAlert(Alert.AlertType.ERROR, "未知的任务类型").showAndWait();
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
