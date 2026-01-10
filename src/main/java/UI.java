import javax.swing.*;
import java.awt.*;
import Alarm.AlarmManager;


public class UI extends JFrame {

    private Patient patient;
    private Timer timer;

    private VitalSignPanel tempChart;
    private VitalSignPanel hrChart;
    private VitalSignPanel rrChart;
    private BloodPressurePanel bpChart;

    //new added
    private JPanel bodyTemperaturePanel;
    private JPanel heartRatePanel;
    private JPanel respiratoryRatePanel;
    private JPanel bloodPressurePanel;

    //alarm manager
    private final AlarmManager alarmManager = new AlarmManager();
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



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

        //member virable changed
        bodyTemperaturePanel = new JPanel(new BorderLayout());
        heartRatePanel =  new JPanel(new BorderLayout());
        respiratoryRatePanel = new JPanel(new BorderLayout());
        bloodPressurePanel = new JPanel(new BorderLayout());

        //background color
        bodyTemperaturePanel.setOpaque(true);
        heartRatePanel.setOpaque(true);
        respiratoryRatePanel.setOpaque(true);
        bloodPressurePanel.setOpaque(true);


        patientPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        bodyTemperaturePanel.setBorder(BorderFactory.createTitledBorder("Body Temperature (°C)"));
        heartRatePanel.setBorder(BorderFactory.createTitledBorder("Heart Rate (bpm)"));
        respiratoryRatePanel.setBorder(BorderFactory.createTitledBorder("Respiratory Rate (breaths/min)"));
        bloodPressurePanel.setBorder(BorderFactory.createTitledBorder("Blood Pressure (mmHg)"));
        ECGPanel.setBorder(BorderFactory.createTitledBorder("ECG"));

        vitalSignsPanel.setLayout(new GridLayout(2,2));

        vitalSignsPanel.add(bodyTemperaturePanel);
        vitalSignsPanel.add(heartRatePanel);
        vitalSignsPanel.add(respiratoryRatePanel);
        vitalSignsPanel.add(bloodPressurePanel);

        tempChart = new VitalSignPanel();
        hrChart   = new VitalSignPanel();
        rrChart   = new VitalSignPanel();
        bpChart = new BloodPressurePanel();


        bodyTemperaturePanel.add(tempChart, BorderLayout.CENTER);
        heartRatePanel.add(hrChart, BorderLayout.CENTER);
        respiratoryRatePanel.add(rrChart, BorderLayout.CENTER);
        bloodPressurePanel.add(bpChart, BorderLayout.CENTER);

        tempChart.setOpaque(false);
        hrChart.setOpaque(false);
        rrChart.setOpaque(false);
        bpChart.setOpaque(false);

        // >>> ADD: when closing the main window, stop updates + close all alarm dialogs
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (timer != null) timer.stop();          // stop generating alarms
                alarmManager.closeAllDialogs();           // close any open alarm popups
            }
        });

        frame.setVisible(true);
        startLiveUpdates();
    }

    private void startLiveUpdates() {

        timer = new Timer(1000, e -> {
            patient.updateVitals();

            tempChart.updateData(patient.getTemperatureHistory());
            hrChart.updateData(patient.getHeartRateHistory());
            rrChart.updateData(patient.getRespRateHistory());
            bpChart.updateData(patient.getBloodPressureHistory());

            // >>>new; ALARM+COLOR+NOTIFY
            var tList  = patient.getTemperatureHistory();
            var hrList = patient.getHeartRateHistory();
            var rrList = patient.getRespRateHistory();
            var bpList = patient.getBloodPressureHistory();

            if (!tList.isEmpty()) {
                alarmManager.applyUIAndNotify(tList.get(tList.size()-1), bodyTemperaturePanel);
            }
            if (!hrList.isEmpty()) {
                alarmManager.applyUIAndNotify(hrList.get(hrList.size()-1), heartRatePanel);
            }
            if (!rrList.isEmpty()) {
                alarmManager.applyUIAndNotify(rrList.get(rrList.size()-1), respiratoryRatePanel);
            }
            if (!bpList.isEmpty()) {
                alarmManager.applyUIAndNotify(bpList.get(bpList.size()-1), bloodPressurePanel);
            }
            // >>>>>>>>

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