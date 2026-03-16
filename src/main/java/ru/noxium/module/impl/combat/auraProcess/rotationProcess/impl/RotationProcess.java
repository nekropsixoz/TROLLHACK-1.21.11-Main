package ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import ru.noxium.event.EventInit;
import ru.noxium.event.lifecycle.ClientTickEvent;
import ru.noxium.event.render.EventRender3D;
import ru.noxium.module.impl.combat.auraProcess.auraUtil.GCDUtil;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.Component;
import ru.noxium.util.player.PlayerUtil;
import ru.noxium.util.render.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RotationProcess extends Component {
   public static RotationProcess.RotationTask currentTask = RotationProcess.RotationTask.IDLE;
   public static float currentYawSpeed;
   public static float currentPitchSpeed;
   public static float currentYawReturnSpeed;
   public static float currentPitchReturnSpeed;
   public static int currentPriority;
   public static int currentTimeout;
   public static int idleTicks;
   public static Rotation targetRotation;

   public static boolean isRotating() {
      return !currentTask.equals(RotationProcess.RotationTask.IDLE);
   }

   private void resetRotation() {
      Rotation targetRotation = new Rotation(FreeLookUtil.freeYaw, FreeLookUtil.freePitch);
      if (updateRotation(targetRotation, currentYawReturnSpeed, currentPitchReturnSpeed)) {
         this.stopRotation();
      }
   }

   @EventInit
   public void onEvent(EventRender3D event) {
      if (mc.player != null && isRotating()) {
         mc.player.bodyYaw = PlayerUtil.calculateCorrectYawOffset(mc.player.getYaw());
      }
   }

   @EventInit
   public void onEvent(ClientTickEvent event) {
      if (currentTask.equals(RotationProcess.RotationTask.AIM) && idleTicks > currentTimeout) {
         currentTask = RotationProcess.RotationTask.RESET;
      }

      if (currentTask.equals(RotationProcess.RotationTask.RESET)) {
         this.resetRotation();
      }

      idleTicks++;
   }

   public static void update(
         Rotation target, float yawSpeed, float pitchSpeed, float yawReturnSpeed, float pitchReturnSpeed, int timeout,
         int priority, boolean clientRotation) {
      if (currentPriority <= priority) {
         if (currentTask.equals(RotationProcess.RotationTask.IDLE) && !clientRotation) {
            FreeLookUtil.active = true;
         }

         currentYawSpeed = yawSpeed;
         currentPitchSpeed = pitchSpeed;
         currentYawReturnSpeed = yawReturnSpeed;
         currentPitchReturnSpeed = pitchReturnSpeed;
         currentTimeout = timeout;
         currentPriority = priority;
         currentTask = RotationProcess.RotationTask.AIM;
         targetRotation = target;
         updateRotation(target, yawSpeed, pitchSpeed);
      }
   }

   public static void update(Rotation targetRotation, float turnSpeed, float returnSpeed, int timeout, int priority) {
      update(targetRotation, turnSpeed, turnSpeed, returnSpeed, returnSpeed, timeout, priority, false);
   }

   static boolean updateRotation(Rotation targetRotation, float yawSpeed, float pitchSpeed) {
      if (mc.player == null) {
         return false;
      } else {
         Rotation currentRotation = new Rotation(mc.player);
         float yawDelta = MathHelper.wrapDegrees(targetRotation.yaw - currentRotation.yaw);
         float pitchDelta = targetRotation.pitch - currentRotation.pitch;
         float clampedYaw = Math.min(Math.abs(yawDelta), yawSpeed);
         float clampedPitch = Math.min(Math.abs(pitchDelta), pitchSpeed);
         mc.player
               .setYaw(
                     mc.player.headYaw = mc.player.headYaw
                           + GCDUtil.getSensitivity(MathHelper.clamp(yawDelta, -clampedYaw, clampedYaw)));
         mc.player
               .setPitch(
                     MathHelper.clamp(
                           mc.player.getPitch()
                                 + GCDUtil.getSensitivity(MathHelper.clamp(pitchDelta, -clampedPitch, clampedPitch)),
                           -90.0F, 90.0F));
         idleTicks = 0;
         return new Rotation(mc.player).getDelta(targetRotation) < 1.0F;
      }
   }

   public void stopRotation() {
      currentTask = RotationProcess.RotationTask.IDLE;
      currentPriority = 0;
      FreeLookUtil.active = false;
   }

   @Environment(EnvType.CLIENT)
   public static enum RotationTask {
      AIM,
      RESET,
      IDLE;
   }
}
