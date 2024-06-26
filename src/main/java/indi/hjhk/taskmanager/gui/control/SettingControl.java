package indi.hjhk.taskmanager.gui.control;

import indi.hjhk.taskmanager.AlertMsgException;
import indi.hjhk.taskmanager.Data;
import indi.hjhk.taskmanager.MathUtils;
import indi.hjhk.taskmanager.gui.SettingGUI;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class SettingControl {
    public TextField txtAlertInterval;
    public TextField txtActiveThreshold;
    public TextField txtLeaveThreshold;
    public TextField txtPauseTime;
    public TextField txtCursorDiff;
    public TextField txtLevel1Threshold;
    public TextField txtLevel2Threshold;
    public TextField txtLevel3Threshold;
    public TextField txtDefaultExportFilename;
    private SettingGUI settingGUI;
    public void init(SettingGUI settingGUI){
        this.settingGUI = settingGUI;

        txtAlertInterval.setTextFormatter(new TextFormatter<>(MathUtils.integerFilter));
        txtActiveThreshold.setTextFormatter(new TextFormatter<>(MathUtils.integerFilter));
        txtLeaveThreshold.setTextFormatter(new TextFormatter<>(MathUtils.integerFilter));
        txtPauseTime.setTextFormatter(new TextFormatter<>(MathUtils.integerFilter));
        txtCursorDiff.setTextFormatter(new TextFormatter<>(MathUtils.integerFilter));
        txtLevel1Threshold.setTextFormatter(new TextFormatter<>(MathUtils.integerFilter));
        txtLevel2Threshold.setTextFormatter(new TextFormatter<>(MathUtils.integerFilter));
        txtLevel3Threshold.setTextFormatter(new TextFormatter<>(MathUtils.integerFilter));

        txtAlertInterval.setText(String.valueOf(Data.Config.ALERT_INTERVAL));
        txtActiveThreshold.setText(String.valueOf(Data.Config.ACTIVE_THRESHOLD));
        txtLeaveThreshold.setText(String.valueOf(Data.Config.LEAVE_THRESHOLD));
        txtPauseTime.setText(String.valueOf(Data.Config.PAUSE_LENGTH));
        txtCursorDiff.setText(String.valueOf(Data.Config.CURSOR_DIFF));
        txtLevel1Threshold.setText(String.valueOf(Data.Config.ALERT_LEVEL1_THRESHOLD));
        txtLevel2Threshold.setText(String.valueOf(Data.Config.ALERT_LEVEL2_THRESHOLD));
        txtLevel3Threshold.setText(String.valueOf(Data.Config.ALERT_LEVEL3_THRESHOLD));
        txtDefaultExportFilename.setText(Data.Config.DEFAULT_EXPORT_FILENAME);
    }

    public void btnCancel_clicked(ActionEvent actionEvent) {
        settingGUI.settingScene.getWindow().hide();
    }

    public void btnConfirm_clicked(ActionEvent actionEvent) {
        try{
            int alertInterval = MathUtils.parseAndCheckInt("提示间隔", txtAlertInterval.getText(), 1, 10000);
            int activeThreshold = MathUtils.parseAndCheckInt("连续使用提示阈值", txtActiveThreshold.getText(), 1, 10000);
            int leaveThreshold = MathUtils.parseAndCheckInt("离开计时阈值", txtLeaveThreshold.getText(), 1, 10000);
            int pauseTime = MathUtils.parseAndCheckInt("提示暂停时长", txtPauseTime.getText(), 1, 10000);
            int cursorDiff = MathUtils.parseAndCheckInt("鼠标活动误差", txtCursorDiff.getText(), 0, 10000);
            int level1Threshold = MathUtils.parseAndCheckInt("任务临近★", txtLevel1Threshold.getText(), 1, 10000);
            int level2Threshold = MathUtils.parseAndCheckInt("任务临近★★", txtLevel2Threshold.getText(), 1, 10000);
            int level3Threshold = MathUtils.parseAndCheckInt("任务临近★★★", txtLevel3Threshold.getText(), 1, 10000);

            Data.Config.ALERT_INTERVAL = alertInterval;
            Data.Config.ACTIVE_THRESHOLD = activeThreshold;
            Data.Config.LEAVE_THRESHOLD = leaveThreshold;
            Data.Config.PAUSE_LENGTH = pauseTime;
            Data.Config.CURSOR_DIFF = cursorDiff;
            Data.Config.ALERT_LEVEL1_THRESHOLD = level1Threshold;
            Data.Config.ALERT_LEVEL2_THRESHOLD = level2Threshold;
            Data.Config.ALERT_LEVEL3_THRESHOLD = level3Threshold;
            Data.Config.DEFAULT_EXPORT_FILENAME = txtDefaultExportFilename.getText();
            Data.Config.writeConfig();

            settingGUI.mainGUI.onConfigChange();
            settingGUI.settingScene.getWindow().hide();
        } catch (AlertMsgException e) {
            Data.Alert.getIconedAlert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}
