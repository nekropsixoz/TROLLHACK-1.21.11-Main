package lol.ethane.feature.module.defined.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lol.ethane.event.defined.game.PreGameTickEvent;
import lol.ethane.event.subscriber.Subscribe;
import lol.ethane.feature.helper.impl.player.rotation.RotationHelper;
import lol.ethane.feature.helper.impl.player.rotation.model.IRotationModel;
import lol.ethane.feature.helper.impl.player.rotation.model.impl.AdaptiveRotationModel;
import lol.ethane.feature.helper.impl.player.rotation.model.impl.AdvancedRotationModel;
import lol.ethane.feature.helper.impl.player.rotation.model.impl.LinearRotationModel;
import lol.ethane.feature.helper.impl.player.rotation.model.impl.SpookyTimeRotationModel;
import lol.ethane.feature.module.Module;
import lol.ethane.feature.module.ModuleCategory;
import lol.ethane.feature.module.property.Property;
import lol.ethane.feature.module.property.impl.BooleanProperty;
import lol.ethane.feature.module.property.impl.EnumProperty;
import lol.ethane.feature.module.property.impl.NumberProperty;
import lol.ethane.utils.injection.LocalPlayerExtension;
import lol.ethane.utils.math.MathUtil;
import lol.ethane.utils.misc.Stopwatch;
import lombok.Generated;
import net.minecraft.class_1268;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2560;
import net.minecraft.class_2879;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_5134;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class AuraModule extends Module {
   private final Property<Boolean> hitSelect = new BooleanProperty("Hit select", false);
   private final Property<Boolean> perfectCriticals = (new BooleanProperty("Perfect criticals", false)).hideIf(() -> {
      return !(Boolean)this.hitSelect.getValue();
   });
   private final Property<AuraModule.Rotations> rotationsProperty;
   private final Property<AuraModule.Mode> modeProperty;
   private final Property<AuraModule.ClickingMode> clickingModeProperty;
   private final Property<Double> gravity;
   private final Property<Double> wind;
   private final Property<Double> yawInfluenceOnPitch;
   private final Property<Double> yawInfluenceCap;
   private final Property<Double> pitchInfluenceOnYaw;
   private final Property<Double> pitchInfluenceCap;
   private final AdvancedRotationModel advancedRotationModel;
   private final List<Integer> recentlyAimedParts;
   private float historyHitboxExclusionRadius;
   private int targetTicks;
   private final Property<Double> minCps;
   private final Property<Double> maxCps;
   private final Property<Double> reach;
   private final Property<Boolean> dynamicReach;
   private final Property<Double> hurtReach;
   private final Property<AuraModule.HitSelectMode> hitSelectModeProperty;
   private final Property<Boolean> swingProperty;
   private final Property<Boolean> lockView;
   private final Property<Boolean> rayCastProperty;
   private class_1309 target;
   private final Stopwatch stopwatch;
   private int butterflyClicks;
   private long nextClickDelay;

   public AuraModule() {
      super("Aura", "Attacks nearby players.", ModuleCategory.COMBAT);
      this.rotationsProperty = new EnumProperty("Rotations", AuraModule.Rotations.ADAPTIVE);
      this.modeProperty = new EnumProperty("Mode", AuraModule.Mode.SINGLE);
      this.clickingModeProperty = (new EnumProperty("Clicking mode", AuraModule.ClickingMode.NORMAL)).hideIf(() -> (Boolean)this.hitSelect.getValue());
      this.gravity = (new NumberProperty("Gravity", 9.0D, 1.0D, 20.0D, 0.5D)).hideIf(() -> {
         return this.rotationsProperty.getValue() != AuraModule.Rotations.ADVANCED;
      });
      this.wind = (new NumberProperty("Wind", 3.0D, 1.0D, 10.0D, 0.5D)).hideIf(() -> {
         return this.rotationsProperty.getValue() != AuraModule.Rotations.ADVANCED;
      });
      this.yawInfluenceOnPitch = (new NumberProperty("Yaw Influence %", 14.0D, 0.0D, 100.0D, 1.0D)).hideIf(() -> {
         return this.rotationsProperty.getValue() != AuraModule.Rotations.ADVANCED;
      });
      this.yawInfluenceCap = (new NumberProperty("Yaw Influence Cap", 50.0D, 0.0D, 180.0D, 1.0D)).hideIf(() -> {
         return this.rotationsProperty.getValue() != AuraModule.Rotations.ADVANCED;
      });
      this.pitchInfluenceOnYaw = (new NumberProperty("Pitch Influence %", 15.0D, 0.0D, 100.0D, 1.0D)).hideIf(() -> {
         return this.rotationsProperty.getValue() != AuraModule.Rotations.ADVANCED;
      });
      this.pitchInfluenceCap = (new NumberProperty("Pitch Influence Cap", 30.0D, 0.0D, 180.0D, 1.0D)).hideIf(() -> {
         return this.rotationsProperty.getValue() != AuraModule.Rotations.ADVANCED;
      });
      this.advancedRotationModel = new AdvancedRotationModel();
      this.recentlyAimedParts = new ArrayList();
      this.historyHitboxExclusionRadius = 0.0F;
      this.targetTicks = 0;
      this.minCps = (new NumberProperty("Min CPS", 8.0D, 1.0D, 20.0D, 0.5D)).hideIf(() -> (Boolean)this.hitSelect.getValue());
      this.maxCps = (new NumberProperty("Max CPS", 12.0D, 1.0D, 20.0D, 0.5D)).hideIf(() -> (Boolean)this.hitSelect.getValue());
      this.reach = new NumberProperty("Reach", 4.5D, 3.0D, 6.0D, 0.1D);
      this.dynamicReach = new BooleanProperty("Dynamic reach", false);
      this.hurtReach = (new NumberProperty("Hurt reach", 4.5D, 3.0D, 6.0D, 0.1D)).hideIf(() -> {
         return !(Boolean)this.dynamicReach.getValue();
      });
      this.hitSelectModeProperty = (new EnumProperty("Hit select mode", AuraModule.HitSelectMode.ALWAYS)).hideIf(() -> {
         return !(Boolean)this.hitSelect.getValue();
      });
      this.swingProperty = new BooleanProperty("Swing", true);
      this.lockView = new BooleanProperty("Lock view", false);
      this.rayCastProperty = new BooleanProperty("Ray cast", true);
      this.stopwatch = new Stopwatch();
      this.nextClickDelay = -1L;
      this.addProperties(new Property[]{this.hitSelectModeProperty, this.rotationsProperty, this.clickingModeProperty, this.minCps, this.maxCps, this.modeProperty, this.reach, this.dynamicReach, this.hurtReach, this.gravity, this.wind, this.yawInfluenceOnPitch, this.yawInfluenceCap, this.pitchInfluenceOnYaw, this.pitchInfluenceCap, this.hitSelect, this.perfectCriticals, this.swingProperty, this.lockView, this.rayCastProperty});
   }

   public void onDisable() {
      RotationHelper.getClientHandler().setDisabled(false);
      super.onDisable();
   }

   public String getSuffix() {
      return ((AuraModule.Mode)this.modeProperty.getValue()).toString();
   }

   @Subscribe
   private void onTick(PreGameTickEvent event) {
      if (this.mc.field_1724 != null) {
         RotationHelper.getClientHandler().setDisabled((Boolean)this.lockView.getValue());
         List<class_1309> targets = this.getTargets();
         if (targets.isEmpty()) {
            this.target = null;
            this.targetTicks = 0;
         } else {
            class_1309 newTarget = (class_1309)targets.getFirst();
            if (this.rotationsProperty.getValue() != AuraModule.Rotations.ADVANCED) {
               this.targetTicks = 0;
               this.recentlyAimedParts.clear();
               this.historyHitboxExclusionRadius = 0.0F;
            }

            if (this.target != newTarget && this.rotationsProperty.getValue() == AuraModule.Rotations.ADVANCED) {
               this.targetTicks = 0;
               this.recentlyAimedParts.clear();
               this.advancedRotationModel.reset();
            }

            this.target = newTarget;
            if (this.target != null) {
               ++this.targetTicks;
            }

            if (this.rotationsProperty.getValue() == AuraModule.Rotations.ADVANCED && this.targetTicks <= 1) {
               this.historyHitboxExclusionRadius = MathUtil.getBiasedRandomFloat(0.03F, 0.135F, 0.6F);
            } else if (this.rotationsProperty.getValue() != AuraModule.Rotations.ADVANCED) {
               this.historyHitboxExclusionRadius = 0.0F;
            }

            if (this.rotationsProperty.getValue() != AuraModule.Rotations.NONE) {
               class_238 boundingBox = this.target.method_5829();
               class_243 bestAimPoint = MathUtil.getBestAimPointSmart(boundingBox, this.target, this.rotationsProperty.getValue() == AuraModule.Rotations.ADVANCED ? this.recentlyAimedParts : Collections.emptyList(), 2.0D);
               if (this.historyHitboxExclusionRadius > 0.0F) {
                  double randomTheta = ThreadLocalRandom.current().nextDouble(0.0D, 6.283185307179586D);
                  bestAimPoint = bestAimPoint.method_1031(Math.cos(randomTheta) * (double)this.historyHitboxExclusionRadius, (ThreadLocalRandom.current().nextDouble() - 0.5D) * (double)this.historyHitboxExclusionRadius, Math.sin(randomTheta) * (double)this.historyHitboxExclusionRadius);
               }

               if (this.rotationsProperty.getValue() == AuraModule.Rotations.ADVANCED) {
                  int gridIdx = MathUtil.getGridIndex(bestAimPoint, boundingBox);
                  if (gridIdx != -1 && !this.recentlyAimedParts.contains(gridIdx)) {
                     this.recentlyAimedParts.add(gridIdx);
                     if (this.recentlyAimedParts.size() > 8) {
                        this.recentlyAimedParts.remove(0);
                     }
                  }
               }

               class_241 rotation = MathUtil.getRotations(this.mc.field_1724.method_33571(), bestAimPoint);
               if (this.rotationsProperty.getValue() == AuraModule.Rotations.ADVANCED) {
                  this.advancedRotationModel.setGravity((Double)this.gravity.getValue());
                  this.advancedRotationModel.setWind((Double)this.wind.getValue());
                  this.advancedRotationModel.setRandomStrength(1.25D);
                  this.advancedRotationModel.setStartSmoothingTicks(3);
                  this.advancedRotationModel.setCorrelationStrength(1.0D);
                  this.advancedRotationModel.setYawInfluenceOnPitch((Double)this.yawInfluenceOnPitch.getValue());
                  this.advancedRotationModel.setYawInfluenceCap((Double)this.yawInfluenceCap.getValue());
                  this.advancedRotationModel.setPitchInfluenceOnYaw((Double)this.pitchInfluenceOnYaw.getValue());
                  this.advancedRotationModel.setPitchInfluenceCap((Double)this.pitchInfluenceCap.getValue());
                  this.advancedRotationModel.setAccelerationHistorySize(10);
                  this.advancedRotationModel.setAccelerationInfluence(30.0D);
                  RotationHelper.getHandler().rotate(rotation, this.advancedRotationModel);
               } else {
                  IRotationModel model;
                  if (this.rotationsProperty.getValue() == AuraModule.Rotations.ADAPTIVE) {
                     model = new AdaptiveRotationModel();
                  } else if (this.rotationsProperty.getValue() == AuraModule.Rotations.SPOOKY_TIME) {
                     model = new SpookyTimeRotationModel();
                  } else {
                     model = new LinearRotationModel();
                  }

                  RotationHelper.getHandler().rotate(rotation, model);
               }
            }

            assert this.mc.field_1761 != null;

            if (!(Boolean)this.hitSelect.getValue()) {
               if (this.stopwatch.elapsed(this.getClickDelay()) && this.isFacingTarget(this.target)) {
                  this.mc.field_1761.method_2918(this.mc.field_1724, this.target);
                  this.swing(true);
                  this.nextClickDelay = -1L;
                  this.stopwatch.reset();
               }
            } else {
               float attackCooldown = this.mc.field_1724.method_7261(0.5F);
               float attackDamage = (float)this.mc.field_1724.method_45325(class_5134.field_23721);
               float dmg = attackDamage * (0.2F + attackCooldown * attackCooldown * 0.8F);
               boolean ready = dmg >= 0.985F;
               if (ready && (Boolean)this.perfectCriticals.getValue() && !this.delayForCrit()) {
                  ready = false;
               }

               if (this.hitSelectModeProperty.getValue() == AuraModule.HitSelectMode.ALWAYS) {
                  if (ready && this.isFacingTarget(this.target)) {
                     this.mc.field_1761.method_2918(this.mc.field_1724, this.target);
                     this.swing(true);
                  }
               } else if (this.hitSelectModeProperty.getValue() == AuraModule.HitSelectMode.SERVER) {
                  if (ready && this.isFacingTarget(this.target)) {
                     this.mc.field_1761.method_2918(this.mc.field_1724, this.target);
                     this.swing(true);
                  } else if (!ready) {
                     this.swing(false);
                  }
               }
            }

         }
      }
   }

   private long getClickDelay() {
      if (this.nextClickDelay != -1L) {
         return this.nextClickDelay;
      } else {
         double min = (Double)this.minCps.getValue();
         double max = (Double)this.maxCps.getValue();
         double meanCps = (min + max) / 2.0D;
         double stdDevCps = (max - min) / 4.0D;
         double cps = ThreadLocalRandom.current().nextGaussian() * stdDevCps + meanCps;
         cps = Math.clamp(cps, min, max);
         long delay = (long)(1000.0D / cps);
         if (this.clickingModeProperty.getValue() == AuraModule.ClickingMode.BUTTERFLY) {
            if (this.butterflyClicks > 0) {
               delay = ThreadLocalRandom.current().nextLong(5L, 40L);
               --this.butterflyClicks;
            } else {
               delay = (long)(1000.0D / (cps * 0.5D));
               if (ThreadLocalRandom.current().nextDouble() < 0.8D) {
                  this.butterflyClicks = 1;
               }
            }
         } else {
            delay += (long)(ThreadLocalRandom.current().nextGaussian() * 15.0D);
         }

         return this.nextClickDelay = Math.max(0L, delay);
      }
   }

   private boolean isFacingTarget(class_1309 target) {
      if (!(Boolean)this.rayCastProperty.getValue()) {
         return true;
      } else {
         class_243 eyePos = this.mc.field_1724.method_33571();
         class_243 viewVec = this.mc.field_1724.method_5828(1.0F);
         double currentReach = (Double)this.reach.getValue();
         if ((Boolean)this.dynamicReach.getValue() && this.mc.field_1724.field_6235 != 0) {
            currentReach = (Double)this.hurtReach.getValue();
         }

         class_243 endPos = eyePos.method_1019(viewVec.method_1021(currentReach));
         Optional<class_243> entityHit = target.method_5829().method_992(eyePos, endPos);
         if (entityHit.isEmpty()) {
            return false;
         } else {
            class_3965 blockHit = this.mc.field_1687.method_17742(new class_3959(eyePos, endPos, class_3960.field_17558, class_242.field_1348, this.mc.field_1724));
            if (blockHit.method_17783() != class_240.field_1333) {
               return blockHit.method_17784().method_1025(eyePos) > ((class_243)entityHit.get()).method_1025(eyePos);
            } else {
               return true;
            }
         }
      }
   }

   private boolean delayForCrit() {
      boolean[] conditions = new boolean[]{this.mc.field_1724.method_5771(), this.mc.field_1724.method_5799(), this.mc.field_1724.method_5782(), this.mc.field_1687.method_8320(this.mc.field_1724.method_23314()).method_26204() instanceof class_2560, this.mc.field_1724.method_6059(class_1294.field_5902), this.mc.field_1724.method_6059(class_1294.field_5919), this.mc.field_1724.method_6059(class_1294.field_5906), this.mc.field_1724.method_6101(), this.mc.field_1724.method_5740(), this.mc.field_1724.method_5765(), this.mc.field_1724.method_31549().field_7479, this.mc.field_1724.method_24828()};
      boolean canCrit = true;
      boolean[] var3 = conditions;
      int var4 = conditions.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         boolean condition = var3[var5];
         if (condition) {
            canCrit = false;
            break;
         }
      }

      if (!canCrit) {
         return true;
      } else if (this.mc.field_1724.field_6017 > 1.0D && this.mc.field_1724.field_6017 < 1.14D) {
         return false;
      } else {
         return !this.mc.field_1724.method_24828() && this.mc.field_1724.field_6017 > 0.25D;
      }
   }

   private void swing(boolean packet) {
      assert this.mc.field_1724 != null;

      if (packet) {
         if ((Boolean)this.swingProperty.getValue()) {
            this.mc.field_1724.method_6104(class_1268.field_5808);
         } else {
            this.mc.field_1724.field_3944.method_52787(new class_2879(class_1268.field_5808));
         }
      } else {
         ((LocalPlayerExtension)this.mc.field_1724).ethane$swingClient(class_1268.field_5808);
      }

   }

   private List<class_1309> getTargets() {
      List<class_1309> targets = new ArrayList();

      assert this.mc.field_1687 != null;

      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      while(var2.hasNext()) {
         class_1297 entity = (class_1297)var2.next();
         if (entity instanceof class_1657) {
            class_1657 player = (class_1657)entity;
            if (entity.method_5805()) {
               assert this.mc.field_1724 != null;

               double reach = this.mc.field_1724.field_6235 == 0 ? (Double)this.reach.getValue() : (Double)this.hurtReach.getValue();
               if (!(Boolean)this.dynamicReach.getValue()) {
                  reach = (Double)this.reach.getValue();
               }

               if (!(MathUtil.distance(entity, this.mc.field_1724) > reach) && entity.field_6012 >= 5 && !(entity.method_5829().method_995() <= 0.075D) && this.mc.field_1724 != entity) {
                  targets.add(player);
               }
            }
         }
      }

      targets.sort(Comparator.comparingDouble((entityx) -> {
         return MathUtil.distance(entityx, this.mc.field_1724);
      }));
      return targets;
   }

   private static enum Rotations {
      NONE("None"),
      LINEAR("Linear"),
      ADAPTIVE("Adaptive"),
      SPOOKY_TIME("SpookyTime"),
      ADVANCED("Advanced");

      private final String name;

      public String toString() {
         return this.name;
      }

      @Generated
      private Rotations(final String name) {
         this.name = name;
      }

      // $FF: synthetic method
      private static AuraModule.Rotations[] $values() {
         return new AuraModule.Rotations[]{NONE, LINEAR, ADAPTIVE, SPOOKY_TIME, ADVANCED};
      }
   }

   private static enum Mode {
      SINGLE("Single");

      private final String name;

      public String toString() {
         return this.name;
      }

      @Generated
      private Mode(final String name) {
         this.name = name;
      }

      // $FF: synthetic method
      private static AuraModule.Mode[] $values() {
         return new AuraModule.Mode[]{SINGLE};
      }
   }

   private static enum ClickingMode {
      NORMAL("Normal"),
      BUTTERFLY("Butterfly");

      private final String name;

      public String toString() {
         return this.name;
      }

      @Generated
      private ClickingMode(final String name) {
         this.name = name;
      }

      // $FF: synthetic method
      private static AuraModule.ClickingMode[] $values() {
         return new AuraModule.ClickingMode[]{NORMAL, BUTTERFLY};
      }
   }

   private static enum HitSelectMode {
      ALWAYS("Always"),
      SERVER("Server");

      private final String name;

      public String toString() {
         return this.name;
      }

      @Generated
      private HitSelectMode(final String name) {
         this.name = name;
      }

      // $FF: synthetic method
      private static AuraModule.HitSelectMode[] $values() {
         return new AuraModule.HitSelectMode[]{ALWAYS, SERVER};
      }
   }
}
