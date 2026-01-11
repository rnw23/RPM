package RPM;

import java.util.Random;

public class VitalSignsGenerator {

    private static final Random random = new Random();

    /**
     * Generates a random heart rate in beats per minute (bpm)
     * Typical adult resting range: 60–100 bpm
     */
    public static double generateHeartRate() {
        return (double)randomIntInRange(30, 120);
    }

    /** * Generates a random systolic and diastolic value
     * Typical systolic pressure range: 100-140
     * Typical diastolic pressure range: 70-90
     */
    public static double generateSystolic() {
        return (double)randomIntInRange(80, 160);
    }
    public static double generateDiastolic() {
        return (double)randomIntInRange(50, 100);
    }

    /**
     * Generates a random ECG value (simulated voltage in millivolts)
     * Typical ECG signal range: -1.0 to +1.0 mV
     */
    public static double generateECG() {
        return (double)randomDoubleInRange(-1.0, 1.0);
    }
/*
    private static double t = 0.0;
    private static final double FS = 100.0; // samples/sec

    public static double generateECG() {
        double bpm = 75.0;
        double freq = bpm / 60.0;

        double ecg = Math.sin(2 * Math.PI * freq * t);
        double noise = (Math.random() - 0.5) * 0.05;

        t += 1.0 / FS;
        return ecg + noise;
    }

 */

    /**
     * Generates a random respiratory rate (breaths per minute)
     * Typical adult resting range: 12–20 breaths/min
     */
    public static double generateRespiratoryRate() {
        return (double)randomIntInRange(7, 30);
    }

    /**
     * Generates a random body temperature in Celsius
     * Normal range: 36.1–37.2 °C
     */
    public static double generateBodyTemperature() {
        return randomDoubleInRange(34, 40);
    }

    /* ---------- Helper Methods ---------- */

    private static int randomIntInRange(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private static double randomDoubleInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

}
