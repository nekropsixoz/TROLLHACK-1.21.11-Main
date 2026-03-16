package ru.noxium.module.impl.movement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.ModeSetting;
import ru.noxium.module.impl.combat.HitAura;
import ru.noxium.util.player.PlayerUtil;

@IModule(
   name = "Speed",
   description = "", 
   category = Category.Movement,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class Speed extends Module {
   public static ModeSetting mode = new ModeSetting("Режим", "Ares-Entity", "Ares-Entity", "Grim-Entity", "Meta-HvH", "NCP");

   public Speed() {
      this.addSettings(new Setting[]{mode});
   }

   @EventInit
   public void onUpdate(EventUpdate e) {
      if (!PlayerUtil.nullCheck()) {
         if (mode.is("Grim-Entity")) {
            if (mc.player == null || mc.world == null) {
               return;
            }

            double finalSpeed = 6.0E-4F;
            if (finalSpeed <= 0.0) {
               return;
            }

            Entity nearest = null;
            double bestSq = Double.MAX_VALUE;
            double maxRangeSq = 0.2F;

            for (Entity ent : mc.world.getEntities()) {
               if (ent != mc.player) {
                  if (ent == HitAura.target) {
                     double dx = ent.getX() - mc.player.getX();
                     double dz = ent.getZ() - mc.player.getZ();
                     double sq = dx * dx + dz * dz;
                     if (sq <= maxRangeSq && sq < bestSq) {
                        bestSq = sq;
                        nearest = ent;
                     }
                  }

                  if (nearest != null) {
                     double[] dir = this.getDirectionToPoint(new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ()), new Vec3d(nearest.getX(), nearest.getY(), nearest.getZ()), finalSpeed);
                     mc.player.addVelocity(dir[0], 0.0, dir[1]);
                  }
               }
            }
         } else if (mode.is("Meta-HvH")) {
            metaHvHSpeed();
         } else if (mode.is("NCP")) {
            tickNCP();
         }
      }
   }

   private static final float NCP_BASE_SPEED = 0.36F;
   private static final float META_HVH_BASE_SPEED = 0.4875F;

   private void tickNCP() {
      if (mc.player == null || mc.world == null) {
         return;
      }

      ItemStack offhandItem = mc.player.getOffHandStack();
      StatusEffectInstance speedEffect = mc.player.getStatusEffect(StatusEffects.SPEED);
      StatusEffectInstance slowEffect = mc.player.getStatusEffect(StatusEffects.SLOWNESS);
      String itemName = offhandItem.isEmpty() ? "" : offhandItem.getName().getString();

      float appliedSpeed;
      if (speedEffect != null) {
         int amplifier = speedEffect.getAmplifier();
         if (amplifier >= 2) {
            appliedSpeed = NCP_BASE_SPEED * 1.155F;
         } else if (amplifier >= 1) {
            appliedSpeed = NCP_BASE_SPEED;
         } else {
            appliedSpeed = NCP_BASE_SPEED * 0.85F;
         }
      } else {
         appliedSpeed = NCP_BASE_SPEED * 0.68F;
      }

      if (!itemName.isEmpty() && itemName.contains("Ломтик Дыни")) {
         appliedSpeed = speedEffect != null && speedEffect.getAmplifier() >= 2 ? 0.41755F : 0.41755F * 0.52F;
      }

      if (slowEffect != null) {
         appliedSpeed *= 0.835F;
      }

      if (!mc.player.isOnGround()) {
         appliedSpeed *= 1.435F;
      }

      setSpeed(appliedSpeed);
   }

   private void setSpeed(float speed) {
      float yaw = mc.player.getYaw();
      double motionX = -Math.sin(Math.toRadians(yaw)) * speed;
      double motionZ = Math.cos(Math.toRadians(yaw)) * speed;

      mc.player.setVelocity(motionX, mc.player.getVelocity().y, motionZ);
   }

   private void metaHvHSpeed() {
      if (mc.player == null) return;

      ItemStack offhandItem = mc.player.getOffHandStack();
      StatusEffectInstance speedEffect = mc.player.getStatusEffect(StatusEffects.SPEED);
      StatusEffectInstance slowEffect = mc.player.getStatusEffect(StatusEffects.SLOWNESS);
      String itemName = offhandItem.isEmpty() ? "" : offhandItem.getName().getString();

      float appliedSpeed = calculateBaseSpeed(speedEffect);
      
      appliedSpeed = applyMelonSliceModifier(itemName, speedEffect, appliedSpeed);
      appliedSpeed = applySlownessModifier(slowEffect, appliedSpeed);
      appliedSpeed = applyAirModifier(appliedSpeed);

      setSpeed(appliedSpeed);
   }

   private float calculateBaseSpeed(StatusEffectInstance speedEffect) {
      if (speedEffect != null) {
         int amplifier = speedEffect.getAmplifier();
         if (amplifier >= 2) {
            return META_HVH_BASE_SPEED * 1.155F;
         } else if (amplifier >= 1) {
            return META_HVH_BASE_SPEED;
         } else {
            return META_HVH_BASE_SPEED * 0.85F;
         }
      }
      return META_HVH_BASE_SPEED * 0.68F;
   }

   private float applyMelonSliceModifier(String itemName, StatusEffectInstance speedEffect, float currentSpeed) {
      if (!itemName.isEmpty() && itemName.contains("Ломтик Дыни")) {
         return speedEffect != null && speedEffect.getAmplifier() >= 2 ? 0.41755F : 0.41755F * 0.52F;
      }
      return currentSpeed;
   }

   private float applySlownessModifier(StatusEffectInstance slowEffect, float currentSpeed) {
      if (slowEffect != null) {
         return currentSpeed * 0.835F;
      }
      return currentSpeed;
   }

   private float applyAirModifier(float currentSpeed) {
      if (!mc.player.isOnGround()) {
         return currentSpeed * 1.435F;
      }
      return currentSpeed;
   }

   private double[] getDirectionToPoint(Vec3d from, Vec3d to, double spd) {
      double dx = to.x - from.x;
      double dz = to.z - from.z;
      double len = Math.sqrt(dx * dx + dz * dz);
      return len == 0.0 ? new double[]{0.0, 0.0} : new double[]{dx / len * spd, dz / len * spd};
   }
}
