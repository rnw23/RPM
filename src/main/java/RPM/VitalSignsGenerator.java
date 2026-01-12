package RPM;

import java.util.Random;

public class VitalSignsGenerator {
    private int abnormal;
    private static final Random random = new Random();

    private double heartRate;
    private double respiratoryRate;
    private double bodyTemperature;
    private double systolic;
    private double diastolic;
    private double ecg;

    private int maxHR, minHR;
    private int maxRR, minRR;
    private double maxTemp, minTemp;
    private int maxSyst, minSyst;

    private int intervalHR;
    private int intervalRR;
    private double intervalTemp;
    private int intervalSyst;
    private int intervalDia;

    public VitalSignsGenerator(int abnormal) {
        this.abnormal = abnormal;

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

        // intervals (already improved)
        intervalHR = 1;
        intervalRR = 1;
        intervalTemp = 0.25;
        intervalSyst = 3;
        intervalDia = 3;

        // initial values
        this.bodyTemperature = randomDoubleInRange(minTemp, maxTemp);
        this.heartRate = randomIntInRange(minHR, maxHR);
        this.respiratoryRate = randomIntInRange(minRR, maxRR);
        this.systolic = randomIntInRange(minSyst, maxSyst);
        this.diastolic = (this.systolic - 30) + randomIntInRange(-intervalDia, intervalDia);
        this.ecg = 0.0;
    }

    public double generateHeartRate() {
        int delta = randomIntInRange(-intervalHR, intervalHR);
        int newVal = (int) heartRate + delta;

        if (newVal < minHR) newVal = minHR + (minHR - newVal);
        if (newVal > maxHR) newVal = maxHR - (newVal - maxHR);

        heartRate = newVal;
        return heartRate;
    }

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

    public double generateDiastolic() {
        int delta = randomIntInRange(-intervalDia, intervalDia);
        diastolic = (systolic - 30) + delta;
        return diastolic;
    }

    public double generateECG() {
        ecg = randomDoubleInRange(-1.0, 1.0);
        return ecg;
    }

    private static int randomIntInRange(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private static double randomDoubleInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}