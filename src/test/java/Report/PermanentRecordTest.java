package Report;

import Alarm.AlarmLevel;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PermanentRecordTest {

    @Test
    void testPermanentRecordConstructorCreatesFile() {
        PermanentRecord record = new PermanentRecord("Test Patient");

        assertNotNull(record);
        assertTrue(Files.exists(record.getFilePath()));
    }

    @Test
    void testAppendMinuteAverageDoesNotThrow() {
        PermanentRecord record = new PermanentRecord("Test Patient");

        MinuteAverage avg = new MinuteAverage(
                LocalDateTime.now(),
                72.5,
                16.0,
                36.8,
                120,
                80
        );

        assertDoesNotThrow(() -> record.appendMinuteAverage(avg));
    }

    @Test
    void testAppendAbnormalEventDoesNotThrow() {
        PermanentRecord record = new PermanentRecord("Test Patient");

        AbnormalEvent event = new AbnormalEvent(
                LocalDateTime.now(),
                "HeartRate",
                130,
                AlarmLevel.RED
        );

        assertDoesNotThrow(() -> record.appendAbnormalEvent(event));
    }

    @Test
    void testCopyToCreatesExportedFile() throws Exception {
        PermanentRecord record = new PermanentRecord("Test Patient");

        Path destination = Path.of("test_export_permanent.xlsx");

        record.copyTo(destination);

        assertTrue(Files.exists(destination));

        // cleanup
        Files.deleteIfExists(destination);
    }
}