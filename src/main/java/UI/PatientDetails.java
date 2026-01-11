package UI;

import RPM.Patient;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PatientDetails extends JPanel {
    private Patient patient;
    private JPanel leftPanel;
    private JPanel centerPanel;
    private JPanel rightPanel;
    private JLabel iconLabel;
    private JLabel name;
    private JLabel id;
    private JLabel age;
    private JLabel location;
    private JLabel contact;

    public PatientDetails(Patient patient) {
        this.patient = patient;
        PatientUI();
    }

    public void PatientUI() {
        setLayout(new BorderLayout(30,0));
        leftPanel = new JPanel();
        centerPanel = new JPanel();
        rightPanel = new JPanel();


        //Left Panel
        try {
            URL url = new URL("https://cdn-icons-png.flaticon.com/512/149/149071.png");
            ImageIcon icon = new ImageIcon(url);
            Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            iconLabel = new JLabel(new ImageIcon(scaled));
        }
        catch (Exception e) {
            iconLabel = new JLabel("👤"); // fallback if image fails
        }

        leftPanel.add(iconLabel);

        //Centre Panel
        name = new JLabel("Name: "+ patient.getName());
        id = new JLabel("Patient ID: " + patient.getId());
        age = new JLabel("Age: " + patient.getAge());

        centerPanel.add(name);
        centerPanel.add(id);
        centerPanel.add(age);

        //Right Panel
        location = new JLabel("Location: " + patient.getLocation());
        contact = new JLabel("Contact: " + patient.getContact());

        rightPanel.add(location);
        rightPanel.add(contact);

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));   // right padding
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); // right padding
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

    }

}
