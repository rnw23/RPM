package Alarm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlarmEmailServiceTest {

    @Test
    void testEmailServiceConstructor() {
        AlarmEmailService service = new AlarmEmailService(
                "smtp.example.com", 587, "user@example.com", "password", true
        );

        assertNotNull(service); // simple constructor test
    }

    // Note: we avoid actually sending email in unit tests
    @Test
    void testSendEmailDoesNotThrow() {
        AlarmEmailService service = new AlarmEmailService(
                "smtp.example.com", 587, "user@example.com", "password", true
        );

        assertDoesNotThrow(() ->
                service.sendEmail("recipient@example.com", "Test Subject", "Test Body")
        );
    }
}
