package DailyReport;

import java.time.LocalDateTime;

public class MinuteAverage {
    private LocalDateTime minute;
    private double avgHeartRate;
    private double avgRespRate;
    private double avgTemperature;


    public MinuteAverage(LocalDateTime minute,
                         double avgHeartRate,
                         double avgRespRate,
                         double avgTemperature) {
        this.minute = minute;
        this.avgHeartRate = avgHeartRate;
        this.avgRespRate = avgRespRate;
        this.avgTemperature = avgTemperature;
    }

    public LocalDateTime getMinute() {
        return minute;
    }

    public double getAvgHeartRate() {
        return avgHeartRate;
    }

    public double getAvgRespRate() {
        return avgRespRate;
    }

    public double getAvgTemperature() {
        return avgTemperature;
    }
}