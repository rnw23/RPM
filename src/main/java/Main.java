import javax.swing.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Patient p = new Patient(1,"John Smith", 35);

        while (true) {
            p.updateVitals();
            System.out.println(p.PatientDisplay());
            Thread.sleep(1000);
        }
    }
}