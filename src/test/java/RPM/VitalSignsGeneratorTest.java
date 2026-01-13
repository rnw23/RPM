package RPM;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
class VitalSignsGeneratorAbnormalTest {
        VitalSignsGenerator generator = new VitalSignsGenerator(1);
        @Test
        void heartRateStaysWithinBounds() {
            for (int i = 0; i < 100; i++) {
                double hr = generator.generateHeartRate();
                assertTrue(hr >= 35 && hr <= 130,
                        "Heart rate out of bounds: " + hr);
            }
        }

        @Test
        void heartRateChangesByAtMostInterval() {
            VitalSignsGenerator generator = new VitalSignsGenerator(1);
            for (int i = 0; i < 100; i++) {
                double previous = generator.generateHeartRate();
                double current = generator.generateHeartRate();

                double difference = Math.abs(current - previous);
                assertTrue(difference <= 1.5,
                        "Heart rate jumped too much: " + difference);
            }
        }

        @Test
        void heartRateUpdatesOverTime() {
            VitalSignsGenerator generator = new VitalSignsGenerator(1);
            int timesDifferent = 0;
            for  (int i = 0; i < 100; i++) {
                double first = generator.generateHeartRate();
                double second = generator.generateHeartRate();
                if (!(first == second)) {
                    timesDifferent++;
                }
            }

            // Not guaranteed to be different, but very likely over multiple runs
            assertTrue(timesDifferent > 75,
                    "Heart rate did not update over time");
        }
    }
    