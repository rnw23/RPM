package Alarm;

import AllVitalSigns.VitalSign;

import java.time.LocalDateTime;

public class Alarm {
    private VitalSign vitalSign;
    private AlarmLevel alarmLevel;
    private String message;
    private LocalDateTime dateTime;

    public Alarm(VitalSign vitalSign) {
        this.vitalSign = vitalSign;
        this.alarmLevel = vitalSign.getAlarmLevel();
        this.dateTime = vitalSign.getDateTime();
        this.message = buildMessage();
    }

    private String buildMessage() {
        return String.format(
                " | Vital: %s | Value: %.1f | Level: %s",
                vitalSign.getClass().getSimpleName(),
                vitalSign.getValue(),
                alarmLevel
        );
    }

    public AlarmLevel getAlarmLevel() {
        return alarmLevel;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    // UML method
    public void sendNotification() {
        System.out.println("🚨 ALARM: " + message);
        // Later you can connect email / sound here
    }
}
