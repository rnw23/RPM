package Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MinuteAverage {

    private static final DateTimeFormatter MINUTE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LocalDateTime minute;
    private final double avgHeartRate;
    private final double avgRespRate;
    private final double avgTemperature;
    private final double avgSystolic;
    private final double avgDiastolic;

    public MinuteAverage(LocalDateTime minute,
                         double avgHeartRate,
                         double avgRespRate,
                         double avgTemperature,
                         double avgSystolic,
                         double avgDiastolic) {
        this.minute = minute;
        this.avgHeartRate = avgHeartRate;
        this.avgRespRate = avgRespRate;
        this.avgTemperature = avgTemperature;
        this.avgSystolic = avgSystolic;
        this.avgDiastolic = avgDiastolic;
    }

    public String getMinuteText() {
        return minute.format(MINUTE_FORMAT);
    }

    public LocalDateTime getMinute() {
        return minute;
    }

    public long getAvgHeartRate() {
        return Math.round(avgHeartRate);
    }

    public long getAvgRespRate() {
        return Math.round(avgRespRate);
    }

    public long getAvgTemperature() {
        return Math.round(avgTemperature);
    }

    public String getAvgBloodPressure() {
        long sys = Math.round(avgSystolic);
        long dia = Math.round(avgDiastolic);
        return sys + "/" + dia;
    }
}
