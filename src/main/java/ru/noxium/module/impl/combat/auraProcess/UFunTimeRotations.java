package ru.noxium.module.impl.combat.auraProcess;

import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import ru.noxium.module.impl.combat.HitAura;
import ru.noxium.module.impl.combat.auraProcess.auraUtil.AuraUtil;
import ru.noxium.module.impl.combat.auraProcess.auraUtil.UBoxPoints;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl.FreeLookUtil;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl.Rotation;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl.RotationProcess;
import ru.noxium.util.other.IMinecraft;
import ru.noxium.util.other.Mathf;

@Environment(EnvType.CLIENT)
public class UFunTimeRotations implements IMinecraft {
   static float tick;
   static float lastAttackPitch = 0.0F;

   public static void rotation(LivingEntity target, boolean isAttack, float attackDistance, boolean check) {
      long currentTime = System.currentTimeMillis();
      if (!HitAura.isLookingUp && currentTime - HitAura.lastLookUpTime >= HitAura.nextLookUpDelay) {
         HitAura.isLookingUp = true;
         HitAura.lookUpStartTime = currentTime;
         HitAura.lookUpDuration = ThreadLocalRandom.current().nextInt(270, 390);
         HitAura.lastLookUpTime = currentTime;
         HitAura.nextLookUpDelay = ThreadLocalRandom.current().nextLong(10500L, 13200L);
      }

      boolean fastspeed = false;
      if (HitAura.isLookingUp && currentTime - HitAura.lookUpStartTime >= HitAura.lookUpDuration) {
         HitAura.isLookingUp = false;
      }

      if (currentTime - HitAura.lookUpStartTime >= HitAura.lookUpDuration + 60L) {
         fastspeed = true;
      }

      Vec3d directionVec = UBoxPoints.getBestVector3dOnEntityBox(target.getBoundingBox()).subtract(mc.player.getEyePos());
      float baseYaw = FreeLookUtil.freeYaw;
      if (isAttack && AuraUtil.getStrictDistance(target) < attackDistance && !check) {
         tick = Mathf.randomValue(6.0F, 7.0F);
      }

      float fov = (float)AuraUtil.calculateFOVFromCamera(target);
      float baseFov = 360.0F;
      float yawChangeSpeed = Mathf.randomValue(22.0F, 29.0F);
      float randomAttackShift = 0.0F;
      float pitchChangeSpeed = Mathf.randomValue(0.0F, 3.5F);
      float waveA = (float)Math.cos(System.currentTimeMillis() / 40.0);
      float waveB = (float)Math.sin(System.currentTimeMillis() / 70.0);
      if (tick > 0.0F && Math.abs(fov) < baseFov) {
         yawChangeSpeed = Mathf.randomValue(90.0F, 120.0F);
         baseYaw = (float)Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
         randomAttackShift = (waveA + waveB) * Rotate.randomLerp(1.0F, 2.0F);
         tick--;
      }

      float basePitch = (float)MathHelper.clamp(
         -Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z))), -90.0, 90.0
      );
      float yawJitter = waveA * Rotate.randomLerp(11.0F, 14.0F) + randomAttackShift;
      float pitchJitter = waveB * Rotate.randomLerp(4.0F, 7.0F) + randomAttackShift;
      if (isAttack && AuraUtil.getStrictDistance(target) < attackDistance && !check) {
         lastAttackPitch = basePitch;
      }

      float finalPitch = HitAura.isLookingUp ? -Mathf.randomValue(85.0F, 90.0F) : basePitch;
      Rotation newRotation = new Rotation(baseYaw + yawJitter, finalPitch + pitchJitter);
      RotationProcess.update(
         newRotation,
         yawChangeSpeed,
         HitAura.isLookingUp ? Rotate.randomLerp(120.0F, 170.0F) : (fastspeed ? Rotate.randomLerp(120.0F, 170.0F) : Rotate.randomLerp(6.0F, 8.0F)),
         25.0F,
         25.0F,
         0,
         15,
         false
      );
   }
}
