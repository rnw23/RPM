package RPM;

import java.util.Random;

public class VitalSignsGenerator {
    private int abnormal;
    private static final Random random = new Random();

    private double HeartRate;
    private double RespiratoryRate;
    private double BodyTemperature;
    private double Systolic;
    private double Diastolic;
    private double ECG;

    private int maxHR;
    private int minHR;
    private int maxRR;
    private int minRR;
    private int maxTemp;
    private int minTemp;
    private int maxSyst;
    private int minSyst;


    private int intervalHR;
    private int intervalRR;
    private double intervalTemp;
    private int intervalSyst;
    private int intervalDia;

    public VitalSignsGenerator(int abnormal){
        //constructor
        this.abnormal = abnormal;
        //defining bounds based on normal/abnormal patient
        if (this.abnormal == 1){
            this.maxHR=130;
            this.minHR=35;
            this.maxRR=27;
            this.minRR=7;
            this.maxTemp=40;
            this.minTemp=34;
            this.maxSyst=160;
            this.minSyst=80;

        }
        else {
            //defining bounds based on normal patient
            this.maxHR=100;
            this.minHR=42;
            this.maxRR=23;
            this.minRR=10;
            this.maxTemp=39;
            this.minTemp=35;
            this.maxSyst=135;
            this.minSyst=95;
        }

        this.BodyTemperature = randomDoubleInRange(minTemp, maxTemp);
        this.HeartRate=(double)randomIntInRange(minHR, maxHR);
        this.RespiratoryRate=(double)randomIntInRange(minRR, maxRR);
        this.Systolic=(double)randomIntInRange(minSyst, maxSyst);
        this.Diastolic=(double)randomIntInRange(((int)this.Systolic-30-intervalDia), ((int)this.Systolic-30+intervalDia));
        this.ECG=0.0;


        intervalHR=1;
        intervalRR=1;
        intervalTemp=0.25;
        intervalSyst=3;
        intervalDia=3;
    }

    public double generateHeartRate() {
        int deltaHR = randomIntInRange(-intervalHR, intervalHR);
        int newHR = (int)this.HeartRate + deltaHR;
        if (newHR < this.minHR) {
            newHR = this.minHR + (this.minHR - newHR);
        } else if (newHR > this.maxHR) {
            newHR = this.maxHR - (newHR - this.maxHR);
        }
        this.HeartRate = (double) newHR;
        return this.HeartRate;
    }

    public double generateSystolic() {
        int deltaSyst = randomIntInRange(-this.intervalSyst, this.intervalSyst);
        int newSyst = (int)this.Systolic + deltaSyst;
        if (newSyst < this.minSyst) {
            newSyst = this.minSyst + this.intervalSyst;
        } else if (newSyst > this.maxSyst) {
            newSyst = this.maxSyst -  this.intervalSyst;
        }
        this.HeartRate = (double) newSyst;
        return this.HeartRate;

    }

    public double generateDiastolic() {
        int deltaDia = randomIntInRange(-this.intervalDia, this.intervalDia);
        this.Diastolic = (double) this.Systolic -30 + deltaDia;
        return this.Diastolic;
    }
    public double generateRespiratoryRate() {
        int deltaRR = randomIntInRange(-this.intervalRR, this.intervalRR);
        int newRR = (int)this.RespiratoryRate + deltaRR;
        if (newRR < this.minRR) {
            newRR = this.minRR + (this.minRR - newRR);
        } else if (newRR > this.maxRR) {
            newRR = this.maxRR - (newRR - this.maxRR);
        }
        this.RespiratoryRate = (double) newRR;
        return this.RespiratoryRate;
    }
    public double generateBodyTemperature() {
        double deltaTemp = randomDoubleInRange(-this.intervalTemp, this.intervalTemp);
        double newTemp = (int)this.BodyTemperature + deltaTemp;
        if (newTemp < this.minTemp){
            newTemp = this.minTemp + this.intervalTemp;
        }
        else if (newTemp > this.maxTemp) {
            newTemp = this.maxTemp -  this.intervalTemp;
        }
        this.RespiratoryRate = (double) newTemp;
        return this.RespiratoryRate;
    }
    public double generateECG() {
        this.ECG = (double)randomDoubleInRange(-1.0, 1.0);
        return this.ECG;
    }
    /* ---------- Helper Methods ---------- */

    private static int randomIntInRange(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private static double randomDoubleInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

}
