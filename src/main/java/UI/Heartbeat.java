package UI;

import javax.sound.sampled.*;

public class Heartbeat {

    public static void playThump(double frequency, int durationMs) {
        try {
            float sampleRate = 44100;
            byte[] buf = new byte[durationMs * (int)(sampleRate / 1000)];
            for (int i = 0; i < buf.length; i++) {
                double angle = 2.0 * Math.PI * i * frequency / sampleRate;
                buf[i] = (byte)(Math.sin(angle) * 127);
            }

            AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, true);
            try (SourceDataLine sdl = AudioSystem.getSourceDataLine(af)) {
                sdl.open(af);
                sdl.start();
                sdl.write(buf, 0, buf.length);
                sdl.drain();
                sdl.stop();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

