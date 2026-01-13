package RPM;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VitalSignsGeneratorAbnormalTest {
    VitalSignsGenerator generator = new VitalSignsGenerator(1);

    //since this is a random generator, tests don't measure against predetermined values
    //tests check if conditions are being met like increment control,
    //setting parameters here so they can be modified later

    //these are the bounds for the abnormal condition
    int maxHR = 130; int minHR = 35;
    int maxRR = 27;  int minRR = 7;
    int maxTemp = 40; int minTemp = 34;
    int maxSyst = 160; int minSyst = 80;

    int  intervalHR = 1;
    int intervalRR = 1;
    double intervalTemp = 0.25;  //(smaller interval since it changes more slowly
    int intervalSyst = 3;
    int intervalDia = 3;

    //all tests are run 100 times to ensure they are valid for various randomised scenarios

    //tests for heart rates
    @Test
    void heartRateStaysWithinBounds() {
        for (int i = 0; i < 100; i++) {
            double hr = generator.generateHeartRate();
            assertTrue(hr >= minHR && hr <= maxHR,
                    "Heart rate out of bounds: " + hr);
        }
    }

    @Test
    void heartRateChangesByAtMostInterval() { //checking how much it is changing per interval
        for (int i = 0; i < 100; i++) {
            double previous = generator.generateHeartRate();
            double current = generator.generateHeartRate();

            double difference = Math.abs(current - previous); //absolute value because can change either way
            assertTrue(difference <= intervalHR,
                    "Heart rate jumped too much: " + difference);
        }
    }

    @Test
    void heartRateUpdatesOverTime() {
        int timesDifferent = 0;
        for  (int i = 0; i < 100; i++) {
            double first = generator.generateHeartRate();
            double second = generator.generateHeartRate();
            for (int j = 0; j < 10; j++) {
                second = generator.generateHeartRate();  //running the generator to check changes multiple times because the increment is only one so it might not change at every single instance
            }
            if (!(first == second)) {  //checking if heart Rate is updating
                timesDifferent++;
            }
        }

        // Not always guaranteed to be different since it can return to the same value, but very likely over multiple runs so set a threshold for 75%
        assertTrue(timesDifferent > 75,
                "Heart rate did not update over time");
    }

    //tests for respiratory rates (same logic)
    @Test
    void respRateStaysWithinBounds() {
        for (int i = 0; i < 100; i++) { //checking over multiple iterations
            double rr = generator.generateRespiratoryRate();
            assertTrue(rr >= minRR && rr <= maxRR, //checking if respiratory rate is within bounds
                    "Respiratory rate out of bounds: " + rr);
        }
    }

    @Test
    void respRateChangesByAtMostInterval() { //checking how much it is changing per interval
        for (int i = 0; i < 100; i++) {
            double previous = generator.generateRespiratoryRate();
            double current = generator.generateRespiratoryRate();

            double difference = Math.abs(current - previous); //absolute value because can change either way
            assertTrue(difference <= intervalRR,
                    "Respiratory rate jumped too much: " + difference);
        }
    }

    @Test
    void respRateUpdatesOverTime() {
        int timesDifferent = 0;
        for  (int i = 0; i < 100; i++) {
            double first = generator.generateRespiratoryRate();
            double second = generator.generateRespiratoryRate();
            for (int j = 0; j < 10; j++) {
                second = generator.generateRespiratoryRate();  //running the generator to check changes multiple times because the increment is only one so it might not change at every single instance
            }
            if (!(first == second)) {  //checking if heart Rate is updating
                timesDifferent++;
            }
        }


        // Not guaranteed to be different, but very likely over multiple runs so set a threshold for 75%
        assertTrue(timesDifferent > 75,
                "Respiratory rate did not update over time");
    }

    //tests for ECG
    @Test
    void ECGStaysWithinBounds() {
        for (int i = 0; i < 100; i++) {
            double ecg = generator.generateECG();
            assertTrue(ecg >= -1 && ecg <= 1,
                    "ECG out of bounds: " + ecg);
        }
    }
    //no interval test for ECG because it is randomly generated to be anywhere within the range/bounds

    @Test
    void ECGUpdatesOverTime() {
        int timesDifferent = 0;
        for  (int i = 0; i < 100; i++) {
            double first = generator.generateECG();
            double second = generator.generateECG();
            if (!(first == second)) {  //checking if ECG is updating
                timesDifferent++;
            }
        }

        // Not guaranteed to be different, but very likely over multiple runs so set a threshold for 75%
        assertTrue(timesDifferent > 75,
                "ECG did not update over time");
    }

    //tests for systolic blood pressure (same logic as HR, RR)
    @Test
    void SysBPStaysWithinBounds() {
        for (int i = 0; i < 100; i++) {
            double sysBP = generator.generateSystolic();
            assertTrue(sysBP >= minSyst && sysBP <= maxSyst,
                    "Systolic BP out of bounds: " + sysBP);
        }
    }

    @Test
    void SysBPChangesByAtMostInterval() { //checking how much it is changing per interval
        for (int i = 0; i < 100; i++) {
            double previous = generator.generateSystolic();
            double current = generator.generateSystolic();

            double difference = Math.abs(current - previous); //absolute value because can change either way
            assertTrue(difference <= intervalSyst,
                    "Systolic BP jumped too much: " + difference);
        }
    }

    @Test
    void sysBPUpdatesOverTime() {
        int timesDifferent = 0;
        for  (int i = 0; i < 100; i++) {
            double first = generator.generateSystolic();
            double second = generator.generateSystolic();
            if (!(first == second)) {  //checking if systolic BP is updating
                timesDifferent++;
            }
        }

        // Not guaranteed to be different, but very likely over multiple runs so set a threshold for 75%
        assertTrue(timesDifferent > 75,
                "Systolic BP did not update over time");
    }

    //tests for diastolicBP
    //different logic because diastolic BP doesn't have its own bounds (being generated from syst - 30 with a fluctuation from intervalDia
    @Test
    void diastolicBPStaysWithinBounds() {
        for (int i = 0; i < 100; i++) {
            double diastolic = generator.generateDiastolic();
            assertTrue(diastolic >= (minSyst-30-intervalDia) && diastolic <= (maxSyst-30+intervalDia),
                    "Diastolic BP out of bounds: " + diastolic);
        }
    }

    @Test
    void diastolicBPChangesByAtMostInterval() { //checking how much it is changing per interval
        for (int i = 0; i < 100; i++) {
            double previous = generator.generateDiastolic();
            double current = generator.generateDiastolic();

            double difference = Math.abs(current - previous); //absolute value because can change either way
            assertTrue(difference <= 2*intervalDia,
                    //note that diastolic BP is generated by adding a value to (Systolic-30) not the previous diastolic value
                    // for a particular value of systolic (which is not being regenerated here), diastolic should not be more than 2*interval different.
                    "Diastolic BP jumped too much: " + difference);
        }
    }

    @Test
    void diastolicUpdatesOverTime() {
        int timesDifferent = 0;
        for  (int i = 0; i < 100; i++) {
            double first = generator.generateDiastolic();
            double second = generator.generateDiastolic();
            if (!(first == second)) {  //checking if diastolic BP is updating
                timesDifferent++;
            }
        }

        // Not guaranteed to be different, but very likely over multiple runs so set a threshold for 75%
        assertTrue(timesDifferent > 75,
                "Diastolic BP did not update over time");
    }

    //tests for body temperature (same logic as HR, RR, Systolic BP)
    @Test
    void tempStaysWithinBounds() {
        for (int i = 0; i < 100; i++) { //checking over multiple iterations
            double temp  = generator.generateBodyTemperature();
            assertTrue(temp >= minTemp && temp <= maxTemp, //checking if temperature is within bounds
                    "Temperature out of bounds: " + temp);
        }
    }

    @Test
    void tempChangesByAtMostInterval() { //checking how much it is changing per interval
        for (int i = 0; i < 100; i++) {
            double previous = generator.generateBodyTemperature();
            double current = generator.generateBodyTemperature();

            double difference = Math.abs(current - previous); //absolute value because can change either way
            assertTrue(difference <= intervalTemp,
                    "Temperature jumped too much: " + difference);
        }
    }

    @Test
    void TempUpdatesOverTime() {
        int timesDifferent = 0;
        for  (int i = 0; i < 100; i++) {
            double first = generator.generateBodyTemperature();
            double second = generator.generateBodyTemperature();
            if (!(first == second)) {  //checking if temperature is updating
                timesDifferent++;
            }
        }

        // Not guaranteed to be different, but very likely over multiple runs so set a threshold for 75%
        assertTrue(timesDifferent > 75,
                "Temperature did not update over time");
    }

}
