package RPM;

import AllVitalSigns.*;
import java.util.ArrayList;

public class Patient {
    private VitalSignsGenerator generator;
    private int id;
    private String name;
    private int age;
    private String location;
    private String contact;
    private int status;

    private ArrayList<HeartRate> HeartRateHistory;
    private ArrayList<BloodPressure> BloodPressureHistory;
    private ArrayList<RespRate> RespRateHistory;
    private ArrayList<Temperature> TemperatureHistory;
    private ArrayList<ECG> ECGHistory;

    public Patient(int id, String name, int age, String location, String contact, int status) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.location = location;
        this.contact = contact;
        this.status = status;

        this.HeartRateHistory = new ArrayList<>();
        this.BloodPressureHistory = new ArrayList<>();
        this.RespRateHistory = new ArrayList<>();
        this.TemperatureHistory = new ArrayList<>();
        this.ECGHistory = new ArrayList<>();

        this.generator = new VitalSignsGenerator(status);
        updateVitals();
    }

    public void updateVitals() {
        HeartRateHistory.add(new HeartRate(generator.generateHeartRate()));
        double sys = generator.generateSystolic();
        double dia = generator.generateDiastolic();
        BloodPressureHistory.add(new BloodPressure(sys, dia));
        RespRateHistory.add(new RespRate(generator.generateRespiratoryRate()));
        TemperatureHistory.add(new Temperature(generator.generateBodyTemperature()));
        ECGHistory.add(new ECG(generator.generateECG()));
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getLocation() { return location; }
    public String getContact() { return contact; }
    public int getStatus() { return status; }

    public HeartRate getHr() { return HeartRateHistory.get(HeartRateHistory.size() - 1); }
    public BloodPressure getBp() { return BloodPressureHistory.get(BloodPressureHistory.size() - 1); }
    public RespRate getRR() { return RespRateHistory.get(RespRateHistory.size() - 1); }
    public Temperature getTemp() { return TemperatureHistory.get(TemperatureHistory.size() - 1); }
    public ECG getECG() { return ECGHistory.get(ECGHistory.size() - 1); }

    public ArrayList<ECG> getECGHistory() { return ECGHistory; }
    public ArrayList<BloodPressure> getBloodPressureHistory() { return BloodPressureHistory; }
    public ArrayList<HeartRate> getHeartRateHistory() { return HeartRateHistory; }
    public ArrayList<RespRate> getRespRateHistory() { return RespRateHistory; }
    public ArrayList<Temperature> getTemperatureHistory() { return TemperatureHistory; }

    private <T> ArrayList<T> genArray(ArrayList<T> history, int sec) {
        ArrayList<T> result = new ArrayList<>();
        if (history == null || history.isEmpty() || sec <= 0) return result;

        int size = history.size();
        int startIndex = Math.max(0, size - sec);
        for (int i = startIndex; i < size; i++) result.add(history.get(i));
        return result;
    }

    public ArrayList<HeartRate> getHrArr(int sec) { return genArray(HeartRateHistory, sec); }
    public ArrayList<BloodPressure> getBpArr(int sec) { return genArray(BloodPressureHistory, sec); }
    public ArrayList<RespRate> getRrArr(int sec) { return genArray(RespRateHistory, sec); }
    public ArrayList<Temperature> getTempArr(int sec) { return genArray(TemperatureHistory, sec); }
    public ArrayList<ECG> getECGArr(int sec) { return genArray(ECGHistory, sec); }

    public String PatientDisplay() {
        return "RPM.Patient\n"
                + "ID: " + getId() + "\n"
                + "Name: " + getName() + "\n"
                + "Age: " + getAge() + " years old\n"
                + "Heart Rate: " + getHr().getValue() + " bpm\n"
                + "Blood Pressure: " + getBp().getSystole() + "/" + getBp().getDiastole() + "\n"
                + "Resp Rate: " + getRR().getValue() + " breaths/min\n"
                + "Temperature: " + String.format("%.2f", getTemp().getValue()) + " °C\n"
                + "ECG: " + String.format("%.2f", getECG().getVoltage()) + "\n";
    }
}
