package Alarm;

import AllVitalSigns.VitalSign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

class AlarmManagerTest {

    // Stub VitalSign for testing
    static class TestVitalSign extends VitalSign {
        private final AlarmLevel level;

        public TestVitalSign(double value, AlarmLevel level) {
            super(value); // call the superclass constructor
            this.level = level;
        }

        @Override
        public AlarmLevel getAlarmLevel() { return level; }

        @Override
        public java.time.LocalDateTime getDateTime() { return java.time.LocalDateTime.now(); }
    }

    private AlarmManager manager;
    private JPanel panel;

    @BeforeEach
    void setup() {
        manager = new AlarmManager();
        panel = new JPanel();
    }

    @Test
    void testSetCurrentPatientName() {
        manager.setCurrentPatientName(" Alice ");
        assertDoesNotThrow(() -> manager.setCurrentPatientName("Bob"));
    }

    @Test
    void testSetRecipientEmail() {
        manager.setRecipientEmail(" email@example.com ");
        assertDoesNotThrow(() -> manager.setRecipientEmail("test@domain.com"));
    }

    @Test
    void testApplyUIAndNotifyGreenLevelClosesDialog() {
        TestVitalSign vital = new TestVitalSign(100, AlarmLevel.GREEN);
        assertDoesNotThrow(() -> manager.applyUIAndNotify(vital, panel));
    }

    @Test
    void testApplyUIAndNotifyRedLevelTriggersDialog() {
        Alarm.setUiAlarmPopupsEnabled(true);
        TestVitalSign vital = new TestVitalSign(120, AlarmLevel.RED);
        assertDoesNotThrow(() -> manager.applyUIAndNotify(vital, panel));
    }

    @Test
    void testCloseAllDialogs() {
        manager.closeAllDialogs();
        assertDoesNotThrow(manager::closeAllDialogs);
    }
}
