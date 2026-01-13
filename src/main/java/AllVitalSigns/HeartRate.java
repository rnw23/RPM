package AllVitalSigns;

import Alarm.*;

/**
 * HeartRate subclass
 * heart rate measurements in beats per min (bpm)
 */

public class HeartRate extends VitalSign {

    public HeartRate(double value) {
        super(value);
    }


    /**
     * Evaluates alarm level
     * GREEN: 50-90 bpm
     * AMBER: 40-50 or 90-110 bpm
     * RED: <40 or >110 bpm
     */
    @Override
    public AlarmLevel getAlarmLevel() {

        if (value < 40 || value > 110)
            return AlarmLevel.RED;

        if ((value >= 40 && value < 50) || (value > 90 && value <= 110))
            return AlarmLevel.AMBER;

        return AlarmLevel.GREEN;
    }
}