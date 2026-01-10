import javax.swing.*;
import java.awt.*;
import Alarm.AlarmManager;


public class UI extends JFrame {

    private Patient patient;

    private VitalSignPanel tempChart;
    private VitalSignPanel hrChart;
    private VitalSignPanel rrChart;
    private BloodPressurePanel bpChart;

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // >>>【新增 3】把 4 个外层 panel 变成“成员变量”，这样 Timer 里才能改背景色
    private JPanel bodyTemperaturePanel;
    private JPanel heartRatePanel;
    private JPanel respiratoryRatePanel;
    private JPanel bloodPressurePanel;

    // >>>【新增 4】报警管理器（负责：改背景色 + 弹窗/声音/邮件冷却）
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

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // >>>【改动点 1】这 4 个 panel 不要用局部变量了，改成上面那 4 个成员变量
        bodyTemperaturePanel = new JPanel(new BorderLayout());
        heartRatePanel =  new JPanel(new BorderLayout());
        respiratoryRatePanel = new JPanel(new BorderLayout());
        bloodPressurePanel = new JPanel(new BorderLayout());

        // >>>【新增】让背景色能生效（Swing 默认有些容器不一定显示背景）
        bodyTemperaturePanel.setOpaque(true);
        heartRatePanel.setOpaque(true);
        respiratoryRatePanel.setOpaque(true);
        bloodPressurePanel.setOpaque(true);
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


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

            // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
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
