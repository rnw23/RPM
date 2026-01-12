package Report;

import Alarm.AlarmLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AbnormalEvent {

    private static final DateTimeFormatter SECOND_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime second;
    private final String vitalType;
    private final double value;
    private final AlarmLevel level;

    public AbnormalEvent(LocalDateTime second,
                         String vitalType,
                         double value,
                         AlarmLevel level) {
        this.second = second;
        this.vitalType = vitalType;
        this.value = value;
        this.level = level;
    }

    public String getSecondText() {
        return second.format(SECOND_FORMAT);
    }

    public LocalDateTime getSecond() {
        return second;
    }

    public String getVitalType() {
        return vitalType;
    }

    public long getValue() {
        return Math.round(value);
    }

    public AlarmLevel getLevel() {
        return level;
    }
}
