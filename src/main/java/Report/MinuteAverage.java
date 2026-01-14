package Report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MinuteAverage stores averaged vital sign values for a single minute bucket.
 * Immutable after construction to simplify reporting and reduce accidental mutation when exported to Excel.
 */
public class MinuteAverage {

    // Formatted minute timestamp for report display
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

    // Raw minute timestamp used for data filtering
    public LocalDateTime getMinute() {
        return minute;
    }

    // Rounded, average heart rate
    public long getAvgHeartRate() {
        return Math.round(avgHeartRate);
    }

    // Rounded, average respiratory rate
    public long getAvgRespRate() {
        return Math.round(avgRespRate);
    }

    // Rounded, average temperature
    public long getAvgTemperature() {
        return Math.round(avgTemperature);
    }

    // Rounded, average blood pressure formatted as "SYS/DIA"
    public String getAvgBloodPressure() {
        long sys = Math.round(avgSystolic);
        long dia = Math.round(avgDiastolic);
        return sys + "/" + dia;
    }
}
