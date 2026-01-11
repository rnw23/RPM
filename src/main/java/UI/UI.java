package UI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import Alarm.*;
import RPM.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
public class UI extends JFrame {

    private PatientBase patients;
    private Patient selectedPatient;
    private Patient patient;
    private JComboBox<String> patientSelector;
    private Timer timer;
    private JSlider windowSlider;
    private JLabel windowLabel;
    private int windowSec = 10; // default
    private PatientDetails patientInfo;

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
    private JPanel patientPanel;

    private JToggleButton heartbeatToggle;

    //alarm manager
    private final AlarmManager alarmManager = new AlarmManager();
    private boolean isEditingSettings = false;

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    public void initialise() {
        patient = new Patient(1, "John Smith", 35, "Ward B", "123456789");
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

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        patientPanel.setPreferredSize(new Dimension(900, 120));
        patientPanel.setLayout(new BoxLayout(patientPanel, BoxLayout.Y_AXIS));
        patientPanel.setPreferredSize(new Dimension(900, 80));
        selectorPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); // small height
        selectorPanel.setPreferredSize(new Dimension(900, 40));

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

        //input by nurse of email
        selectorPanel.add(new JLabel("Alert To: "));
        JTextField toField = new JTextField("your@outlook.com", 18);
        selectorPanel.add(toField);

        selectorPanel.add(new JLabel("Sender: "));
        JTextField senderField = new JTextField("your@outlook.com", 18);
        selectorPanel.add(senderField);

        selectorPanel.add(new JLabel("AppPwd: "));
        JPasswordField pwdField = new JPasswordField(16);
        selectorPanel.add(pwdField);

        //N slides

        windowLabel = new JLabel("Window: 10s");
        selectorPanel.add(windowLabel);

        windowSlider = new JSlider(5, 60, 10); // min=5s max=60s default=10s
        windowSlider.setMajorTickSpacing(5);
        windowSlider.setPaintTicks(true);

        selectorPanel.add(windowSlider);

        windowSlider.addChangeListener(e -> {
            windowSec = windowSlider.getValue();
            windowLabel.setText("Window: " + windowSec + "s");

            // refresch immediately
            //refreshCharts();
        });

        // ---- prevent alarm popups from interrupting nurse input ----
        toField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { isEditingSettings = true; }
            @Override public void focusLost(FocusEvent e) { isEditingSettings = false; }
        });

        senderField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { isEditingSettings = true; }
            @Override public void focusLost(FocusEvent e) { isEditingSettings = false; }
        });

        pwdField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { isEditingSettings = true; }
            @Override public void focusLost(FocusEvent e) { isEditingSettings = false; }
        });

        JButton applyEmailBtn = new JButton("Apply Email");
        selectorPanel.add(applyEmailBtn);

// ser defalut email sender
        alarmManager.setRecipientEmail(toField.getText());

        applyEmailBtn.addActionListener(e -> {
            String to = toField.getText().trim();
            String sender = senderField.getText().trim();
            String appPwd = new String(pwdField.getPassword());

            alarmManager.setRecipientEmail(to);

            // outlook SMTP:
            alarmManager.configureEmail(
                    "smtp.office365.com",
                    587,
                    sender,
                    appPwd,
                    true
            );

            JOptionPane.showMessageDialog(frame, "Email settings applied.");
        });
        //done

        selectorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        topPanel.add(selectorPanel);
        topPanel.add(patientPanel);
        patientInfo = new PatientDetails(selectedPatient);
        patientPanel.add(patientInfo, BorderLayout.CENTER);

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

        // Add heartbeat toggle button
        heartbeatToggle = new JToggleButton("Heartbeat Sound OFF");
        heartRatePanel.add(heartbeatToggle, BorderLayout.SOUTH);

        heartbeatToggle.addActionListener(e -> {
            if (heartbeatToggle.isSelected()) {
                heartbeatToggle.setText("Heartbeat Sound OFF");
            } else {
                heartbeatToggle.setText("Heartbeat Sound ON");
            }
        });


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

                patientInfo.updatePatient(selectedPatient);
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

            refreshCharts();



            // >>>new; ALARM+COLOR+NOTIFY
            var tList = selectedPatient.getTemperatureHistory();
            var hrList = selectedPatient.getHeartRateHistory();
            var rrList = selectedPatient.getRespRateHistory();
            var bpList = selectedPatient.getBloodPressureHistory();


            if (!isEditingSettings && !tList.isEmpty()) {
                alarmManager.applyUIAndNotify(tList.get(tList.size() - 1), bodyTemperaturePanel);
            }
            if (!isEditingSettings && !hrList.isEmpty()) {
                alarmManager.applyUIAndNotify(hrList.get(hrList.size() - 1), heartRatePanel);
            }
            if (!isEditingSettings && !rrList.isEmpty()) {
                alarmManager.applyUIAndNotify(rrList.get(rrList.size() - 1), respiratoryRatePanel);
            }
            if (!isEditingSettings && !bpList.isEmpty()) {
                alarmManager.applyUIAndNotify(bpList.get(bpList.size() - 1), bloodPressurePanel);
            }




            if (heartbeatToggle.isSelected()) {
                double currentHR = selectedPatient.getHr().getValue(); // bpm
                int interval = (int)(60000 / currentHR);          // ms between beats
                new Thread(() -> {
                    Heartbeat.playThump(300, 80); // your current tone
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException ex) { }
                }).start();
            }
            else{

            }

        });

        new Timer(33, e -> {
            var ecgHist = selectedPatient.getECGHistory();
            ecg.updateData(ecgHist);

            if (!isEditingSettings && !ecgHist.isEmpty()) {
                var latest = ecgHist.get(ecgHist.size() - 1);
                alarmManager.applyUIAndNotify(latest, ECGPanel);
            }
        }).start();

        timer.start();
    }
    private void refreshCharts() {
        tempChart.updateData(selectedPatient.getTempArr(windowSec));
        hrChart.updateData(selectedPatient.getHrArr(windowSec));
        rrChart.updateData(selectedPatient.getRrArr(windowSec));
        bpChart.updateData(selectedPatient.getBpArr(windowSec));
    }


}