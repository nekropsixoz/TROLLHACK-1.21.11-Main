package ru.noxium.module.impl.combat.auraProcess.auraUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.entity.EntityPose;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import ru.noxium.util.other.IMinecraft;
import ru.noxium.util.render.animation.util.Easings;

@Environment(EnvType.CLIENT)
public final class UBoxPoints implements IMinecraft {
   public static double clamp(double value, double min, double max) {
      return Math.min(max, Math.max(value, min));
   }

   public static int lerp(int a, int b, float f) {
      return a + (int)(f * (b - a));
   }

   public static double lerp(double a, double b, double f) {
      return a + f * (b - a);
   }

   public static HitResult traceBlock(Vec3d startVec, Vec3d endVec, ShapeType blockMode, FluidHandling fluidMode) {
      return mc.world.raycast(new RaycastContext(startVec, endVec, blockMode, fluidMode, mc.player));
   }

   private static double getDistanceXZ(ClientPlayerEntity self, double x, double z) {
      double d0 = self.getX() - x;
      double d1 = self.getZ() - z;
      return MathHelper.sqrt((float)(d0 * d0 + d1 * d1));
   }

   private static boolean seenOnce3(ClientPlayerEntity self, double x, double y, double z) {
      Vec3d vector3d1 = new Vec3d(x, y, z);
      return mc.world != null
         && traceBlock(self.getEyePos(), vector3d1, ShapeType.COLLIDER, FluidHandling.NONE).getType() != Type.BLOCK;
   }

   private static boolean seenOnceVector3d(ClientPlayerEntity self, Vec3d vec) {
      Vec3d vector3d = new Vec3d(self.getX(), self.getEyeY(), self.getZ());
      return mc.world != null && traceBlock(vector3d, vec, ShapeType.COLLIDER, FluidHandling.NONE).getType() != Type.BLOCK;
   }

   private static boolean localSeen(ClientPlayerEntity selfEntity, Vec3d xyz, float scale) {
      return scale == 0.0F
         ? seenOnce3(selfEntity, xyz.x, xyz.y, xyz.z)
         : seenOnce3(selfEntity, xyz.x, xyz.y, xyz.z)
            && seenOnce3(selfEntity, xyz.x, xyz.y + scale, xyz.z)
            && seenOnce3(selfEntity, xyz.x, xyz.y - scale, xyz.z)
            && seenOnce3(selfEntity, xyz.x + scale, xyz.y, xyz.z)
            && seenOnce3(selfEntity, xyz.x - scale, xyz.y, xyz.z)
            && seenOnce3(selfEntity, xyz.x, xyz.y, xyz.z + scale)
            && seenOnce3(selfEntity, xyz.x, xyz.y, xyz.z - scale);
   }

   public static List<Vec3d> entityBoxVec3dsAlternate(Box aabb) {
      List<Vec3d> vecs = new ArrayList<>();
      double offsetXYZ = 0.01F;
      int maxPointsCountXZ = 17;
      int minPointsCountXZ = 5;
      int maxPointsCountY = 24;
      int minPointsCountY = 6;
      aabb = aabb.offset(-offsetXYZ, -offsetXYZ, -offsetXYZ);
      double[] whh = new double[]{aabb.maxX - aabb.minX, aabb.maxY - aabb.minY, (aabb.maxY - aabb.minY) / 1.05};
      double[] xyz = new double[]{aabb.minX + whh[0] / 2.0, aabb.minY, aabb.minZ + whh[0] / 2.0};
      double[] xyz1 = new double[]{aabb.minX, aabb.minY, aabb.minZ};
      double[] xyz2 = new double[]{aabb.maxX, aabb.maxY, aabb.maxZ};
      float sqrtWHH0CubeD2 = (float)Math.sqrt(whh[0] * whh[0] + whh[0] * whh[0] + whh[0] * whh[0]) / 2.0F;
      ClientPlayerEntity me = mc.player;
      if (me == null) {
         return null;
      } else {
         Vec3d mePos = new Vec3d(me.getX(), me.getY(), me.getZ());
         float factorCount = (float)(
            (1.0 - Math.min(mePos.distanceTo(new Vec3d(xyz[0], xyz[1], xyz[2])) / 5.0, 1.0))
               * Math.min(mePos.distanceTo(new Vec3d(xyz[0], me.getY(), xyz[2])) / 0.6F, 1.0)
         );
         int pointsCountXZ = lerp(minPointsCountXZ, maxPointsCountXZ, factorCount);
         int pointsCountY = lerp(minPointsCountY, maxPointsCountY, factorCount);
         float scaleSeenCheck = 0.0F;
         int[] var18 = IntStream.range(0, pointsCountXZ).toArray();
         int var19 = var18.length;

         for (int var20 = 0; var20 < var19; var20++) {
            Integer xsI = var18[var20];
            boolean edgeX = xsI == 0 || xsI == pointsCountXZ - 1;
            double xs = lerp(xyz1[0], xyz2[0], (double)((float)xsI.intValue() / (pointsCountXZ - 1)));
            int[] var25 = IntStream.range(0, pointsCountXZ).toArray();
            int var26 = var25.length;

            for (int var27 = 0; var27 < var26; var27++) {
               Integer zsI = var25[var27];
               boolean edgeZ = zsI == 0 || zsI == pointsCountXZ - 1;
               double zs = lerp(xyz1[2], xyz2[2], (double)((float)zsI.intValue() / (pointsCountXZ - 1)));
               int[] var32 = IntStream.range(0, pointsCountY).toArray();
               int var33 = var32.length;

               for (int var34 = 0; var34 < var33; var34++) {
                  Integer ysI = var32[var34];
                  boolean edgeY = ysI == 0 || ysI == pointsCountY - 1;
                  double ys = lerp(xyz1[1], xyz2[1], (double)((float)ysI.intValue() / (pointsCountY - 1)));
                  Vec3d vec = new Vec3d(xs, ys, zs);
                  if ((edgeX || edgeZ || edgeY)
                     && !(mePos.distanceTo(vec.add(0.0, -me.getEyeHeight(EntityPose.STANDING), 0.0)) < sqrtWHH0CubeD2)
                     && localSeen(me, vec, scaleSeenCheck)
                     && !vecs.add(vec)) {
                     break;
                  }
               }
            }
         }

         return vecs;
      }
   }

