package lol.ethane.utils.misc;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import lol.ethane.Ethane;
import lombok.Generated;

public final class SoundUtil {
   public static void play(String name) {
      try {
         Clip clip = AudioSystem.getClip();
         InputStream is = Ethane.class.getClassLoader().getResourceAsStream("assets/ethane/sounds/" + name + ".wav");

         try {
            if (is == null) {
               throw new RuntimeException("Sound not found!");
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            clip.open(ais);
            clip.start();
         } catch (Throwable var6) {
            if (is != null) {
               try {
                  is.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (is != null) {
            is.close();
         }

      } catch (Exception var7) {
         throw new RuntimeException(var7);
      }
   }

   @Generated
   private SoundUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
