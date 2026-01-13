package AllVitalSigns;

import Alarm.AlarmLevel;

/**
 * ECG subclass
 * Measured in volts
 * The voltage is assumed to be normalized between -1.0 and +1.0
 */
public class ECG extends VitalSign{

    public ECG(double voltage){
        super(voltage);
    }

    public double getVoltage() {
        return getValue();
    }

    /**
     * Evaluates alarm level by absolute ECG voltage
     * RED: >= 0.9V
     * AMBER: >= 0.7V
     * GREEN: < 0.7V
     */
    @Override
    public AlarmLevel getAlarmLevel() {
        double abs = Math.abs(getValue());
        if (abs >= 0.9) return AlarmLevel.RED;
        if (abs >= 0.7) return AlarmLevel.AMBER;
        return AlarmLevel.GREEN;
    }



}