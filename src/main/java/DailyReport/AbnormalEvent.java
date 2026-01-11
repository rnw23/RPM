package DailyReport;

import Alarm.AlarmLevel;
import AllVitalSigns.VitalSign;

import java.time.LocalDateTime;

public class AbnormalEvent {
    private String vitalName;
    private double value;
    private AlarmLevel level;
    private LocalDateTime dateTime;

    public AbnormalEvent(VitalSign vitalSign) {
        this.vitalName = vitalSign.getClass().getSimpleName();
        this.value = vitalSign.getValue();
        this.level = vitalSign.getAlarmLevel();
        this.dateTime = vitalSign.getDateTime();
    }

    public String getVitalName() {
        return vitalName;
    }

    public double getValue() {
        return value;
    }

    public AlarmLevel getLevel() {
        return level;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}