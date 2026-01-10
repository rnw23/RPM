package AllVitalSigns;

import Alarm.*;

public class HeartRate extends VitalSign {

    public HeartRate(double value) {
        super(value);
    }

    @Override
    public AlarmLevel getAlarmLevel() {

        if (value <= 40 || value > 110)
            return AlarmLevel.RED;

        if ((value >= 41 && value <= 50) || (value > 90 && value <= 100))
            return AlarmLevel.AMBER;

        return AlarmLevel.GREEN;
    }
}