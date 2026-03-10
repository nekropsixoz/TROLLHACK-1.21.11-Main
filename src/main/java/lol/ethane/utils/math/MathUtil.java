package lol.ethane.utils.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import lol.ethane.utils.rotation.RotationUtil;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class MathUtil {
   private static final float[] SIN_TABLE = new float[65536];

   public static double distance(class_1297 entity1, class_1297 entity2) {
      return Math.sqrt(squaredBoxedDistanceTo(entity1, entity2));
   }

   public static double squaredBoxedDistanceTo(class_1297 entity1, class_1297 entity2) {
      return squaredBoxedDistanceTo(entity1, entity2.method_33571());
   }

   public static double squaredBoxedDistanceTo(class_1297 entity, class_243 otherPos) {
      return squaredBoxedDistanceTo(entity.method_5829(), otherPos);
   }

   public static double squaredBoxedDistanceTo(class_238 box, class_243 otherPos) {
      class_243 pos = getNearestPoint(otherPos, box);
      return pos.method_1025(otherPos);
   }

   public static class_243 closestPoint(class_243 eyePos, class_238 box) {
      return new class_243(Math.max(box.field_1323, Math.min(eyePos.field_1352, box.field_1320)), Math.max(box.field_1322, Math.min(eyePos.field_1351, box.field_1325)), Math.max(box.field_1321, Math.min(eyePos.field_1350, box.field_1324)));
   }

   public static double roundToDecimalPlace(double value, double inc) {
      double halfOfInc = inc / 2.0D;
      double floored = StrictMath.floor(value / inc) * inc;
      return value >= floored + halfOfInc ? (new BigDecimal(StrictMath.ceil(value / inc) * inc, MathContext.DECIMAL64)).stripTrailingZeros().doubleValue() : (new BigDecimal(floored, MathContext.DECIMAL64)).stripTrailingZeros().doubleValue();
   }

   public static class_243 getBestAimPoint(class_238 box) {
      assert class_310.method_1551().field_1724 != null;

      class_243 eyePos = class_310.method_1551().field_1724.method_33571();
      return box.field_1323 < eyePos.field_1352 && eyePos.field_1352 < box.field_1320 && box.field_1321 < eyePos.field_1350 && eyePos.field_1350 < box.field_1324 ? new class_243(box.field_1323 + (box.field_1320 - box.field_1323) / 2.0D, Math.max(box.field_1322, Math.min(eyePos.field_1351, box.field_1325)), box.field_1321 + (box.field_1324 - box.field_1321) / 2.0D) : closestPoint(eyePos, box);
   }

   public static int getGridIndex(class_243 point, class_238 box) {
      if (!box.method_1006(point)) {
         return -1;
      } else {
         int resolution = 1;
         int x = (int)Math.clamp((point.field_1352 - box.field_1323) / (box.field_1320 - box.field_1323) * 4.0D, 0.0D, 3.0D);
         int y = (int)Math.clamp((point.field_1351 - box.field_1322) / (box.field_1325 - box.field_1322) * 4.0D, 0.0D, 3.0D);
         int z = (int)Math.clamp((point.field_1350 - box.field_1321) / (box.field_1324 - box.field_1321) * 4.0D, 0.0D, 3.0D);
         return x + y * 4 + z * 4 * 4;
      }
   }

   public static class_243 getBestAimPointSmart(class_238 box, class_1297 entity) {
      return getBestAimPointSmart(box, entity, Collections.emptySet(), 0.0D);
   }

   public static class_243 getBestAimPointSmart(class_238 box, class_1297 entity, Collection<Integer> avoidedIndices, double voidStrength) {
      class_243 best = getBestAimPoint(box);
      boolean visible = canVectorBeSeen(class_310.method_1551().field_1724.method_33571(), best);
      class_243 newBest;
      double x;
      if (!visible || !avoidedIndices.isEmpty()) {
         newBest = null;
         double bestScore = Double.POSITIVE_INFINITY;

         for(x = 0.0D; x <= 1.0D; x += 0.1D) {
            for(double y = 0.0D; y <= 1.0D; y += 0.1D) {
               for(double z = 0.0D; z <= 1.0D; z += 0.1D) {
                  class_243 vector = new class_243(box.field_1323 + (box.field_1320 - box.field_1323) * x, box.field_1322 + (box.field_1325 - box.field_1322) * y, box.field_1321 + (box.field_1324 - box.field_1321) * z);
                  if (canVectorBeSeen(class_310.method_1551().field_1724.method_33571(), vector)) {
                     double score = class_310.method_1551().field_1724.method_33571().method_1025(vector);
                     if (!avoidedIndices.isEmpty()) {
                        int gridIdx = getGridIndex(vector, box);
                        if (avoidedIndices.contains(gridIdx)) {
                           score += voidStrength * 100.0D;
                        }
                     }

                     if (score < bestScore) {
                        newBest = vector;
                        bestScore = score;
                     }
                  }
               }
            }
         }

         if (newBest != null) {
            best = newBest;
         }
      }

      newBest = best;
      class_243 actualVelocity = new class_243(class_310.method_1551().field_1724.field_6014 - class_310.method_1551().field_1724.method_23317(), class_310.method_1551().field_1724.field_6036 - class_310.method_1551().field_1724.method_23318(), class_310.method_1551().field_1724.field_5969 - class_310.method_1551().field_1724.method_23321());
      class_243 diff = actualVelocity.method_1023(entity.field_6014 - entity.method_23317(), entity.field_6036 - entity.method_23318(), entity.field_5969 - entity.method_23321()).method_22882();
      if (diff.method_1027() > 0.0D) {
         newBest = best.method_1031(0.0D, (ThreadLocalRandom.current().nextDouble() - 0.5D) * (diff.field_1351 + 0.1D), 0.0D);
      }

      newBest = newBest.method_1020(diff.method_1021(0.3D));
      x = (double)(RotationUtil.fov(new class_241(class_310.method_1551().field_1724.method_36454(), class_310.method_1551().field_1724.method_36455()), getRotations(class_310.method_1551().field_1724.method_33571(), newBest)) / 255.0F);
      newBest = newBest.method_1031(0.0D, -x * box.method_17940(), 0.0D);
      best = closestPoint(newBest, box);
      return best;
   }

   public static class_3965 rayCast(class_243 start, class_243 end) {
      assert class_310.method_1551().field_1724 != null;

      assert class_310.method_1551().field_1687 != null;

      return class_310.method_1551().field_1687.method_17742(new class_3959(start, end, class_3960.field_17559, class_242.field_1348, class_310.method_1551().field_1724));
   }

   public static boolean canVectorBeSeen(class_243 start, class_243 end) {
      class_3965 rayCast = rayCast(start, end);
      return rayCast == null || rayCast.method_17783() != class_240.field_1332 || !(rayCast instanceof class_3965);
   }

   public static class_241 getRotations(class_243 from, class_243 to) {
      class_243 delta = new class_243(to.field_1352 - from.field_1352, to.field_1351 - from.field_1351, to.field_1350 - from.field_1350);
      return getRotations(delta);
   }

   public static class_241 getRotations(class_243 delta) {
      return new class_241((float)getYaw(delta), (float)getPitch(delta.field_1351, delta.method_37267()));
   }

   public static double getPitch(double deltaY, double distance) {
      return -Math.toDegrees(Math.atan2(deltaY, distance));
   }

   public static double getYaw(class_243 delta) {
      return Math.toDegrees(Math.atan2(delta.field_1350, delta.field_1352)) - 90.0D;
   }

   public static class_243 getNearestPoint(class_243 eyes, class_238 box) {
      double[] origin = new double[]{eyes.field_1352, eyes.field_1351, eyes.field_1350};
      double[] destMins = new double[]{box.field_1323, box.field_1322, box.field_1321};
      double[] destMaxs = new double[]{box.field_1320, box.field_1325, box.field_1324};

      for(int i = 0; i < 3; ++i) {
         origin[i] = Math.max(destMins[i], Math.min(origin[i], destMaxs[i]));
      }

      return new class_243(origin[0], origin[1], origin[2]);
   }

   public static class_243 getNearestPoint(class_238 box, class_243 from) {
      return new class_243(class_3532.method_15350(from.field_1352, box.field_1323, box.field_1320), class_3532.method_15350(from.field_1351, box.field_1322, box.field_1325), class_3532.method_15350(from.field_1350, box.field_1321, box.field_1324));
   }

   public static Number roundAndClamp(Number value, Number minValue, Number maxValue, Number increment) {
      Objects.requireNonNull(value);
      
      if (value instanceof Double casted) {
         casted = (double)Math.round(casted / increment.doubleValue()) * increment.doubleValue();
         casted = class_3532.method_15350(casted, minValue.doubleValue(), maxValue.doubleValue());
         return casted;
      } else if (value instanceof Float casted) {
         casted = (float)Math.round(casted / increment.floatValue()) * increment.floatValue();
         casted = class_3532.method_15363(casted, minValue.floatValue(), maxValue.floatValue());
         return casted;
      } else if (value instanceof Long casted) {
         casted = (long)Math.round((float)casted / (float)increment.longValue()) * increment.longValue();
         casted = class_3532.method_53062(casted, minValue.longValue(), maxValue.longValue());
         return casted;
      } else {
         int casted = value.intValue();
         casted = Math.round((float)casted / (float)increment.intValue()) * increment.intValue();
         casted = class_3532.method_15340(casted, minValue.intValue(), maxValue.intValue());
         return casted;
      }
   }

   public static float getBiasedRandomFloat(float min, float max, float bias) {
      float r = ThreadLocalRandom.current().nextFloat();
      r = (float)Math.pow((double)r, (double)bias);
      return min + (max - min) * r;
   }

   public static float fastSin(double rad) {
      return SIN_TABLE[(int)(rad * 10430.378350470453D) & '\uffff'];
   }

   static {
      for(int i = 0; i < 65536; ++i) {
         SIN_TABLE[i] = (float)Math.sin((double)i * 3.141592653589793D * 2.0D / 65536.0D);
      }

   }
}
