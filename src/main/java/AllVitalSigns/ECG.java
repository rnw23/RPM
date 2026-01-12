package AllVitalSigns;

import Alarm.AlarmLevel;

public class ECG extends VitalSign{

    public ECG(double voltage){
        super(voltage);
    }

    public double getVoltage() {
        return getValue();
    }

    @Override
    public AlarmLevel getAlarmLevel() {
        double abs = Math.abs(getValue());
        if (abs >= 0.9) return AlarmLevel.RED;
        if (abs >= 0.7) return AlarmLevel.AMBER;
        return AlarmLevel.GREEN;
    }



}