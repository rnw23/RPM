import RPM.Patient;
import RPM.PatientBase;
import UI.App;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        PatientBase base = new PatientBase();
        base.addPatient(new Patient(1, "John Smith", 28, "Ward A", "01234567890", 0));
        base.addPatient(new Patient(2, "Alice Brown", 35, "Ward B", "01234567891", 0));
        base.addPatient(new Patient(3, "David Jones", 42, "Ward C", "01234567892", 1));
        base.addPatient(new Patient(4, "Jennifer Baker", 49, "Ward D", "01234567893", 1));

        SwingUtilities.invokeLater(() -> new App(base).setVisible(true));

    }
}