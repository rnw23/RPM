package UI;

import javax.sound.sampled.*;

/**
 * Generates heartbeat sound according to heartrate
 * Reference 1 - developed with assistance from an AI language model (ChatGPT) to support code structuring,
 * audio signal generation using the Java Sound API and explanation of the audio system.
 */
public class Heartbeat {

    public static void playThump(double frequency, int durationMs) {
        try {

            float sampleRate = 44100; // Standard audio sample rate
            byte[] buf = new byte[durationMs * (int)(sampleRate / 1000)]; // Create audio buffer sized according to duration and sample rate
            // Generate sin wave at the specified frequency
            for (int i = 0; i < buf.length; i++) {
                double angle = 2.0 * Math.PI * i * frequency / sampleRate;
                buf[i] = (byte)(Math.sin(angle) * 127);
            }

            // Define audio format
            AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, true);
            // Obtain and open a line for audio playback
            try (SourceDataLine sdl = AudioSystem.getSourceDataLine(af)) {
                sdl.open(af);
                sdl.start();
                sdl.write(buf, 0, buf.length);   // Write audio data to the line for playback
                // Ensure all queued audio is played before stopping
                sdl.drain();
                sdl.stop();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

