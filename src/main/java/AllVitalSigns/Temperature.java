package AllVitalSigns;

import Alarm.*;

public class Temperature extends VitalSign {

    public Temperature(double value) {
        super(value);
    }

    @Override
    public AlarmLevel getAlarmLevel() {

        if (value < 35 || value > 39)
            return AlarmLevel.RED;

        if ((value >= 35 && value < 36) || (value > 38 && value <= 39))
            return AlarmLevel.AMBER;

        return AlarmLevel.GREEN;
    }
}