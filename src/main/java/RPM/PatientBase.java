package RPM;

import java.util.ArrayList;
import java.util.List;

/**
 * PatientBase to manage database of Patient objects
 * methods to add, access, and search for patients
 */
public class PatientBase {

    private final ArrayList<Patient> patients = new ArrayList<>();  //stores all patients

    public PatientBase() {}  //initialise empty patient list

    public List<Patient> getPatients() {
        return patients;
    }

    //retrieve patient by index
    public Patient getPatient(int index) {
        if (index < 0 || index >= patients.size()) return null;
        return patients.get(index);
    }

    //add new patient
    public void addPatient(Patient patient) {
        if (patient != null) patients.add(patient);
    }

    //return number of patients
    public int size() {
        return patients.size();
    }

    //search patient by patient ID
    public Patient findById(int id) {
        for (Patient p : patients) {
            if (p.getId() == id) return p;
        }
        return null;
    }
}
