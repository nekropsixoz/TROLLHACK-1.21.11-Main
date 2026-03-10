package lol.ethane.utils.misc;

import lombok.Generated;

public class Stopwatch {
   public long lastMS = System.currentTimeMillis();

   public boolean elapsed(long time, boolean reset) {
      if (System.currentTimeMillis() - this.lastMS > time) {
         if (reset) {
            this.reset();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean elapsed(long time) {
      return this.elapsed(time, false);
   }

   public void reset() {
      this.lastMS = System.currentTimeMillis();
   }

   public long elapsedTime() {
      return System.currentTimeMillis() - this.lastMS;
   }

   @Generated
   public long getLastMS() {
      return this.lastMS;
   }

   @Generated
   public void setLastMS(long lastMS) {
      this.lastMS = lastMS;
   }
}
