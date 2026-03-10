package lol.ethane.utils.render.animation;

import lol.ethane.utils.misc.Stopwatch;
import lombok.Generated;

public class Animation {
   private final Stopwatch timer = new Stopwatch();
   private Easing easing;
   private long duration;
   private double startPoint;
   private double endPoint;
   private double value;
   private boolean finished;
   public boolean funny;

   public Animation(Easing easing, long duration) {
      this.easing = easing;
      this.duration = duration;
   }

   public void process(double endPoint) {
      if (this.endPoint != endPoint) {
         this.endPoint = endPoint;
         this.reset();
      } else {
         this.finished = this.timer.elapsed(this.duration);
         if (this.finished) {
            this.value = endPoint;
            return;
         }
      }

      double progress = (Double)this.easing.getFunction().apply(this.getProgress());
      this.value = this.startPoint + (endPoint - this.startPoint) * progress;
      if (Math.abs(this.value - endPoint) < 0.05D && this.funny) {
         this.value = endPoint;
         this.finished = true;
      }

   }

   private double getProgress() {
      return (double)(System.currentTimeMillis() - this.timer.getLastMS()) / (double)this.duration;
   }

   public void reset() {
      this.timer.reset();
      this.startPoint = this.value;
      this.finished = false;
   }

   public void restart() {
      this.timer.reset();
      this.startPoint = 0.0D;
      this.finished = false;
   }

   @Generated
   public Stopwatch getTimer() {
      return this.timer;
   }

   @Generated
   public Easing getEasing() {
      return this.easing;
   }

   @Generated
   public long getDuration() {
      return this.duration;
   }

   @Generated
   public double getStartPoint() {
      return this.startPoint;
   }

   @Generated
   public double getEndPoint() {
      return this.endPoint;
   }

   @Generated
   public double getValue() {
      return this.value;
   }

   @Generated
   public boolean isFinished() {
      return this.finished;
   }

   @Generated
   public boolean isFunny() {
      return this.funny;
   }

   @Generated
   public void setEasing(Easing easing) {
      this.easing = easing;
   }

   @Generated
   public void setDuration(long duration) {
      this.duration = duration;
   }

   @Generated
   public void setStartPoint(double startPoint) {
      this.startPoint = startPoint;
   }

   @Generated
   public void setEndPoint(double endPoint) {
      this.endPoint = endPoint;
   }

   @Generated
   public void setValue(double value) {
      this.value = value;
   }

   @Generated
   public void setFinished(boolean finished) {
      this.finished = finished;
   }

   @Generated
   public void setFunny(boolean funny) {
      this.funny = funny;
   }
}
