package Report;

import Alarm.AlarmLevel;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DailyReportTest {

    @Test
    void testDailyReportCreationCreatesFile() {
        MinuteAverage avg = new MinuteAverage(
                LocalDateTime.now(),
                70,
                15,
                36.5,
                118,
                78
        );

        AbnormalEvent event = new AbnormalEvent(
                LocalDateTime.now(),
                "Temperature",
                39.2,
                AlarmLevel.RED
        );

        DailyReport report = new DailyReport(
                "Test Patient",
                LocalDate.now(),
                List.of(avg),
                List.of(event)
        );

        assertNotNull(report);
        assertTrue(Files.exists(report.getFilePath()));
    }

    @Test
    void testDailyReportWithEmptyDataDoesNotThrow() {
        assertDoesNotThrow(() -> new DailyReport(
                "Empty Patient",
                LocalDate.now(),
                List.of(),
                List.of()
        ));
    }
}