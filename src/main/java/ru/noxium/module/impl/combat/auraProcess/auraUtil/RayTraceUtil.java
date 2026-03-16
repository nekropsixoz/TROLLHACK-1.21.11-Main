package ru.noxium.module.impl.combat.auraProcess.auraUtil;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.world.World;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import ru.noxium.util.other.IMinecraft;

@Environment(EnvType.CLIENT)
public final class RayTraceUtil implements IMinecraft {
   public static EntityHitResult traceEntities(
         Entity shooter, Vec3d startVector, Vec3d endVector, Box boundingBox, Predicate<Entity> filter,
         double distance) {
      World world = MinecraftClient.getInstance().world;
      double closestDistance = distance;
      Entity closestEntity = null;
      Vec3d closestHitVector = null;

      for (Entity entity : world.getOtherEntities(shooter, boundingBox, filter)) {
         Box entityBoundingBox = entity.getBoundingBox().expand(entity.getTargetingMargin());
         Optional<Vec3d> optional = entityBoundingBox.raycast(startVector, endVector);
         if (entityBoundingBox.contains(startVector) || optional.isPresent()) {
            double distanceToHit = optional.<Double>map(startVector::squaredDistanceTo).orElse(0.0);
            distanceToHit = Math.sqrt(distanceToHit);
            if ((distanceToHit < closestDistance || closestDistance == 0.0)
                  && entity.getRootVehicle() != shooter.getRootVehicle()) {
               closestEntity = entity;
               closestHitVector = optional.orElse(startVector);
               closestDistance = distanceToHit;
            }
         }
      }

      return closestEntity == null ? null : new EntityHitResult(closestEntity, closestHitVector);
   }

   public static HitResult calculateRayTrace(double distance, float yaw, float pitch, Entity entity,
         boolean ignoreBlocks) {
      float tickDelta = mc.getRenderTickCounter().getTickProgress(true);
      Vec3d startVector = mc.player.getCameraPosVec(tickDelta);
      Vec3d directionVector = getVectorForRotation(pitch, yaw);
      Vec3d endVector = startVector.add(directionVector.multiply(distance));
      HitResult blockResult = traceBlock(startVector, endVector, ShapeType.COLLIDER, FluidHandling.NONE);
      double entityDistance = blockResult.getPos().squaredDistanceTo(startVector);
      Box entityBoundingBox = entity.getBoundingBox().stretch(directionVector.multiply(distance)).expand(1.0);
      EntityHitResult entityHitResult = ProjectileUtil.raycast(
            entity, startVector, endVector, entityBoundingBox, x -> !x.isSpectator() && x.isAlive() && x.canHit(),
            distance * distance);
      return (HitResult) (entityHitResult == null
            || !ignoreBlocks && !(entityHitResult.getPos().squaredDistanceTo(startVector) < entityDistance)
                  ? blockResult
                  : entityHitResult);
   }

   public static boolean rayTraceEntity(float yaw, float pitch, double distance, Entity entity) {
      float tickDelta = mc.getRenderTickCounter().getTickProgress(true);
      Vec3d eyeVec = mc.player.getCameraPosVec(tickDelta);
      Vec3d lookVec = getVectorForRotation(pitch, yaw);
      Vec3d endVec = eyeVec.add(lookVec.multiply(distance));
      Box entityBox = entity.getBoundingBox();
      return entityBox.contains(eyeVec) || entityBox.raycast(eyeVec, endVec).isPresent();
   }

   public static Vec3d getVectorForRotation(float pitch, float yaw) {
      float yawRadians = -yaw * (float) (Math.PI / 180.0) - (float) Math.PI;
      float pitchRadians = -pitch * (float) (Math.PI / 180.0);
      float cosYaw = (float) Math.cos(yawRadians);
      float sinYaw = (float) Math.sin(yawRadians);
      float cosPitch = -(float) Math.cos(pitchRadians);
      float sinPitch = (float) Math.sin(pitchRadians);
      return new Vec3d(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
   }

   public static HitResult traceBlock(Vec3d startVec, Vec3d endVec, ShapeType blockMode, FluidHandling fluidMode) {
      return mc.world.raycast(new RaycastContext(startVec, endVec, blockMode, fluidMode, mc.player));
   }

   public static Vec3d calculateViewVector(float yaw, float pitch) {
      float pitchRad = (float) (pitch * (Math.PI / 180.0));
      float yawRad = (float) (-yaw * (Math.PI / 180.0));
      float cosYaw = (float) Math.cos(yawRad);
      float sinYaw = (float) Math.sin(yawRad);
      float cosPitch = (float) Math.cos(pitchRad);
      float sinPitch = (float) Math.sin(pitchRad);
      return new Vec3d(sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);
   }

   @Generated
   private RayTraceUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
