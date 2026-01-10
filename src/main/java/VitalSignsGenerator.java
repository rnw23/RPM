import java.util.Random;

public class VitalSignsGenerator {

    private static final Random random = new Random();

    /**
     * Generates a random heart rate in beats per minute (bpm)
     * Typical adult resting range: 60–100 bpm
     */
    public static int generateHeartRate() {
        return randomIntInRange(30, 120);
    }

    /** * Generates a random systolic and diastolic value
     * Typical systolic pressure range: 100-140
     * Typical diastolic pressure range: 70-90
     */
    public static int generateSystolic() {
        return randomIntInRange(80, 160);
    }
    public static int generateDiastolic() {
        return randomIntInRange(50, 100);
    }

    /**
     * Generates a random ECG value (simulated voltage in millivolts)
     * Typical ECG signal range: -1.0 to +1.0 mV
     */
    public static double generateECG() {
        return randomDoubleInRange(-1.0, 1.0);
    }

    /**
     * Generates a random respiratory rate (breaths per minute)
     * Typical adult resting range: 12–20 breaths/min
     */
    public static int generateRespiratoryRate() {
        return randomIntInRange(7, 30);
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
