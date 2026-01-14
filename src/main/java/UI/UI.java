package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import Report.DailyReport;

import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

import Alarm.*;
import RPM.*;

/**
 * UI panel for the Remote Patient Monitoring system (a JPanel)
 * features includepatient selection, real-time vital sign visualisation,
 * alarms, audio heartbeat
 */
public class UI extends JPanel {

    //patient + patient selection
    private final PatientBase patients;
    private Patient selectedPatient;
    private PatientDetails patientInfo;
    private JComboBox<String> patientSelector;

    private Timer timer;

    //select graph display time window
    private JSlider windowSlider;
    private JLabel windowLabel;
    private int windowSec = 10;

    //vital chart panels
    private VitalSignPanel tempChart;
    private VitalSignPanel hrChart;
    private VitalSignPanel rrChart;
    private BloodPressurePanel bpChart;
    private ECGplot ecg;

    private JPanel bodyTemperaturePanel;
    private JPanel heartRatePanel;
    private JPanel respiratoryRatePanel;
    private JPanel bloodPressurePanel;
    private JPanel ECGPanel;

    //for heartbeat indicator
    private JToggleButton heartbeatToggle;
    private Timer heartbeatTimer;

    //alarm control
    private final AlarmManager alarmManager = new AlarmManager();
    private boolean isEditingSettings = false;

    //root panel
    private JPanel mainPanel;

    public UI(PatientBase patients) {
        this.patients = patients;
        initialise();
    }

    public void initialise() {

        selectedPatient = patients.getPatient(0); // default to display first patient
        alarmManager.setCurrentPatientName(selectedPatient.getName());

        //JFrame frame = new JFrame("Remote Patient Monitor");
        //frame.setSize(900, 900);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //mainPanel = new JPanel(new BorderLayout());
        //setContentPane(mainPanel);

        setLayout(new BorderLayout());
        mainPanel = this; // optional, or remove mainPanel entirely


        //top layout --> selector + patient info
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel patientPanel = new JPanel();
        patientPanel.setPreferredSize(new Dimension(900, 120));
        patientPanel.setLayout(new BoxLayout(patientPanel, BoxLayout.Y_AXIS));

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        selectorPanel.setPreferredSize(new Dimension(900, 40));

        //set up vital sign panel
        JPanel vitalSignsPanel = new JPanel(new GridLayout(2, 2));

        //set up ecg panel
        ECGPanel = new JPanel(new BorderLayout());
        ECGPanel.setPreferredSize(new Dimension(900, 150));

        //add all base panel to root panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(vitalSignsPanel, BorderLayout.CENTER);
        mainPanel.add(ECGPanel, BorderLayout.SOUTH);

        //selector --> patient selector, email setting, ns time window slider
        //  1. patient selector
        patientSelector = new JComboBox<>();
        for (Patient p : patients.getPatients()) patientSelector.addItem(p.getName());

        selectorPanel.add(new JLabel("Select Patient: "));
        selectorPanel.add(patientSelector);

        // 2. email settings
        selectorPanel.add(new JLabel("Alert To: "));
        JTextField toField = new JTextField("your@email.com", 18);
        selectorPanel.add(toField);

        selectorPanel.add(new JLabel("Sender: "));
        JTextField senderField = new JTextField("your@gmail.com", 18);
        selectorPanel.add(senderField);

        selectorPanel.add(new JLabel("AppPwd: "));
        JPasswordField pwdField = new JPasswordField(16);
        selectorPanel.add(pwdField);


        //3. time window size selector
        windowLabel = new JLabel("Window: 10s");
        selectorPanel.add(windowLabel);
        windowSlider = new JSlider(5, 60, 10);
        windowSlider.setMajorTickSpacing(5);
        windowSlider.setPaintTicks(true);
        selectorPanel.add(windowSlider);

        windowSlider.addChangeListener(e -> {
            windowSec = windowSlider.getValue();
            windowLabel.setText("Window: " + windowSec + "s");

            //update chart history window
            tempChart.setMaxPoints(windowSec);
            hrChart.setMaxPoints(windowSec);
            rrChart.setMaxPoints(windowSec);
            bpChart.setMaxPoints(windowSec);

            refreshCharts();
        });

        // prevent alarm popups from interrupting nurse input
        FocusAdapter focusGuard = new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { isEditingSettings = true; }
            @Override public void focusLost(FocusEvent e) { isEditingSettings = false; }
        };
        toField.addFocusListener(focusGuard);
        senderField.addFocusListener(focusGuard);
        pwdField.addFocusListener(focusGuard);

