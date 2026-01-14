package RPM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatientBaseTest {

    private PatientBase patientBase;
    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setUp() {
        patientBase = new PatientBase();
        patient1 = new Patient(1, "Ann", 30, "Ward A", "123", 1);
        patient2 = new Patient(2, "Ben", 45, "Ward B", "223", 2);
    }

    @Test
    void testInitialPatientBaseIsEmpty() {
        assertEquals(0, patientBase.size());
        assertTrue(patientBase.getPatients().isEmpty());
    }

    @Test
    void testAddPatientIncreasesSize() {
        patientBase.addPatient(patient1);
        patientBase.addPatient(patient2);

        assertEquals(2, patientBase.size());
    }

    @Test
    void testAddNullPatientDoesNothing() {
        patientBase.addPatient(null);

        assertEquals(0, patientBase.size());
    }

    @Test
    void testGetPatientValidIndex() {
        patientBase.addPatient(patient1);
        patientBase.addPatient(patient2);

        assertEquals(patient1, patientBase.getPatient(0));
        assertEquals(patient2, patientBase.getPatient(1));
    }

    @Test
    void testGetPatientInvalidIndexReturnsNull() {
        patientBase.addPatient(patient1);

        assertNull(patientBase.getPatient(-1));
        assertNull(patientBase.getPatient(1));
        assertNull(patientBase.getPatient(100));
    }

    @Test
    void testFindByIdReturnsCorrectPatient() {
        patientBase.addPatient(patient1);
        patientBase.addPatient(patient2);

        Patient found = patientBase.findById(2);
        assertNotNull(found);
        assertEquals("Bob", found.getName());
    }

    @Test
    void testFindByIdReturnsNullWhenNotFound() {
        patientBase.addPatient(patient1);

        assertNull(patientBase.findById(999));
    }

    @Test
    void testGetPatientsReturnsLiveList() {
        patientBase.addPatient(patient1);

        assertEquals(1, patientBase.getPatients().size());
    }
}
