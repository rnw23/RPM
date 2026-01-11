package RPM;

import java.util.ArrayList;
import java.util.List;

public class PatientBase {

    private final ArrayList<Patient> patients = new ArrayList<>();

    public PatientBase() {
        // Initial patients
        patients.add(new Patient(1, "John Smith", 28, "Ward A", "01234567890", 0));
        patients.add(new Patient(2, "Alice Brown", 35, "Ward B", "01234567891", 0));
        patients.add(new Patient(3, "David Jones", 42, "Ward C", "01234567892", 1));
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public Patient getPatient(int index) {
        if (index < 0 || index >= patients.size()) return null;
        return patients.get(index);
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    public int size() {
        return patients.size();
    }
}
