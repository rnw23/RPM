package AllVitalSigns;

import java.time.LocalDateTime;

public class ECG {
    protected LocalDateTime dateTime;
    protected double voltage;
    public ECG(double voltage){
        this.voltage = voltage;
        this.dateTime = LocalDateTime.now();
    }
}