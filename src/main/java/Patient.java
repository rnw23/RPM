import AllVitalSigns.*;
import java.util.ArrayList;

public class Patient extends VitalSignsGenerator {
    private int id;
    private String name;
    private int age;
    private ArrayList<HeartRate> HeartRateHistory;
    private ArrayList<BloodPressure> BloodPressureHistory;
    private ArrayList<RespRate> RespRateHistory;
    private ArrayList<Temperature> TemperatureHistory;
    private ArrayList<ECG> ECGHistory;


    public Patient(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.HeartRateHistory = new ArrayList<>();
        this.BloodPressureHistory = new ArrayList<>();
        this.RespRateHistory = new ArrayList<>();
        this.TemperatureHistory = new ArrayList<>();
        this.ECGHistory = new ArrayList<>();
        updateVitals();

    }

    public void updateVitals() {
        HeartRateHistory.add(new HeartRate(generateHeartRate()));
        BloodPressureHistory.add(new BloodPressure(generateSystolic(), generateDiastolic()));
        RespRateHistory.add(new RespRate(generateRespiratoryRate()));
        TemperatureHistory.add(new Temperature(generateBodyTemperature()));
        ECGHistory.add(new ECG(generateECG()));

    }

    /* ----- Getters ----- */
    public int getId () {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() { return age; }

    public HeartRate getHr() { return HeartRateHistory.get(HeartRateHistory.size() - 1); }

    public BloodPressure getBp(){ return BloodPressureHistory.get(BloodPressureHistory.size() - 1); }

    public RespRate getRR(){ return RespRateHistory.get(RespRateHistory.size() - 1); }

    public Temperature getTemp(){ return TemperatureHistory.get(TemperatureHistory.size() - 1); }

    public ECG getECG(){ return ECGHistory.get(ECGHistory.size() - 1); }

    /* ----- Potential Display in the UI ----- */
    public String PatientDisplay() {
        return "Patient\n"
                + "ID: " + getId() + "\n"
                + "Name: " + getName() + "\n"
                + "Age: " + getAge() + "\n"
                + "Heart Rate: " + getHr() + " bpm\n"
                + "Blood Pressure: " + getBp() + "\n"
                + "Resp Rate: " + getRR() + "\n"
                + "Temperature: " + getTemp() + "\n"
                + "ECG: " + getECG();
    }

//                + "Blood Pressure: " + bp.toString() + "\n"
//                + String.format("ECG: %.3f mV%n", ECG)
//                + "Respiratory Rate: " + RR + " breaths/min\n"
//                + String.format("Body Temperature:  %.1f °C%n", temp) + "\n";

    /* ----- Example usage -----

    public static void main(String[] args) throws InterruptedException {
        Patient p = new Patient(1,"John Smith", 35);

        while (true) {
            p.updateVitals();
            System.out.println(p.PatientDisplay());
            Thread.sleep(1000);
        }
    }

     */
}