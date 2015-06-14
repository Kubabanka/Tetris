package tetris;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * Created by Jakub Bańka and Tomasz Duda on 2015-06-14.
 */

/**
 * Klasa obsługująca odtwarzanie efektów dźwiękowych podczas grania.
 */
public class Sound {
    public static synchronized void play(final String fileName)
    {
        // Note: use .wav files
        new Thread(new Runnable() {
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(fileName));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.out.println("Błąd odtwarzania " + e.getMessage() + " pliku: " + fileName);
                }
            }
        }).start();
    }
}
