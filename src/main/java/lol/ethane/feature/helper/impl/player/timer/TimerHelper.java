package lol.ethane.feature.helper.impl.player.timer;

import lombok.Generated;

public final class TimerHelper {
   private float timer = 1.0F;
   private static TimerHelper instance;

   private TimerHelper() {
   }

   public float get() {
      return this.timer;
   }

   public void set(float value) {
      this.timer = value;
   }

   public static void setInstance() {
      instance = new TimerHelper();
   }

   @Generated
   public static TimerHelper getInstance() {
      return instance;
   }
}
