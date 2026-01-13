package AllVitalSigns;

import Alarm.*;

/**
 * Temperature subclass
 * body temperature in °C.
 */

public class Temperature extends VitalSign {

    public Temperature(double value) {
        super(value);
    }

    /**
     * Evaluates alarm level
     * GREEN: 36-38°C
     * AMBER: 35-36 or 38-39°C
     * RED: <36 or >38°C
     */
    @Override
    public AlarmLevel getAlarmLevel() {

        if (value < 35 || value > 39)
            return AlarmLevel.RED;

        if ((value >= 35 && value < 36) || (value > 38 && value <= 39))
            return AlarmLevel.AMBER;

        return AlarmLevel.GREEN;
    }
}