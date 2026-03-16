package ru.noxium.module.impl.combat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import ru.noxium.Noxium;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventScreen;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.api.setting.impl.MultiBooleanSetting;
import ru.noxium.module.impl.combat.auraProcess.auraUtil.GCDUtil;
import ru.noxium.module.impl.combat.auraProcess.auraUtil.UBoxPoints;
import ru.noxium.util.other.Mathf;

@IModule(
   name = "Aim Helper",
   description = "Обходил жозкий POLAR/FUNTIME/POKITIME",
   category = Category.Combat,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class AimHelper extends Module {
   public static MultiBooleanSetting targets = new MultiBooleanSetting(
      "Цели", new BooleanSetting("Игроки", true), new BooleanSetting("Голые", true), new BooleanSetting("Мобы", true)
   );
   public static ModeSetting mode = new ModeSetting("Режим наведения", "Model 1", "Model 1", "Model 2", "Model 3");
   public static LivingEntity target;

   public AimHelper() {
      this.addSettings(new Setting[]{mode, targets});
   }

   @EventInit
   public void onRender(EventScreen e) {
      if (mode.is("Model 1")) {
         float addyVacY = 0.4F * (float)Math.cos(System.currentTimeMillis() / 2200.0);
         float addyVacZ = 0.16F * (float)Math.cos(System.currentTimeMillis() / 1250.0);
         float addyVacX = 0.22F * (float)Math.sin(System.currentTimeMillis() / 1700.0);
         Vec3d vec = UBoxPoints.getBestVector3dOnEntityBox(target.getBoundingBox(), false)
            .add(addyVacX, addyVacY, addyVacZ)
            .subtract(mc.player.getEyePos());
         float yaw = (float)Math.toDegrees(Math.atan2(-vec.x, vec.z));
         float pitch = (float)(-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))));
         pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
         float yawDiff = MathHelper.wrapDegrees(yaw - mc.player.getYaw()) + Mathf.randomInt(-3, 3);
         float yawChange = MathHelper.clamp(yawDiff * 0.9F, -56.0F, 56.0F);
         float pitchDiff = pitch - mc.player.getPitch() + Mathf.randomInt(-3, 3);
         float pitchChange = MathHelper.clamp(pitchDiff * 0.9F, -40.0F, 40.0F);
         float gcd = GCDUtil.getGCDValue();
         yawChange -= yawChange % gcd;
         pitchChange -= pitchChange % gcd;
         mc.player.setYaw(mc.player.getYaw() + yawChange * Mathf.randomValue(0.16F, 0.21F) * 0.84F);
         mc.player.setPitch(mc.player.getPitch() + pitchChange * Mathf.randomValue(0.05F, 0.1F) * 0.84F);
      } else if (mode.is("Model 2")) {
         float addyVacY = 0.4F * (float)Math.cos(System.currentTimeMillis() / 2200.0);
         float addyVacZ = 0.16F * (float)Math.cos(System.currentTimeMillis() / 450.0);
         float addyVacX = 0.22F * (float)Math.sin(System.currentTimeMillis() / 300.0);
         Vec3d vec = UBoxPoints.getBestVector3dOnEntityBox(target.getBoundingBox(), false)
            .add(addyVacX, addyVacY, addyVacZ)
            .subtract(mc.player.getEyePos());
         float yaw = (float)Math.toDegrees(Math.atan2(-vec.x, vec.z));
         float yawDiff = MathHelper.wrapDegrees(yaw - mc.player.getYaw()) + Mathf.randomInt(-3, 3);
         float yawChange = MathHelper.clamp(yawDiff * 0.9F, -56.0F, 56.0F);
         float gcd = GCDUtil.getGCDValue();
         yawChange -= yawChange % gcd;
         mc.player.setYaw(mc.player.getYaw() + yawChange * Mathf.randomValue(0.12F, 0.15F) * 0.84F);
      } else if (mode.is("Model 3")) {
         float addyVacY = 0.4F * (float)Math.cos(System.currentTimeMillis() / 2200.0);
         float addyVacZ = 0.16F * (float)Math.cos(System.currentTimeMillis() / 1250.0);
         float addyVacX = 0.22F * (float)Math.sin(System.currentTimeMillis() / 1700.0);
         Vec3d vec = UBoxPoints.getBestVector3dOnEntityBox(target.getBoundingBox(), false)
            .add(addyVacX, target.getHeight() - 1.5F, addyVacZ)
            .subtract(mc.player.getEyePos());
         float yaw = (float)Math.toDegrees(Math.atan2(-vec.x, vec.z));
         float pitch = (float)(-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))));
         pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
         float yawDiff = MathHelper.wrapDegrees(yaw - mc.player.getYaw()) + Mathf.randomValue(-4.0F, 6.0F);
         float yawChange = MathHelper.clamp(yawDiff * 0.9F, -56.0F, 56.0F);
         float pitchDiff = pitch - mc.player.getPitch() + Mathf.randomInt(-3, 6);
         float pitchChange = MathHelper.clamp(pitchDiff * 0.9F, -40.0F, 40.0F);
         float gcd = GCDUtil.getGCDValue();
         yawChange -= yawChange % gcd;
         pitchChange -= pitchChange % gcd;
         mc.player.setYaw(mc.player.getYaw() + yawChange * Mathf.randomValue(0.16F, 0.21F) * 0.84F);
         mc.player.setPitch(mc.player.getPitch() + pitchChange * Mathf.randomValue(0.05F, 0.1F) * 0.84F);
      }
   }

   @EventInit
   public void onUpdate(EventUpdate e) {
      if (!mc.player.isAlive()) {
         this.toggle();
      } else {
         if (target == null || !this.isValidTarget(target)) {
            this.updateTarget();
         }

         if (target == null || mc.player == null || mc.world == null) {
            this.reset();
         }
      }
   }

   private void updateTarget() {
      LivingEntity bestTarget = null;
      double bestAngle = Double.MAX_VALUE;
      Vec3d eyePos = mc.player.getEyePos();
      Vec3d lookVec = mc.player.getRotationVec(1.0F).normalize();

      for (Entity entity : mc.world.getEntities()) {
         if (entity instanceof LivingEntity living && this.isValidTarget(living)) {
            Vec3d targetPos = new Vec3d(living.getX(), living.getY(), living.getZ()).add(0.0, living.getHeight() * 0.5, 0.0);
            Vec3d toTarget = targetPos.subtract(eyePos).normalize();
            double angle = Math.acos(MathHelper.clamp(lookVec.dotProduct(toTarget), -1.0, 1.0));
            if (angle < bestAngle) {
               bestAngle = angle;
               bestTarget = living;
            }
         }
      }

      target = bestTarget;
   }

   private float auraDist() {
      return 5.0F;
   }

   private boolean isValidTarget(LivingEntity entity) {
      if (entity instanceof ClientPlayerEntity) {
         return false;
      } else if (mc.player.distanceTo(entity) > this.auraDist()) {
         return false;
      } else if (!mc.player.canSee(entity)) {
         return false;
      } else if (entity instanceof PlayerEntity p && Noxium.get.friendManager.isFriend(p.getName().getString())) {
         return false;
      } else if (entity instanceof PlayerEntity && !targets.get("Игроки")) {
         return false;
      } else if (entity instanceof PlayerEntity && entity.getArmor() == 0 && !targets.get("Голые")) {
         return false;
      } else if (entity instanceof PlayerEntity && ((PlayerEntity)entity).isCreative()) {
         return false;
      } else {
         return (entity instanceof Monster || entity instanceof SlimeEntity || entity instanceof VillagerEntity || entity instanceof AnimalEntity)
               && !targets.get("Мобы")
            ? false
            : !entity.isInvulnerable() && entity.isAlive() && !(entity instanceof ArmorStandEntity);
      }
   }

   private void reset() {
      target = null;
   }
}
