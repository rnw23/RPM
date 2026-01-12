package RPM;

import java.util.Random;

public class VitalSignsGenerator {
    //predefining all fields to be used later
    private int abnormal; //used to describe if patient's displayed vital signs are relatively normal (0) or abnormal(1)
    private static final Random random = new Random(); //required to randomise values later

    //current values of vital signs, change at every iteration
    private double heartRate;
    private double respiratoryRate;
    private double bodyTemperature;
    private double systolic;
    private double diastolic;
    private double ecg;

    //bounds within which the vital signs are being generated
    private int maxHR, minHR;
    private int maxRR, minRR;
    private double maxTemp, minTemp;
    private int maxSyst, minSyst;
    //no bounds for diastolic because it's generated using systolic - 30 (plus noise)

    //defining how much each vital sign can change in either direction from the baseline/previous value
    private int intervalHR;
    private int intervalRR;
    private double intervalTemp;
    private int intervalSyst;
    private int intervalDia;

    public VitalSignsGenerator(int abnormal) {
        this.abnormal = abnormal;

        //setting bounds for vital signs being generated based on if patient is described as normal or abnormal
        if (this.abnormal == 1) {
            this.maxHR = 130; this.minHR = 35;
            this.maxRR = 27;  this.minRR = 7;
            this.maxTemp = 40; this.minTemp = 34;
            this.maxSyst = 160; this.minSyst = 80;
        } else {
            this.maxHR = 100; this.minHR = 42;
            this.maxRR = 23;  this.minRR = 10;
            this.maxTemp = 39; this.minTemp = 35;
            this.maxSyst = 135; this.minSyst = 95;
        }

        // intervals (minimal because the values are changing every second so changes add up)
        intervalHR = 1;
        intervalRR = 1;
        intervalTemp = 0.25;  //(smaller interval since it changes more slowly
        intervalSyst = 3;
        intervalDia = 3;

        // randomly generating initial values
        this.bodyTemperature = randomDoubleInRange(minTemp, maxTemp);
        this.heartRate = randomIntInRange(minHR, maxHR);
        this.respiratoryRate = randomIntInRange(minRR, maxRR);
        this.systolic = randomIntInRange(minSyst, maxSyst);
        this.diastolic = (this.systolic - 30) + randomIntInRange(-intervalDia, intervalDia);
        this.ecg = 0.0;
    }
    //defining function to generate random integers in a given range, for HR,RR,BP
    private static int randomIntInRange(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    //defining function to generate random doubles for temperature
    private static double randomDoubleInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    //using random functions to generate a heart rate slightly above or below the earlier this.heartrate value
    public double generateHeartRate() {
        int delta = randomIntInRange(-intervalHR, intervalHR);
        int newVal = (int) heartRate + delta;

        if (newVal < minHR) newVal = minHR + (minHR - newVal);
        if (newVal > maxHR) newVal = maxHR - (newVal - maxHR);

        heartRate = newVal;
        return heartRate;
    }

    //using same logic as heart rate for respiratory rate, temperature, systolic blood pressure
    public double generateRespiratoryRate() {
        int delta = randomIntInRange(-intervalRR, intervalRR);
        int newVal = (int) respiratoryRate + delta;

        if (newVal < minRR) newVal = minRR + (minRR - newVal);
        if (newVal > maxRR) newVal = maxRR - (newVal - maxRR);

        respiratoryRate = newVal;
        return respiratoryRate;
    }

    public double generateBodyTemperature() {
        double delta = randomDoubleInRange(-intervalTemp, intervalTemp);
        double newVal = bodyTemperature + delta;

        if (newVal < minTemp) newVal = minTemp + (minTemp - newVal);
        if (newVal > maxTemp) newVal = maxTemp - (newVal - maxTemp);

        bodyTemperature = newVal;
        return bodyTemperature;
    }

    public double generateSystolic() {
        int delta = randomIntInRange(-intervalSyst, intervalSyst);
        int newVal = (int) systolic + delta;

        if (newVal < minSyst) newVal = minSyst + (minSyst - newVal);
        if (newVal > maxSyst) newVal = maxSyst - (newVal - maxSyst);

        systolic = newVal;
        return systolic;
    }
    //generating diastolic values within interval using systolic-30 as baseline, avoiding the need for applying bounds and diastolic<systolic logics
    public double generateDiastolic() {
        int delta = randomIntInRange(-intervalDia, intervalDia);
        diastolic = (systolic - 30) + delta;
        return diastolic;
    }
    //generating random values as a primitive proof of concept for displaying ecg values
    public double generateECG() {
        ecg = randomDoubleInRange(-1.0, 1.0);
        return ecg;
    }


}