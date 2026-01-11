package UI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import Alarm.*;
import RPM.*;

public class UI extends JFrame {

    private PatientBase patients;
    private Patient selectedPatient;
    private JComboBox<String> patientSelector;
    private Timer timer;

    private VitalSignPanel tempChart;
    private VitalSignPanel hrChart;
    private VitalSignPanel rrChart;
    private BloodPressurePanel bpChart;
    private ECGplot ecg;

    //new added
    private JPanel bodyTemperaturePanel;
    private JPanel heartRatePanel;
    private JPanel respiratoryRatePanel;
    private JPanel bloodPressurePanel;
    private JPanel ECGPanel;

    //alarm manager
    private final AlarmManager alarmManager = new AlarmManager();
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    public void initialise() {
        patients = new PatientBase();
        selectedPatient = patients.getPatient(0);


        JFrame frame = new JFrame("Remote Patient Monitor");
        frame.setSize(900, 900);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        JPanel patientPanel = new JPanel();
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel vitalSignsPanel = new JPanel();
        JPanel topPanel = new JPanel();
        ECGPanel = new JPanel();

        patientPanel.setLayout(new BoxLayout(patientPanel, BoxLayout.Y_AXIS));
        patientPanel.setPreferredSize(new Dimension(900, 80));
        ECGPanel.setPreferredSize(new Dimension(900, 150));
        ECGPanel.setLayout(new BorderLayout());

        frame.setContentPane(mainPanel);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(vitalSignsPanel, BorderLayout.CENTER);
        mainPanel.add(ECGPanel, BorderLayout.SOUTH);

        patientSelector = new JComboBox<>();

        for (Patient p : patients.getPatients()) {
            patientSelector.addItem(p.getName());
        }

        selectorPanel.add(new JLabel("Select Patient: "));
        selectorPanel.add(patientSelector);
        topPanel.add(selectorPanel);
        topPanel.add(patientPanel);
        //member variable changed
        bodyTemperaturePanel = new JPanel(new BorderLayout());
        heartRatePanel = new JPanel(new BorderLayout());
        respiratoryRatePanel = new JPanel(new BorderLayout());
        bloodPressurePanel = new JPanel(new BorderLayout());

        //background color
        bodyTemperaturePanel.setOpaque(true);
        heartRatePanel.setOpaque(true);
        respiratoryRatePanel.setOpaque(true);
        bloodPressurePanel.setOpaque(true);
        ECGPanel.setOpaque(true);

        // labelling
        patientPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        bodyTemperaturePanel.setBorder(BorderFactory.createTitledBorder("Body Temperature (°C)"));
        heartRatePanel.setBorder(BorderFactory.createTitledBorder("Heart Rate (bpm)"));
        respiratoryRatePanel.setBorder(BorderFactory.createTitledBorder("Respiratory Rate (breaths/min)"));
        bloodPressurePanel.setBorder(BorderFactory.createTitledBorder("Blood Pressure (mmHg)"));
        ECGPanel.setBorder(BorderFactory.createTitledBorder("ECG"));

        vitalSignsPanel.setLayout(new GridLayout(2, 2));
        vitalSignsPanel.add(bodyTemperaturePanel);
        vitalSignsPanel.add(heartRatePanel);
        vitalSignsPanel.add(respiratoryRatePanel);
        vitalSignsPanel.add(bloodPressurePanel);

        tempChart = new VitalSignPanel();
        hrChart = new VitalSignPanel();
        rrChart = new VitalSignPanel();
        bpChart = new BloodPressurePanel();
        ecg = new ECGplot();

        ecg = new ECGplot();
        ecg.setTimeWindowSeconds(10);
        ecg.setSamplesPerSecond(100);
        ecg.setVoltageRange(1.0); // because your generator is -1..1

        //ECGPanel.add(ecg, BorderLayout.CENTER);

        bodyTemperaturePanel.add(tempChart, BorderLayout.CENTER);
        heartRatePanel.add(hrChart, BorderLayout.CENTER);
        respiratoryRatePanel.add(rrChart, BorderLayout.CENTER);
        bloodPressurePanel.add(bpChart, BorderLayout.CENTER);
        ECGPanel.add(ecg, BorderLayout.CENTER);

        tempChart.setOpaque(false);
        hrChart.setOpaque(false);
        rrChart.setOpaque(false);
        bpChart.setOpaque(false);
        ecg.setOpaque(false);

        // >>> ADD: when closing the main window, stop updates + close all alarm dialogs
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (timer != null) timer.stop();          // stop generating alarms
                alarmManager.closeAllDialogs();           // close any open alarm popups
            }
        });

        patientSelector.addActionListener(e -> {
            int index = patientSelector.getSelectedIndex();
            if (index >= 0) {
                selectedPatient = patients.getPatient(index);

                // Refresh charts immediately
                tempChart.updateData(selectedPatient.getTemperatureHistory());
                hrChart.updateData(selectedPatient.getHeartRateHistory());
                rrChart.updateData(selectedPatient.getRespRateHistory());
                bpChart.updateData(selectedPatient.getBloodPressureHistory());
            }
        });


        frame.setVisible(true);
        startLiveUpdates();
    }

    private void startLiveUpdates() {

        timer = new Timer(1000, e -> {
            for (Patient p : patients.getPatients()) {
                p.updateVitals();    // all patients continue generating data
            }

            tempChart.updateData(selectedPatient.getTemperatureHistory());
            hrChart.updateData(selectedPatient.getHeartRateHistory());
            rrChart.updateData(selectedPatient.getRespRateHistory());
            bpChart.updateData(selectedPatient.getBloodPressureHistory());



            // >>>new; ALARM+COLOR+NOTIFY
            var tList = selectedPatient.getTemperatureHistory();
            var hrList = selectedPatient.getHeartRateHistory();
            var rrList = selectedPatient.getRespRateHistory();
            var bpList = selectedPatient.getBloodPressureHistory();


            if (!tList.isEmpty()) {
                alarmManager.applyUIAndNotify(tList.get(tList.size() - 1), bodyTemperaturePanel);
            }
            if (!hrList.isEmpty()) {
                alarmManager.applyUIAndNotify(hrList.get(hrList.size() - 1), heartRatePanel);
            }
            if (!rrList.isEmpty()) {
                alarmManager.applyUIAndNotify(rrList.get(rrList.size() - 1), respiratoryRatePanel);
            }
            if (!bpList.isEmpty()) {
                alarmManager.applyUIAndNotify(bpList.get(bpList.size() - 1), bloodPressurePanel);
            }
            // >>>>>>>>

        });

        new Timer(33, e -> {
            ecg.updateData(selectedPatient.getECGHistory());

        }).start();

        timer.start();
    }
}