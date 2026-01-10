package AllVitalSigns;

import Alarm.AlarmLevel;

public class RespRate extends VitalSign{
    public RespRate(double value) {
        super(value);
    }

    @Override
    public AlarmLevel getAlarmLevel() {

        if (value < 9 || value > 25)
            return AlarmLevel.RED;

        if ((value >= 9 && value < 12) || (value > 20 && value <= 25))
            return AlarmLevel.AMBER;

        return AlarmLevel.GREEN;
    }
}