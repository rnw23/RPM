package Alarm;

import AllVitalSigns.VitalSign;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AlarmTest {

    // Stub VitalSign for testing
    static class TestVitalSign extends VitalSign {
        private final AlarmLevel level;

        public TestVitalSign(double value, AlarmLevel level) {
            super(value); // call the superclass constructor with value
            this.level = level;
        }

        @Override
        public AlarmLevel getAlarmLevel() { return level; }

        @Override
        public LocalDateTime getDateTime() { return LocalDateTime.now(); }
    }

    @Test
    void testAlarmProperties() {
        VitalSign vital = new TestVitalSign(120.5, AlarmLevel.RED);
        Alarm alarm = new Alarm(vital);

        assertEquals(AlarmLevel.RED, alarm.getAlarmLevel());
        assertTrue(alarm.getMessage().contains("120.5"));
        assertNotNull(alarm.getDateTime());
    }

    @Test
    void testUiAlarmPopupsEnabledFlag() {
        Alarm.setUiAlarmPopupsEnabled(true);
        assertTrue(Alarm.isUiAlarmPopupsEnabled());

        Alarm.setUiAlarmPopupsEnabled(false);
        assertFalse(Alarm.isUiAlarmPopupsEnabled());
    }
}
