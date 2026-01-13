package Alarm;

import AllVitalSigns.VitalSign;
import java.time.LocalDateTime;

public class Alarm {
    private VitalSign vitalSign;
    private AlarmLevel alarmLevel;
    private String message;
    private LocalDateTime dateTime;
    private static volatile boolean uiAlarmPopupsEnabled = false;

    public Alarm(VitalSign vitalSign) {
        this.vitalSign = vitalSign;
        this.alarmLevel = vitalSign.getAlarmLevel();
        this.dateTime = vitalSign.getDateTime();
        this.message = buildMessage();
    }

    private String buildMessage() {
        return String.format(
                "| Vital: %s | Value: %.1f | Level: %s |",
                vitalSign.getClass().getSimpleName(),
                vitalSign.getValue(),
                alarmLevel
        );
    }

    public AlarmLevel getAlarmLevel() { return alarmLevel; }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    // Global UI gate: allow alarms to be computed, but block alarm UI popups
    public static void setUiAlarmPopupsEnabled(boolean enabled) {
        uiAlarmPopupsEnabled = enabled;
    }

    public static boolean isUiAlarmPopupsEnabled() {
        return uiAlarmPopupsEnabled;
    }

}
