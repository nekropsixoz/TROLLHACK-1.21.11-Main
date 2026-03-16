package ru.noxium.module.impl.combat.auraProcess.auraUtil;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import net.minecraft.util.hit.EntityHitResult;
import org.joml.Vector2f;
import org.joml.Vector4f;
import ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl.Rotation;
import ru.noxium.util.other.IMinecraft;
import ru.noxium.util.render.math.MathHelper;

@Environment(EnvType.CLIENT)
public final class AuraUtil implements IMinecraft {
   public static double getStrictDistance(Entity entity) {
      return getClosestVec(entity).length();
   }

   public static Rotation cameraAngle() {
      return new Rotation(mc.player.getYaw(), mc.player.getPitch());
   }

   public static boolean validDistance(Entity entity, float distance, boolean smart) {
      return getStrictDistance(entity) < distance;
   }

   public static Vec3d getClosestVec(Entity entity) {
      Vec3d eyePosVec = mc.player.getEyePos();
      return getClosestVec(eyePosVec, entity).subtract(eyePosVec);
   }

   public static Vec3d getClosestVec(Vec3d vec, Box AABB) {
      return new Vec3d(
            MathHelper.clamp(vec.x, AABB.minX, AABB.maxX),
            MathHelper.clamp(vec.y, AABB.minY, AABB.maxY),
            MathHelper.clamp(vec.z, AABB.minZ, AABB.maxZ));
   }

   public static Vec3d getClosestVec(Vec3d vec, Entity entity) {
      return getClosestVec(vec, entity.getBoundingBox());
   }

   public static Vec3d getVector4(LivingEntity target) {
      double wHalf = target.getWidth() / 2.0F;
      double yExpand = MathHelper.clamp(target.getY() - 6.0, 0.0, target.getHeight());
      double xExpand = MathHelper.clamp(mc.player.getX() - target.getX(), -wHalf, wHalf);
      double zExpand = MathHelper.clamp(mc.player.getZ() - target.getZ(), -wHalf, wHalf);
      return new Vec3d(
            target.getX() - mc.player.getX() + xExpand,
            target.getY() - mc.player.getY() - 0.8F,
            target.getZ() - mc.player.getZ() + zExpand);
   }

   public static Vec3d getVector3(LivingEntity target) {
      double yExpand = MathHelper.clamp(target.getY() - target.getY(), 0.0, target.getHeight());
      double xExpand = MathHelper.clamp(mc.player.getX() - target.getX(), 0.0, 0.0);
      double zExpand = MathHelper.clamp(mc.player.getZ() - target.getZ(), 0.0, 0.0);
      return new Vec3d(
            target.getX() - mc.player.getX() + xExpand,
            target.getY() - mc.player.getY() - 0.8F,
            target.getZ() - mc.player.getZ() + zExpand);
   }

   public static Vec3d getVector2(LivingEntity target) {
      double yExpand = MathHelper.clamp(target.getEyeY() - target.getY(), 0.0, target.getHeight());
      double xExpand = MathHelper.clamp(mc.player.getX() - target.getX(), 0.0, 0.0);
      double zExpand = MathHelper.clamp(mc.player.getZ() - target.getZ(), 0.0, 0.0);
      return new Vec3d(
            target.getX() - mc.player.getX() + xExpand,
            target.getY() - mc.player.getEyeY() + yExpand,
            target.getZ() - mc.player.getZ() + zExpand);
   }

   public static Vec3d getVector(LivingEntity target) {
      double wHalf = target.getWidth() / 2.0F;
      double yExpand = MathHelper.clamp(target.getEyeY() - target.getY(), 0.0, target.getHeight());
      double xExpand = MathHelper.clamp(mc.player.getX() - target.getX(), -wHalf, wHalf);
      double zExpand = MathHelper.clamp(mc.player.getZ() - target.getZ(), -wHalf, wHalf);
      return new Vec3d(
            target.getX() - mc.player.getX() + xExpand,
            target.getY() - mc.player.getEyeY() + yExpand,
            target.getZ() - mc.player.getZ() + zExpand);
   }

   public static double direction(float rotationYaw, float moveForward, float moveStrafing) {
      if (moveForward < 0.0F) {
         rotationYaw += 180.0F;
      }

      float forward = 1.0F;
      if (moveForward < 0.0F) {
         forward = -0.5F;
      }

      if (moveForward > 0.0F) {
         forward = 0.5F;
      }

      if (moveStrafing > 0.0F) {
         rotationYaw -= 90.0F * forward;
      }

      if (moveStrafing < 0.0F) {
         rotationYaw += 90.0F * forward;
      }

      return Math.toRadians(rotationYaw);
   }

