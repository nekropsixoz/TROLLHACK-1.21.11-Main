package lol.ethane.feature.helper.impl.player.rotation.model.impl;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import lol.ethane.feature.helper.impl.player.rotation.model.IRotationModel;
import lombok.Generated;
import net.minecraft.class_241;
import net.minecraft.class_3532;

public class AdvancedRotationModel implements IRotationModel {
   private double gravity = 9.0D;
   private double wind = 3.0D;
   private double maxVelocity = 15.0D;
   private double slowdownRange = 12.0D;
   private double randomStrength = 1.0D;
   private double velocityX;
   private double velocityY;
   private double windX;
   private double windY;
   private double currentMaxVelocity = -1.0D;
   private int startSmoothingTicks;
   private double correlationStrength;
   private double yawInfluenceOnPitch;
   private double yawInfluenceCap;
   private double pitchInfluenceOnYaw;
   private double pitchInfluenceCap;
   private AdvancedRotationModel.AccelerationMode accelerationMode;
   private int accelerationHistorySize;
   private double accelerationInfluence;
   private double lastDeltaYaw;
   private double lastDeltaPitch;
   private int ticksSinceReset;
   private final ArrayDeque<class_241> accelerationHistory;
   private double ramp;
   private final ArrayDeque<Double> yawHistory;

   public AdvancedRotationModel() {
      this.accelerationMode = AdvancedRotationModel.AccelerationMode.HISTORY;
      this.accelerationHistory = new ArrayDeque();
      this.ramp = 0.5D;
      this.yawHistory = new ArrayDeque();
   }

   public void setRamp(double ramp) {
      this.ramp = ramp;
   }

   public void reset() {
      this.velocityX = 0.0D;
      this.velocityY = 0.0D;
      this.windX = 0.0D;
      this.windY = 0.0D;
      this.currentMaxVelocity = -1.0D;
      this.yawHistory.clear();
      this.lastDeltaYaw = 0.0D;
      this.lastDeltaPitch = 0.0D;
      this.ticksSinceReset = 0;
      this.accelerationHistory.clear();
   }

