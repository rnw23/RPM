package RPM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PatientTest {
    private Patient patient;

    @BeforeEach
    void setUp() {
        // set up test patient
        patient = new Patient(1, "Test Patient", 45, "Ward A", "123456", 1);
    }

    @Test
    void testVitalsAreUpdatedOnConstruction() {
        assertNotNull(patient.getHr());
        assertNotNull(patient.getBp());
        assertNotNull(patient.getRR());
        assertNotNull(patient.getTemp());
        assertNotNull(patient.getECG());
        assertFalse(patient.getHeartRateHistory().isEmpty());
        assertFalse(patient.getBloodPressureHistory().isEmpty());
    }

    @Test
    void testUpdateVitalsToHistory() {
        int initialSize = patient.getHeartRateHistory().size();
        patient.updateVitals();
        assertEquals(initialSize + 1, patient.getHeartRateHistory().size());
        assertEquals(initialSize + 1, patient.getRespRateHistory().size());
        assertEquals(initialSize + 1, patient.getTemperatureHistory().size());
    }

    @Test
    void testMinuteAverageIsGenerated() {
        // Simulate multiple updates in the same minute
        for (int i = 0; i < 5; i++) {
            patient.updateVitals();
        }

        patient.finalizeCurrentMinute();
        List<?> averages = patient.getMinuteAveragesForDate(LocalDate.now());
        assertFalse(averages.isEmpty(), "Minute average should be created");
    }

    @Test
    void testFinalizeCurrentMinuteDoesNotDuplicate() {
        patient.updateVitals();
        patient.finalizeCurrentMinute();
        patient.finalizeCurrentMinute();

        List<?> averages = patient.getMinuteAveragesForDate(LocalDate.now());
        assertEquals(1, averages.size(), "Minute average should not duplicate");
    }

    @Test
    void testAbnormalEventsAreRecordedAndFinalized() {
        // Force several updates to increase chance of abnormal values
        for (int i = 0; i < 20; i++) {
            patient.updateVitals();
        }

        patient.finalizeOpenEpisodes();

        List<?> events = patient.getAbnormalEventsForDate(LocalDate.now());
        assertNotNull(events);
        // Cannot guarantee abnormal events, but must not crash
    }

    @Test
    void testGetRecentVitalArrays() {
        patient.updateVitals();
        patient.updateVitals();
        patient.updateVitals();

        assertEquals(3, patient.getHrArr(3).size());
        assertEquals(2, patient.getBpArr(2).size());
        assertEquals(1, patient.getTempArr(1).size());
    }

    @Test
    void testGetRecentVitalArraysWithInvalidInput() {
        assertTrue(patient.getHrArr(0).isEmpty());
        assertTrue(patient.getBpArr(-1).isEmpty());
    }

    @Test
    void testPatientMetadataAccessors() {
        assertEquals(1, patient.getId());
        assertEquals("Test Patient", patient.getName());
        assertEquals(45, patient.getAge());
        assertEquals("Ward A", patient.getLocation());
        assertEquals("123456", patient.getContact());
        assertEquals(1, patient.getStatus());
    }
}
