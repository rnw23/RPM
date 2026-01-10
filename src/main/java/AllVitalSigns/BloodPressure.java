package AllVitalSigns;

import Alarm.AlarmLevel;
public class BloodPressure extends VitalSign{

    private double diastole;
    public BloodPressure(double value, double diastole) {
        super(value); //value=systole
        this.diastole= diastole;
    }

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
    }
}