   public class_241 tick(class_241 from, class_241 to, float delta) {
      if (this.currentMaxVelocity == -1.0D) {
         this.currentMaxVelocity = this.maxVelocity;
      }

      double currentYaw = (double)from.field_1343;
      double currentPitch = (double)from.field_1342;
      double targetYaw = (double)to.field_1343;
      double targetPitch = (double)to.field_1342;
      double deltaYaw = class_3532.method_15338(targetYaw - currentYaw);
      double deltaPitch = targetPitch - currentPitch;
      double distance = Math.hypot(deltaYaw, deltaPitch);
      double absYawDelta = Math.abs(deltaYaw);
      this.yawHistory.add(absYawDelta);
      if (this.yawHistory.size() > 10) {
         this.yawHistory.removeFirst();
      }

      double avgYawDelta = 0.0D;
      if (!this.yawHistory.isEmpty()) {
         double d;
         for(Iterator var22 = this.yawHistory.iterator(); var22.hasNext(); avgYawDelta += d) {
            d = (Double)var22.next();
         }

         avgYawDelta /= (double)this.yawHistory.size();
      }

      double randomScale = ThreadLocalRandom.current().nextDouble(0.8D, 1.6D);
      double dynamicMaxVelocity = 3.5D + absYawDelta * 0.4375D + avgYawDelta * randomScale;
      if (this.currentMaxVelocity != -1.0D) {
         this.maxVelocity = dynamicMaxVelocity;
      }

      this.currentMaxVelocity = dynamicMaxVelocity;
      if (distance <= 1.0D) {
         this.velocityX = 0.0D;
         this.velocityY = 0.0D;
         this.windX = 0.0D;
         this.windY = 0.0D;
         this.currentMaxVelocity = this.maxVelocity;
         return to;
      } else {
         ++this.ticksSinceReset;
         double w0 = this.wind * this.randomStrength;
         double windMag = Math.min(w0, distance);
         double sqrt3 = Math.sqrt(3.0D);
         double sqrt5 = Math.sqrt(5.0D);
         if (distance >= this.slowdownRange) {
            this.windX = this.windX / sqrt3 + (2.0D * ThreadLocalRandom.current().nextDouble() - 1.0D) * windMag / sqrt5;
            this.windY = this.windY / sqrt3 + (2.0D * ThreadLocalRandom.current().nextDouble() - 1.0D) * windMag / sqrt5;
         } else {
            this.windX /= sqrt3;
            this.windY /= sqrt3;
         }

         this.velocityX += this.windX + this.gravity * (deltaYaw / distance);
         this.velocityY += this.windY + this.gravity * (deltaPitch / distance);
         double velocityMag = Math.hypot(this.velocityX, this.velocityY);
         double outputIdx;
         if (velocityMag > this.currentMaxVelocity) {
            outputIdx = this.currentMaxVelocity / 2.0D + ThreadLocalRandom.current().nextDouble() * this.currentMaxVelocity / 2.0D;
            this.velocityX = this.velocityX / velocityMag * outputIdx;
            this.velocityY = this.velocityY / velocityMag * outputIdx;
         }

         outputIdx = 0.5D;
         double moveYaw = this.velocityX * outputIdx;
         double movePitch = this.velocityY * outputIdx;
         double yawInfluence = Math.abs(moveYaw) * (this.yawInfluenceOnPitch / 100.0D);
         yawInfluence = Math.min(yawInfluence, this.yawInfluenceCap);
         if (movePitch != 0.0D) {
            movePitch += Math.copySign(yawInfluence, movePitch) * this.correlationStrength;
         } else {
            movePitch += (double)(ThreadLocalRandom.current().nextBoolean() ? 1 : -1) * yawInfluence * this.correlationStrength;
         }

         double pitchInfluence = Math.abs(movePitch) * (this.pitchInfluenceOnYaw / 100.0D);
         pitchInfluence = Math.min(pitchInfluence, this.pitchInfluenceCap);
         if (moveYaw != 0.0D) {
            moveYaw += Math.copySign(pitchInfluence, moveYaw) * this.correlationStrength;
         } else {
            moveYaw += (double)(ThreadLocalRandom.current().nextBoolean() ? 1 : -1) * pitchInfluence * this.correlationStrength;
         }

         double avgYaw;
         if (this.accelerationMode != AdvancedRotationModel.AccelerationMode.OFF) {
            this.accelerationHistory.add(new class_241((float)moveYaw, (float)movePitch));

            while(this.accelerationHistory.size() > this.accelerationHistorySize) {
               this.accelerationHistory.removeFirst();
            }

            if (this.accelerationMode == AdvancedRotationModel.AccelerationMode.HISTORY && !this.accelerationHistory.isEmpty()) {
               avgYaw = 0.0D;
               double avgPitch = 0.0D;

               class_241 v;
               for(Iterator var50 = this.accelerationHistory.iterator(); var50.hasNext(); avgPitch += (double)v.field_1342) {
                  v = (class_241)var50.next();
                  avgYaw += (double)v.field_1343;
               }

               avgYaw /= (double)this.accelerationHistory.size();
               avgPitch /= (double)this.accelerationHistory.size();
               double influence = this.accelerationInfluence / 100.0D;
               moveYaw = moveYaw * (1.0D - influence) + avgYaw * influence;
               movePitch = movePitch * (1.0D - influence) + avgPitch * influence;
            }
         }

         if (this.ticksSinceReset < this.startSmoothingTicks) {
            avgYaw = (double)this.ticksSinceReset / (double)this.startSmoothingTicks;
            moveYaw *= avgYaw;
            movePitch *= avgYaw;
         }

         this.lastDeltaYaw = moveYaw;
         this.lastDeltaPitch = movePitch;
         return new class_241((float)(currentYaw + moveYaw), (float)(currentPitch + movePitch));
      }
   }

   public void setMaxVelocity(double maxVelocity) {
      this.maxVelocity = maxVelocity;
      this.currentMaxVelocity = maxVelocity;
   }

   @Generated
   public double getGravity() {
      return this.gravity;
   }

   @Generated
   public double getWind() {
      return this.wind;
   }

   @Generated
   public double getMaxVelocity() {
      return this.maxVelocity;
   }

   @Generated
   public double getSlowdownRange() {
      return this.slowdownRange;
   }

   @Generated
   public double getRandomStrength() {
      return this.randomStrength;
   }

   @Generated
   public double getVelocityX() {
      return this.velocityX;
   }

   @Generated
   public double getVelocityY() {
      return this.velocityY;
   }

   @Generated
   public double getWindX() {
      return this.windX;
   }

   @Generated
   public double getWindY() {
      return this.windY;
   }

   @Generated
   public double getCurrentMaxVelocity() {
      return this.currentMaxVelocity;
   }

   @Generated
   public int getStartSmoothingTicks() {
      return this.startSmoothingTicks;
   }

   @Generated
   public double getCorrelationStrength() {
      return this.correlationStrength;
   }

