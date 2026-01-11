package AllVitalSigns;

import java.time.LocalDateTime;
import Alarm.AlarmLevel;

public class ECG extends VitalSign{
    protected LocalDateTime dateTime;
    protected double voltage;

    public ECG(double voltage){
        super(voltage);
        this.voltage = voltage;
        this.dateTime = LocalDateTime.now();
        this.value = voltage;
    }

    public double getVoltage() {
        return voltage;
    }

    @Override
    public AlarmLevel getAlarmLevel() {
        double abs = Math.abs(voltage);

        //range need yo be decided
        if (abs >= 0.9) return AlarmLevel.RED;
        if (abs >= 0.7) return AlarmLevel.AMBER;
        return AlarmLevel.GREEN;
    }



}