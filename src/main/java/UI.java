import javax.swing.*;
import java.awt.*;

public class UI extends JFrame {

    public void initialise(){
        JFrame frame = new JFrame("Patient Monitor");
        frame.setSize(900,500);
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

        JPanel bodyTemperature = new JPanel();
        JPanel heartRate =  new JPanel();
        JPanel respiratoryRate = new JPanel();
        JPanel bloodPressure = new JPanel();

        patientPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        bodyTemperature.setBorder(BorderFactory.createTitledBorder("Body Temperature"));
        heartRate.setBorder(BorderFactory.createTitledBorder("Heart Rate"));
        respiratoryRate.setBorder(BorderFactory.createTitledBorder("Respiratory Rate"));
        bloodPressure.setBorder(BorderFactory.createTitledBorder("Blood Pressure"));
        ECGPanel.setBorder(BorderFactory.createTitledBorder("ECG"));

        vitalSignsPanel.setLayout(new GridLayout(2,2));
        vitalSignsPanel.add(bodyTemperature);
        vitalSignsPanel.add(heartRate);
        vitalSignsPanel.add(respiratoryRate);
        vitalSignsPanel.add(bloodPressure);

        frame.setVisible(true);
    }
}