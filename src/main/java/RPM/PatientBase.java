package RPM;

import java.util.ArrayList;
import java.util.List;

public class PatientBase {

    private final ArrayList<Patient> patients = new ArrayList<>();

    public PatientBase() {}

    public List<Patient> getPatients() {
        return patients;
    }

    public Patient getPatient(int index) {
        if (index < 0 || index >= patients.size()) return null;
        return patients.get(index);
    }

    public void addPatient(Patient patient) {
        if (patient != null) patients.add(patient);
    }

    public int size() {
        return patients.size();
    }

    public Patient findById(int id) {
        for (Patient p : patients) {
            if (p.getId() == id) return p;
        }
        return null;
    }
}