   private static double getDistanceAtVec3dToVec3d(Vec3d first, Vec3d second) {
      double xDiff;
      double yDiff;
      double zDiff;
      return Math.sqrt(
         (xDiff = first.x - second.x) * xDiff
            + (yDiff = first.y - second.y) * yDiff
            + (zDiff = first.z - second.z) * zDiff
      );
   }

   public static Vec3d getBestVector3dOnEntityBox(Box aabb) {
      return getBestVector3dOnEntityBox(aabb, true);
   }

   public static Vec3d getBestVector3dOnEntityBox(Box aabb, boolean alwaysMultipoints) {
      if (aabb == null) {
         return mc.player.getEyePos();
      } else {
         double[] whh = new double[]{aabb.maxX - aabb.minX, aabb.maxY - aabb.minY, (aabb.maxY - aabb.minY) / 1.1F};
         double[] xyz = new double[]{aabb.minX + whh[0] / 2.0, aabb.minY, aabb.minZ + whh[0] / 2.0};
         double[] diffs = new double[]{mc.player.getY() - xyz[1], getDistanceXZ(mc.player, xyz[0], xyz[2])};
         double ddtn = clamp(Easings.QUART_OUT.ease((diffs[1] - whh[0] / 2.0) / (5.0 + whh[0] / 2.0)), 0.1, 0.95);
         double pca = clamp(ddtn * ddtn, 0.0, 1.0);
         double pitchPointHeight = clamp(whh[2] / 2.0 * pca + whh[2] / 2.0 * clamp(diffs[0] + pca, 0.0, 1.0), 0.0, whh[2]);
         Vec3d defaultVec = new Vec3d(xyz[0], xyz[1] + pitchPointHeight, xyz[2]);
         if (!alwaysMultipoints && !seenOnceVector3d(mc.player, defaultVec)) {
            defaultVec = defaultVec.add(0.0, -pitchPointHeight / 2.0, 0.0);
         }

         if (!(whh[1] <= 1.0) && (alwaysMultipoints || !seenOnceVector3d(mc.player, defaultVec))) {
            List<Vec3d> normalVecs = entityBoxVec3dsAlternate(aabb);
            float factorDown = 1.0F - (float)Math.max(Math.min((diffs[1] - 2.0) / 3.0, 1.0), 0.0);
            Vec3d toSortVec = new Vec3d(
               mc.player.getX(),
               mc.player.getY() + 0.6F + lerp(pitchPointHeight, pitchPointHeight / 2.5, (double)factorDown),
               mc.player.getZ()
            );
            if (normalVecs != null && normalVecs.size() > 1) {
               normalVecs.sort(Comparator.comparing(vec3 -> getDistanceAtVec3dToVec3d(toSortVec, vec3)));
            }

            return normalVecs != null && !normalVecs.isEmpty() ? normalVecs.getFirst() : defaultVec;
         } else {
            return defaultVec;
         }
      }
   }

   @Generated
   private UBoxPoints() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
