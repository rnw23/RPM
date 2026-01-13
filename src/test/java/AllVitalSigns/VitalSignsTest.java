package AllVitalSigns;

import Alarm.AlarmLevel;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VitalSignsTest {

    // bp
    @Test
    void testBloodPressure() {
        // green
        BloodPressure bpGreen = new BloodPressure(120, 80);
        assertEquals(AlarmLevel.GREEN, bpGreen.getAlarmLevel());

        // amber low + high
        BloodPressure bpAmberLow = new BloodPressure(95, 70);
        assertEquals(AlarmLevel.AMBER, bpAmberLow.getAlarmLevel());
        BloodPressure bpAmberHigh = new BloodPressure(141, 90);
        assertEquals(AlarmLevel.AMBER, bpAmberHigh.getAlarmLevel());
        // red low + high
        BloodPressure bpRedLow = new BloodPressure(89, 60);
        assertEquals(AlarmLevel.RED, bpRedLow.getAlarmLevel());
        BloodPressure bpRedHigh = new BloodPressure(221, 100);
        assertEquals(AlarmLevel.RED, bpRedHigh.getAlarmLevel());

        // boundary checks
        assertEquals(AlarmLevel.AMBER, new BloodPressure(90, 60).getAlarmLevel());
        assertEquals(AlarmLevel.GREEN, new BloodPressure(100, 70).getAlarmLevel());
        assertEquals(AlarmLevel.GREEN, new BloodPressure(140, 90).getAlarmLevel());
        assertEquals(AlarmLevel.AMBER, new BloodPressure(220, 120).getAlarmLevel());

        // getter tests
        assertEquals(141, bpAmberHigh.getSystole());
        assertEquals(90, bpAmberHigh.getDiastole());

        // timestamp test
        assertNotNull(bpGreen.getDateTime());
    }

    // hr
    @Test
    void testHeartRate() {
        // green
        HeartRate hrGreen = new HeartRate(70);
        assertEquals(AlarmLevel.GREEN, hrGreen.getAlarmLevel());

        // amber low and high
        HeartRate hrAmberLow = new HeartRate(42);
        assertEquals(AlarmLevel.AMBER, hrAmberLow.getAlarmLevel());
        HeartRate hrAmberHigh = new HeartRate(109);
        assertEquals(AlarmLevel.AMBER, hrAmberHigh.getAlarmLevel());

        // red low and high
        HeartRate hrRedLow = new HeartRate(39);
        assertEquals(AlarmLevel.RED, hrRedLow.getAlarmLevel());
        HeartRate hrRedHigh = new HeartRate(111);
        assertEquals(AlarmLevel.RED, hrRedHigh.getAlarmLevel());

        // boundaries
        assertEquals(AlarmLevel.GREEN, new HeartRate(50).getAlarmLevel());
        assertEquals(AlarmLevel.GREEN, new HeartRate(90).getAlarmLevel());
        assertEquals(AlarmLevel.AMBER, new HeartRate(110).getAlarmLevel());
        assertEquals(AlarmLevel.AMBER, new HeartRate(40).getAlarmLevel());

        // Timestamp test
        assertNotNull(hrGreen.getDateTime());
    }

    //resprate
    @Test
    void testRespRate() {
        // green
        RespRate rrGreen = new RespRate(16);
        assertEquals(AlarmLevel.GREEN, rrGreen.getAlarmLevel());

        // amber low + high
        RespRate rrAmberLow = new RespRate(9);
        assertEquals(AlarmLevel.AMBER, rrAmberLow.getAlarmLevel());
        RespRate rrAmberHigh = new RespRate(21);
        assertEquals(AlarmLevel.AMBER, rrAmberHigh.getAlarmLevel());

        // red low + high
        RespRate rrRedLow = new RespRate(8);
        assertEquals(AlarmLevel.RED, rrRedLow.getAlarmLevel());
        RespRate rrRedHigh = new RespRate(26);
        assertEquals(AlarmLevel.RED, rrRedHigh.getAlarmLevel());

        // boundaries
        assertEquals(AlarmLevel.GREEN, new RespRate(12).getAlarmLevel());
        assertEquals(AlarmLevel.GREEN, new RespRate(20).getAlarmLevel());
        assertEquals(AlarmLevel.AMBER, new RespRate(9).getAlarmLevel());
        assertEquals(AlarmLevel.AMBER, new RespRate(25).getAlarmLevel());

        // timestamp
        assertNotNull(rrGreen.getDateTime());
    }

    // temp
    @Test
    void testTemperature() {
        // green
        Temperature tempGreen = new Temperature(37.0);
        assertEquals(AlarmLevel.GREEN, tempGreen.getAlarmLevel());

        // amber low + high
        Temperature tempAmberLow = new Temperature(35.2);
        assertEquals(AlarmLevel.AMBER, tempAmberLow.getAlarmLevel());
        Temperature tempAmberHigh = new Temperature(38.6);
        assertEquals(AlarmLevel.AMBER, tempAmberHigh.getAlarmLevel());

        // red low + high
        Temperature tempRedLow = new Temperature(34.0);
        assertEquals(AlarmLevel.RED, tempRedLow.getAlarmLevel());
        Temperature tempRedHigh = new Temperature(40.0);
        assertEquals(AlarmLevel.RED, tempRedHigh.getAlarmLevel());

        // boundaries
        assertEquals(AlarmLevel.GREEN, new Temperature(36.0).getAlarmLevel());
        assertEquals(AlarmLevel.GREEN, new Temperature(38.0).getAlarmLevel());
        assertEquals(AlarmLevel.AMBER, new Temperature(35.0).getAlarmLevel());
        assertEquals(AlarmLevel.AMBER, new Temperature(39.0).getAlarmLevel());

        // Timestamp
        assertNotNull(tempGreen.getDateTime());
    }

    // ecg
    @Test
    void testECG() {
        // green
        ECG ecgGreen = new ECG(0.5);
        assertEquals(AlarmLevel.GREEN, ecgGreen.getAlarmLevel());

        // amber positive/negative
        ECG ecgAmberPos = new ECG(0.75);
        assertEquals(AlarmLevel.AMBER, ecgAmberPos.getAlarmLevel());
        ECG ecgAmberNeg = new ECG(-0.8);
        assertEquals(AlarmLevel.AMBER, ecgAmberNeg.getAlarmLevel());

        // red positive/negative
        ECG ecgRedPos = new ECG(0.95);
        assertEquals(AlarmLevel.RED, ecgRedPos.getAlarmLevel());
        ECG ecgRedNeg = new ECG(-1.0);
        assertEquals(AlarmLevel.RED, ecgRedNeg.getAlarmLevel());

        //boundary
        assertEquals(AlarmLevel.AMBER, new ECG(0.7).getAlarmLevel());
        assertEquals(AlarmLevel.RED, new ECG(0.9).getAlarmLevel());

        // Getter test
        assertEquals(0.95, ecgRedPos.getVoltage());

        // Timestamp
        assertNotNull(ecgGreen.getDateTime());
    }
}
