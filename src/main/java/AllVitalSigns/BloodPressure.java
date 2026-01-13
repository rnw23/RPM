package AllVitalSigns;

import Alarm.AlarmLevel;

/**
 * BloodPressure subclass
 * Measured in mmHg
 */

public class BloodPressure extends VitalSign{

    private double diastole;
    public BloodPressure(double value, double diastole) {
        super(value); //value=systole
        this.diastole= diastole; //contain an extra value compared to other vital signs
    }


    /**
     * Evaluates blood pressure (simplified to only consider systole pressure)
     * GREEN: 100-140 mmHg
     * AMBER: 90-100 or 140-220 bpm
     * RED: <90 or >220 mmHg
     */
    @Override
    public AlarmLevel getAlarmLevel() {

        if (value < 90 || value > 220)
            return AlarmLevel.RED;

        if ((value >= 140 && value < 220) || (value > 90 && value <= 100))
            return AlarmLevel.AMBER;

        return AlarmLevel.GREEN;
    }

    public double getDiastole(){
        return diastole;
    }

    public double getSystole(){
        return value;
    }  // more convenient getter name
}