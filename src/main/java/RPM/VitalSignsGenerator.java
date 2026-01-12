package RPM;

import java.util.Random;

public class VitalSignsGenerator {

    private static final Random random = new Random();

    private final int abnormal; // 0 = normal, 1 = abnormal

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

    private int stepHR;
    private int stepRR;
    private double stepTemp;
    private int stepSyst;
    private int stepDia;

    public VitalSignsGenerator(int abnormal) {
        this.abnormal = abnormal;

        stepHR = 1;
        stepRR = 1;
        stepTemp = 0.25;
        stepSyst = 3;
        stepDia  = 3;

        if (this.abnormal == 1) {
            maxHR = 130; minHR = 35;
            maxRR = 27;  minRR = 7;
            maxTemp = 40; minTemp = 34;
            maxSyst = 160; minSyst = 80;
        } else {
            maxHR = 100; minHR = 42;
            maxRR = 23;  minRR = 10;
            maxTemp = 39; minTemp = 35;
            maxSyst = 135; minSyst = 95;
        }

        bodyTemperature = randomDouble(minTemp, maxTemp);
        heartRate       = randomInt(minHR, maxHR);
        respiratoryRate = randomInt(minRR, maxRR);
        systolic        = randomInt(minSyst, maxSyst);

        diastolic = (systolic - 30) + randomInt(-stepDia, stepDia);
        diastolic = clamp(diastolic, 40, systolic - 10);

        ecg = 0.0;
    }

    public double generateHeartRate() {
        heartRate = reflectStepInt(heartRate, minHR, maxHR, stepHR);
        return heartRate;
    }

    public double generateRespiratoryRate() {
        respiratoryRate = reflectStepInt(respiratoryRate, minRR, maxRR, stepRR);
        return respiratoryRate;
    }

    public double generateBodyTemperature() {
        bodyTemperature = reflectStepDouble(bodyTemperature, minTemp, maxTemp, stepTemp);
        return bodyTemperature;
    }

    public double generateSystolic() {
        systolic = reflectStepInt(systolic, minSyst, maxSyst, stepSyst);
        return systolic;
    }

    public double generateDiastolic() {
        diastolic = (systolic - 30) + randomInt(-stepDia, stepDia);
        diastolic = clamp(diastolic, 40, systolic - 10);
        return diastolic;
    }

    public double generateECG() {
        ecg = randomDouble(-1.0, 1.0);
        return ecg;
    }

    private static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private static double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private static double clamp(double v, double min, double max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    private static double reflectStepInt(double current, int min, int max, int step) {
        int delta = randomInt(-step, step);
        double next = current + delta;

        if (next < min) next = min + (min - next);
        if (next > max) next = max - (next - max);

        return clamp(next, min, max);
    }

    private static double reflectStepDouble(double current, double min, double max, double step) {
        double delta = randomDouble(-step, step);
        double next = current + delta;

        if (next < min) next = min + (min - next);
        if (next > max) next = max - (next - max);

        return clamp(next, min, max);
    }
}