        JButton applyEmailBtn = new JButton("Apply Email");
        selectorPanel.add(applyEmailBtn);

        alarmManager.setRecipientEmail(toField.getText());

        applyEmailBtn.addActionListener(e -> {
            String to = toField.getText().trim();
            String sender = senderField.getText().trim();
            String appPwd = new String(pwdField.getPassword());

            alarmManager.setRecipientEmail(to);

            alarmManager.configureEmail(
                    "smtp.gmail.com",
                    587,
                    sender,
                    appPwd,
                    true
            );

            //frame
            JOptionPane.showMessageDialog(UI.this, "Email settings applied.");
        });


        //4. daily report
        JButton dailyBtn = new JButton("Generate Daily Report");
        selectorPanel.add(dailyBtn);

        dailyBtn.addActionListener(e -> {
            try {
                String idText = JOptionPane.showInputDialog(UI.this, "Enter Patient ID:");
                if (idText == null) return;
                int pid = Integer.parseInt(idText.trim());

                String dateText = JOptionPane.showInputDialog(UI.this, "Enter Date (YYYY-MM-DD):");
                if (dateText == null) return;
                LocalDate date = LocalDate.parse(dateText.trim());

                Patient target = patients.findById(pid);
                if (target == null) {
                    JOptionPane.showMessageDialog(UI.this, "No patient found with ID " + pid);
                    return;
                }

                target.finalizeCurrentMinute();

                DailyReport report = new DailyReport(
                        target.getName(),
                        date,
                        target.getMinuteAveragesForDate(date),
                        target.getAbnormalEventsForDate(date)
                );

                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(new java.io.File(report.getFilePath().getFileName().toString()));
                int result = chooser.showSaveDialog(UI.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    java.nio.file.Files.copy(
                            report.getFilePath(),
                            chooser.getSelectedFile().toPath(),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                    JOptionPane.showMessageDialog(UI.this, "Saved daily report.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(UI.this, "Failed. Check Patient ID and date format (YYYY-MM-DD).");
            }
        });

        topPanel.add(selectorPanel);
        topPanel.add(patientPanel);

        patientInfo = new PatientDetails(selectedPatient);
        patientPanel.add(patientInfo);

        // 5. vital charts
        bodyTemperaturePanel = new JPanel(new BorderLayout());
        heartRatePanel = new JPanel(new BorderLayout());
        respiratoryRatePanel = new JPanel(new BorderLayout());
        bloodPressurePanel = new JPanel(new BorderLayout());

        bodyTemperaturePanel.setOpaque(true);
        heartRatePanel.setOpaque(true);
        respiratoryRatePanel.setOpaque(true);
        bloodPressurePanel.setOpaque(true);
        ECGPanel.setOpaque(true);

        patientPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        bodyTemperaturePanel.setBorder(BorderFactory.createTitledBorder("Body Temperature (°C)"));
        heartRatePanel.setBorder(BorderFactory.createTitledBorder("Heart Rate (bpm)"));
        respiratoryRatePanel.setBorder(BorderFactory.createTitledBorder("Respiratory Rate (breaths/min)"));
        bloodPressurePanel.setBorder(BorderFactory.createTitledBorder("Blood Pressure (mmHg)"));
        ECGPanel.setBorder(BorderFactory.createTitledBorder("ECG"));

        vitalSignsPanel.add(bodyTemperaturePanel);
        vitalSignsPanel.add(heartRatePanel);
        vitalSignsPanel.add(respiratoryRatePanel);
        vitalSignsPanel.add(bloodPressurePanel);

        tempChart = new VitalSignPanel();
        hrChart = new VitalSignPanel();
        rrChart = new VitalSignPanel();
        bpChart = new BloodPressurePanel();
        ecg = new ECGplot();

        hrChart.setFixedRange(30, 130);
        rrChart.setFixedRange(5, 30);
        tempChart.setFixedRange(34, 40);

        tempChart.setMaxPoints(windowSec);
        hrChart.setMaxPoints(windowSec);
        rrChart.setMaxPoints(windowSec);
        bpChart.setMaxPoints(windowSec);

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

        //heartbeat
        heartbeatToggle = new JToggleButton("Heartbeat Sound OFF");
        heartRatePanel.add(heartbeatToggle, BorderLayout.SOUTH);

        heartbeatToggle.addActionListener(e -> {
            if (heartbeatToggle.isSelected()) {
                heartbeatToggle.setText("Heartbeat Sound ON");
                startHeartbeat();
            } else {
                heartbeatToggle.setText("Heartbeat Sound OFF");
                stopHeartbeat();
            }
        });

        // close handling
        //frame
        /*
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (timer != null) timer.stop();
                stopHeartbeat();

                // IMPORTANT: finalize reporting data so it is not lost at shutdown
                for (Patient p : patients.getPatients()) {
                    try { p.finalizeCurrentMinute(); } catch (Exception ignored) {}
                    try { p.finalizeOpenEpisodes(); } catch (Exception ignored) {}
                }

                alarmManager.closeAllDialogs();
            }
        });

         */

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0 && !isDisplayable()) {
                if (timer != null) timer.stop();
                stopHeartbeat();
                alarmManager.closeAllDialogs();
            }
        });