   @Generated
   public double getYawInfluenceOnPitch() {
      return this.yawInfluenceOnPitch;
   }

   @Generated
   public double getYawInfluenceCap() {
      return this.yawInfluenceCap;
   }

   @Generated
   public double getPitchInfluenceOnYaw() {
      return this.pitchInfluenceOnYaw;
   }

   @Generated
   public double getPitchInfluenceCap() {
      return this.pitchInfluenceCap;
   }

   @Generated
   public AdvancedRotationModel.AccelerationMode getAccelerationMode() {
      return this.accelerationMode;
   }

   @Generated
   public int getAccelerationHistorySize() {
      return this.accelerationHistorySize;
   }

   @Generated
   public double getAccelerationInfluence() {
      return this.accelerationInfluence;
   }

   @Generated
   public double getLastDeltaYaw() {
      return this.lastDeltaYaw;
   }

   @Generated
   public double getLastDeltaPitch() {
      return this.lastDeltaPitch;
   }

   @Generated
   public int getTicksSinceReset() {
      return this.ticksSinceReset;
   }

   @Generated
   public ArrayDeque<class_241> getAccelerationHistory() {
      return this.accelerationHistory;
   }

   @Generated
   public double getRamp() {
      return this.ramp;
   }

   @Generated
   public ArrayDeque<Double> getYawHistory() {
      return this.yawHistory;
   }

   @Generated
   public void setGravity(double gravity) {
      this.gravity = gravity;
   }

   @Generated
   public void setWind(double wind) {
      this.wind = wind;
   }

   @Generated
   public void setSlowdownRange(double slowdownRange) {
      this.slowdownRange = slowdownRange;
   }

   @Generated
   public void setRandomStrength(double randomStrength) {
      this.randomStrength = randomStrength;
   }

   @Generated
   public void setVelocityX(double velocityX) {
      this.velocityX = velocityX;
   }

   @Generated
   public void setVelocityY(double velocityY) {
      this.velocityY = velocityY;
   }

   @Generated
   public void setWindX(double windX) {
      this.windX = windX;
   }

   @Generated
   public void setWindY(double windY) {
      this.windY = windY;
   }

   @Generated
   public void setCurrentMaxVelocity(double currentMaxVelocity) {
      this.currentMaxVelocity = currentMaxVelocity;
   }

   @Generated
   public void setStartSmoothingTicks(int startSmoothingTicks) {
      this.startSmoothingTicks = startSmoothingTicks;
   }

   @Generated
   public void setCorrelationStrength(double correlationStrength) {
      this.correlationStrength = correlationStrength;
   }

   @Generated
   public void setYawInfluenceOnPitch(double yawInfluenceOnPitch) {
      this.yawInfluenceOnPitch = yawInfluenceOnPitch;
   }

   @Generated
   public void setYawInfluenceCap(double yawInfluenceCap) {
      this.yawInfluenceCap = yawInfluenceCap;
   }

   @Generated
   public void setPitchInfluenceOnYaw(double pitchInfluenceOnYaw) {
      this.pitchInfluenceOnYaw = pitchInfluenceOnYaw;
   }

   @Generated
   public void setPitchInfluenceCap(double pitchInfluenceCap) {
      this.pitchInfluenceCap = pitchInfluenceCap;
   }

   @Generated
   public void setAccelerationMode(AdvancedRotationModel.AccelerationMode accelerationMode) {
      this.accelerationMode = accelerationMode;
   }

   @Generated
   public void setAccelerationHistorySize(int accelerationHistorySize) {
      this.accelerationHistorySize = accelerationHistorySize;
   }

   @Generated
   public void setAccelerationInfluence(double accelerationInfluence) {
      this.accelerationInfluence = accelerationInfluence;
   }

   @Generated
   public void setLastDeltaYaw(double lastDeltaYaw) {
      this.lastDeltaYaw = lastDeltaYaw;
   }

   @Generated
   public void setLastDeltaPitch(double lastDeltaPitch) {
      this.lastDeltaPitch = lastDeltaPitch;
   }

   @Generated
   public void setTicksSinceReset(int ticksSinceReset) {
      this.ticksSinceReset = ticksSinceReset;
   }

   public static enum AccelerationMode {
      OFF("Off"),
      HISTORY("History");

      private final String name;

      private AccelerationMode(String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static AdvancedRotationModel.AccelerationMode[] $values() {
         return new AdvancedRotationModel.AccelerationMode[]{OFF, HISTORY};
      }
   }
}
