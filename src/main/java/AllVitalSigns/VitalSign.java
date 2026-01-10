package AllVitalSigns;

import Alarm.AlarmLevel;
import java.time.LocalDateTime;

public abstract class VitalSign {

    protected LocalDateTime dateTime;
    protected double value;


    public VitalSign(double value) {
        this.value = value;
        this.dateTime = LocalDateTime.now();
    }

    public double getValue() {
        return value;
    }



    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public abstract AlarmLevel getAlarmLevel();
}