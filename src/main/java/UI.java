import javax.swing.*;
import java.awt.*;

public class UI extends JFrame {

    private Patient patient;

    private VitalSignPanel tempChart;
    private VitalSignPanel hrChart;
    private VitalSignPanel rrChart;
    private BloodPressurePanel bpChart;



    public void initialise(){
        patient = new Patient(1, "John Smith", 35);

        JFrame frame = new JFrame("Patient Monitor");
        frame.setSize(900,900);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        JPanel patientPanel = new JPanel();
        JPanel vitalSignsPanel = new JPanel();
        JPanel ECGPanel = new JPanel();

        patientPanel.setPreferredSize(new Dimension(900, 80));
        ECGPanel.setPreferredSize(new Dimension(900, 150));

        frame.setContentPane(mainPanel);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(patientPanel,BorderLayout.NORTH);
        mainPanel.add(vitalSignsPanel,BorderLayout.CENTER);
        mainPanel.add(ECGPanel,BorderLayout.SOUTH);

        JPanel bodyTemperature = new JPanel(new BorderLayout());
        JPanel heartRate =  new JPanel(new BorderLayout());
        JPanel respiratoryRate = new JPanel(new BorderLayout());
        JPanel bloodPressure = new JPanel(new BorderLayout());


        patientPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        bodyTemperature.setBorder(BorderFactory.createTitledBorder("Body Temperature (°C)"));
        heartRate.setBorder(BorderFactory.createTitledBorder("Heart Rate (bpm)"));
        respiratoryRate.setBorder(BorderFactory.createTitledBorder("Respiratory Rate (breaths/min)"));
        bloodPressure.setBorder(BorderFactory.createTitledBorder("Blood Pressure (mmHg)"));
        ECGPanel.setBorder(BorderFactory.createTitledBorder("ECG"));

        vitalSignsPanel.setLayout(new GridLayout(2,2));
        vitalSignsPanel.add(bodyTemperature);
        vitalSignsPanel.add(heartRate);
        vitalSignsPanel.add(respiratoryRate);
        vitalSignsPanel.add(bloodPressure);

        tempChart = new VitalSignPanel();
        hrChart   = new VitalSignPanel();
        rrChart   = new VitalSignPanel();
        bpChart = new BloodPressurePanel();


        bodyTemperature.add(tempChart, BorderLayout.CENTER);
        heartRate.add(hrChart, BorderLayout.CENTER);
        respiratoryRate.add(rrChart, BorderLayout.CENTER);
        bloodPressure.add(bpChart, BorderLayout.CENTER);

        frame.setVisible(true);
        startLiveUpdates();
    }

    private void startLiveUpdates() {

        Timer timer = new Timer(1000, e -> {
            patient.updateVitals();

            tempChart.updateData(patient.getTemperatureHistory());
            hrChart.updateData(patient.getHeartRateHistory());
            rrChart.updateData(patient.getRespRateHistory());
            bpChart.updateData(patient.getBloodPressureHistory());

        });

        timer.start();
    }
}

/* ----- Potential Usage -----
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UI().initialise();
        });
    }
}

*/