   public static Vec3d getClosestTargetPoint(Vec3d vec, Entity entity, float point) {
      if (entity == null) {
         return Vec3d.ZERO;
      } else {
         Box box = entity.getBoundingBox().expand(-point);
         Vec3d center = box.getCenter();
         Vec3d closestPoint = null;
         double closestDistance = Double.MAX_VALUE;

         for (double offsetX = 0.0; offsetX <= (box.maxX - box.minX) / 2.0; offsetX += 0.1) {
            for (double offsetY = 0.0; offsetY <= (box.maxY - box.minY) / 2.0; offsetY += 0.1) {
               for (double offsetZ = 0.0; offsetZ <= (box.maxZ - box.minZ) / 2.0; offsetZ += 0.1) {
                  for (int signX : new int[] { -1, 1 }) {
                     for (int signY : new int[] { -1, 1 }) {
                        for (int signZ : new int[] { -1, 1 }) {
                           double x = center.x + signX * offsetX;
                           double y = center.y + signY * offsetY;
                           double z = center.z + signZ * offsetZ;
                           Vec3d potentialPoint = new Vec3d(x, y, z);
                           Vector2f rotation = calculate(potentialPoint);
                           if (RayTraceUtil.calculateRayTrace(6.0, rotation.x, rotation.y, mc.player,
                                 false) instanceof EntityHitResult entityTrace
                                 && entityTrace.getEntity().equals(entity)) {
                              double distance = vec.distanceTo(potentialPoint);
                              if (distance < closestDistance) {
                                 closestDistance = distance;
                                 closestPoint = potentialPoint;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (closestPoint != null) {
            return closestPoint;
         } else {
            double closestX = MathHelper.clamp(vec.x, box.minX, box.maxX);
            double closestY = MathHelper.clamp(vec.y, box.minY, box.maxY);
            double closestZ = MathHelper.clamp(vec.z, box.minZ, box.maxZ);
            return new Vec3d(closestX, closestY, closestZ);
         }
      }
   }

   public static Vector2f calculate(Vec3d toVec) {
      return calculate(new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ()).add(0.0, mc.player.getEyeY(), 0.0), toVec);
   }

   public static Vector2f calculate(Vec3d fromVec, Vec3d toVec) {
      double TO_DEGREES = 180.0 / Math.PI;
      Vec3d diff = toVec.subtract(fromVec);
      double distance = Math.hypot(diff.x, diff.z);
      float yaw = (float) (MathHelper.atan2(diff.z, diff.x) * (180.0 / Math.PI)) - 90.0F;
      float pitch = (float) (-(MathHelper.atan2(diff.y, distance) * (180.0 / Math.PI)));
      return new Vector2f(yaw, pitch);
   }

   public static Vec3d getClosestTargetPoint(Entity entity) {
      float tickDelta = mc.getRenderTickCounter().getTickProgress(false);
      return getClosestTargetPoint(mc.player.getCameraPosVec(tickDelta), entity,
            Math.min(entity.getWidth(), entity.getHeight()) / 4.0F);
   }

   public static Vector4f calculateRotationFromCamera(LivingEntity target) {
      float tickDelta = mc.getRenderTickCounter().getTickProgress(false);
      Vec3d eyePos = mc.player.getCameraPosVec(tickDelta);
      Vec3d vec = getClosestTargetPoint(target).subtract(eyePos);
      float rawYaw = MathHelper.wrapDegrees((float) (Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0));
      float rawPitch = (float) (-Math.toDegrees(Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z))));
      float yawDelta = MathHelper.wrapDegrees(rawYaw - mc.player.getYaw());
      float pitchDelta = rawPitch - mc.player.getPitch();
      return new Vector4f(rawYaw, rawPitch, yawDelta, pitchDelta);
   }

   public static double calculateFOVFromCamera(LivingEntity target) {
      Vector4f rotation = calculateRotationFromCamera(target);
      float yawDelta = rotation.z;
      float pitchDelta = rotation.w;
      return Math.sqrt(yawDelta * yawDelta + pitchDelta * pitchDelta);
   }

   @Generated
   private AuraUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
