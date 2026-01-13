package AllVitalSigns;

import Alarm.AlarmLevel;
import java.time.LocalDateTime;

/**
 * Abstract base class representing a generic vital sign.
 * All specific vital signs inherit from this.
 * All specific vital signs contain a value and an associated timestamp
 */

public abstract class VitalSign {

    protected LocalDateTime dateTime; //timestamp
    protected double value; //numeric value


    public VitalSign(double value) {
        this.value = value;
        this.dateTime = LocalDateTime.now();  //record current timestamp
    }

    public double getValue() {
        return value;
    } //value getter

    public LocalDateTime getDateTime() {
        return dateTime;
    }  //timestamp getter

    public abstract AlarmLevel getAlarmLevel();  //return alarm level of this vital sign value
}