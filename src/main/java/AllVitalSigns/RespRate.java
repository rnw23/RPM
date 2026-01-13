package AllVitalSigns;

import Alarm.AlarmLevel;

/**
 * Resperation Rate subclass
 * RespRate measured in breaths per minute (bpm)
 */
public class RespRate extends VitalSign{
    public RespRate(double value) {
        super(value);
    }

    /**
     * Evaluates alarm level
     * GREEN: 12-20 bpm
     * AMBER: 9-12 or 20-25 bpm
     * RED: <9 or >25 bpm
     */
    @Override
    public AlarmLevel getAlarmLevel() {

        if (value < 9 || value > 25)
            return AlarmLevel.RED;

        if ((value >= 9 && value < 12) || (value > 20 && value <= 25))
            return AlarmLevel.AMBER;

        return AlarmLevel.GREEN;
    }
}