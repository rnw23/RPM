package Report;

import Alarm.AlarmLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AbnormalEvent represents an abnormal "episode" for a vital sign.
 * Start time: when the vital first enters AMBER or RED range
 * End time: updated while the abnormal condition persists; finalised when it returns to GREEN
 * Value range: tracks min and max values observed during the episode
 * Severity level: fixed for the episode (AMBER or RED).
 */
public class AbnormalEvent {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String vitalType;
    private final AlarmLevel level;

    private final LocalDateTime start;
    private LocalDateTime end;

    private double minValue;
    private double maxValue;

    public AbnormalEvent(LocalDateTime start,
                         String vitalType,
                         double initialValue,
                         AlarmLevel level) {
        this.start = start;
        this.end = start;
        this.vitalType = vitalType;
        this.level = level;
        this.minValue = initialValue;
        this.maxValue = initialValue;
    }

    // Extends the episode window and updates min/max range.
    public void update(LocalDateTime time, double value) {
        this.end = time;
        this.minValue = Math.min(this.minValue, value);
        this.maxValue = Math.max(this.maxValue, value);
    }

    public String getStartText() { return start.format(FORMAT); }
    public String getEndText() { return end.format(FORMAT); }

    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }

    public String getVitalType() { return vitalType; }
    public AlarmLevel getLevel() { return level; }

    // Export user-friendly text for min/max range
    public String getValueRangeText() {
        return String.format("%.1f - %.1f", minValue, maxValue);
    }

    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
}