        /*addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                if (timer == null || !timer.isRunning()) {
                    startLiveUpdates();
                }
            }
        });

         */

        //switch patient
        patientSelector.addActionListener(e -> {
            int index = patientSelector.getSelectedIndex();
            if (index >= 0) {
                selectedPatient = patients.getPatient(index);
                alarmManager.setCurrentPatientName(selectedPatient.getName());
                patientInfo.updatePatient(selectedPatient);
                refreshCharts();
            }
        });

        //frame.setVisible(true);
        //alarmManager.setParentComponent(this);
        startLiveUpdates();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    //1Hz update loop for vitals
    private void startLiveUpdates() {
        timer = new Timer(1000, e -> {

            // update vitals for all patients
            for (Patient p : patients.getPatients()) p.updateVitals();
            refreshCharts();

            //update data history
            var tList = selectedPatient.getTemperatureHistory();
            var hrList = selectedPatient.getHeartRateHistory();
            var rrList = selectedPatient.getRespRateHistory();
            var bpList = selectedPatient.getBloodPressureHistory();
            var ecgHist = selectedPatient.getECGHistory();


            //alarm is user not editing settings
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
            if (!isEditingSettings && !ecgHist.isEmpty()) {
                alarmManager.applyUIAndNotify(ecgHist.get(ecgHist.size() - 1), ECGPanel);
            }

            // update ECG plot once per second (no extra 33ms timer)
            ecg.updateData(ecgHist);
        });

        timer.start();
    }

    //update all charts
    private void refreshCharts() {
        tempChart.updateData(selectedPatient.getTempArr(windowSec));
        hrChart.updateData(selectedPatient.getHrArr(windowSec));
        rrChart.updateData(selectedPatient.getRrArr(windowSec));
        bpChart.updateData(selectedPatient.getBpArr(windowSec));
    }


    private void startHeartbeat() {
        stopHeartbeat();

        heartbeatTimer = new Timer(600, evt -> {
            if (!heartbeatToggle.isSelected() || selectedPatient == null) return;

            double hr = selectedPatient.getHr().getValue();
            int interval = (int) Math.max(250, Math.min(2000, 60000.0 / Math.max(1.0, hr)));

            // play sound in background (tiny, controlled thread)
            new Thread(() -> Heartbeat.playThump(300, 80), "Heartbeat").start();

            heartbeatTimer.setDelay(interval);
        });

        heartbeatTimer.setRepeats(true);
        heartbeatTimer.start();
    }

    private void stopHeartbeat() {
        if (heartbeatTimer != null) {
            heartbeatTimer.stop();
            heartbeatTimer = null;
        }
    }
    /*
    public void startMonitoring() {
        if (timer != null && timer.isRunning()) return;

        timer = new Timer(1000, e -> {
            // update vitals
            // alarmManager.applyUIAndNotify(...)
        });
        timer.start();
    }

    public void stopMonitoring() {
        if (timer != null) timer.stop();
    }
    */

}