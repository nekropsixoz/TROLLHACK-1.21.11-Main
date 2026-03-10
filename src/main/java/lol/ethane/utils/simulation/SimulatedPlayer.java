package lol.ethane.utils.simulation;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import lol.ethane.event.EventDispatcher;
import lol.ethane.event.defined.player.PlayerSafeWalkEvent;
import net.minecraft.class_10185;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1320;
import net.minecraft.class_1657;
import net.minecraft.class_1690;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2399;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2533;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3481;
import net.minecraft.class_3486;
import net.minecraft.class_3532;
import net.minecraft.class_3610;
import net.minecraft.class_3611;
import net.minecraft.class_5134;
import net.minecraft.class_5635;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_744;
import net.minecraft.class_746;
import net.minecraft.class_2338.class_2339;

public class SimulatedPlayer implements PlayerSimulation {
   private static final double STEP_HEIGHT = 0.5D;
   private final class_1657 player;
   public SimulatedPlayer.SimulatedPlayerInput input;
   public class_243 pos;
   public class_243 deltaMovement;
   public class_238 boundingBox;
   public float yRot;
   public float xRot;
   public boolean isSprinting;
   public double fallDistance;
   private int jumpingCooldown;
   private boolean isJumping;
   private boolean isFallFlying;
   public boolean onGround;
   public boolean horizontalCollision;
   private boolean verticalCollision;
   private boolean wasTouchingWater;
   private boolean isSwimming;
   private boolean wasUnderwater;
   private final Object2DoubleMap<class_6862<class_3611>> fluidHeight;
   private final Set<class_6862<class_3611>> fluidOnEyes;
   private int simulatedTicks = 0;
   private boolean clipLedged = false;

   public SimulatedPlayer(class_1657 player, SimulatedPlayer.SimulatedPlayerInput input, class_243 pos, class_243 deltaMovement, class_238 boundingBox, float yRot, float xRot, boolean isSprinting, double fallDistance, int jumpingCooldown, boolean isJumping, boolean isFallFlying, boolean onGround, boolean horizontalCollision, boolean verticalCollision, boolean wasTouchingWater, boolean isSwimming, boolean wasUnderwater, Object2DoubleMap<class_6862<class_3611>> fluidHeight, Set<class_6862<class_3611>> fluidOnEyes) {
      this.player = player;
      this.input = input;
      this.pos = pos;
      this.deltaMovement = deltaMovement;
      this.boundingBox = boundingBox;
      this.yRot = yRot;
      this.xRot = xRot;
      this.isSprinting = isSprinting;
      this.fallDistance = fallDistance;
      this.jumpingCooldown = jumpingCooldown;
      this.isJumping = isJumping;
      this.isFallFlying = isFallFlying;
      this.onGround = onGround;
      this.horizontalCollision = horizontalCollision;
      this.verticalCollision = verticalCollision;
      this.wasTouchingWater = wasTouchingWater;
      this.isSwimming = isSwimming;
      this.wasUnderwater = wasUnderwater;
      this.fluidHeight = fluidHeight;
      this.fluidOnEyes = fluidOnEyes;
   }

   public static SimulatedPlayer fromClientPlayer(SimulatedPlayer.SimulatedPlayerInput input) {
      class_746 player = class_310.method_1551().field_1724;

      assert player != null;

      return new SimulatedPlayer(player, input, player.method_73189(), player.method_18798(), player.method_5829(), player.method_36454(), player.method_36455(), player.method_5624(), player.field_6017, ((lol.ethane.mixin.accessor.LivingEntityAccessor)player).getStuckArrowCount(), ((lol.ethane.mixin.accessor.LivingEntityAccessor)player).getJumping(), player.method_6128(), player.method_24828(), player.field_5976, player.field_5992, ((lol.ethane.mixin.accessor.EntityAccessor)player).getNoClip(), player.method_5681(), ((lol.ethane.mixin.accessor.PlayerEntityAccessor)player).getReducedDebugInfo(), new Object2DoubleArrayMap(((lol.ethane.mixin.accessor.EntityAccessor)player).getFluidHeight()), new HashSet(((lol.ethane.mixin.accessor.EntityAccessor)player).getCollidingBlockPos()));
   }

   public static SimulatedPlayer fromOtherPlayer(class_1657 player, SimulatedPlayer.SimulatedPlayerInput input) {
      return new SimulatedPlayer(player, input, player.method_73189(), player.method_73189().method_1023(player.field_6014, player.field_6036, player.field_5969), player.method_5829(), player.method_36454(), player.method_36455(), player.method_5624(), player.field_6017, ((lol.ethane.mixin.accessor.LivingEntityAccessor)player).getStuckArrowCount(), ((lol.ethane.mixin.accessor.LivingEntityAccessor)player).getJumping(), player.method_6128(), player.method_24828(), player.field_5976, player.field_5992, ((lol.ethane.mixin.accessor.EntityAccessor)player).getNoClip(), player.method_5681(), ((lol.ethane.mixin.accessor.PlayerEntityAccessor)player).getReducedDebugInfo(), new Object2DoubleArrayMap(((lol.ethane.mixin.accessor.EntityAccessor)player).getFluidHeight()), new HashSet(((lol.ethane.mixin.accessor.EntityAccessor)player).getCollidingBlockPos()));
   }

   public class_243 getPos() {
      return this.pos;
   }

   public boolean isClipLedged() {
      return this.clipLedged;
   }

   public void tick() {
      this.clipLedged = false;
      if (!(this.pos.field_1351 <= -70.0D)) {
         this.input.update();
         this.checkWaterState();
         this.updateSubmergedInWaterState();
         this.updateSwimming();
         if (this.jumpingCooldown > 0) {
            --this.jumpingCooldown;
         }

         this.isJumping = this.input.field_54155.comp_3163();
         class_243 d = this.deltaMovement;
         double h = d.field_1352;
         double i = d.field_1351;
         double j = d.field_1350;
         if (Math.abs(d.field_1352) < 0.003D) {
            h = 0.0D;
         }

         if (Math.abs(d.field_1351) < 0.003D) {
            i = 0.0D;
         }

         if (Math.abs(d.field_1350) < 0.003D) {
            j = 0.0D;
         }

         if (this.onGround) {
            this.isFallFlying = false;
         }

         this.deltaMovement = new class_243(h, i, j);
         double k;
         if (this.isJumping) {
            k = this.isInLava() ? this.getFluidHeight(class_3486.field_15518) : this.getFluidHeight(class_3486.field_15517);
            boolean bl = this.isTouchingWater() && k > 0.0D;
            double swimHeight = this.getSwimHeight();
            if (bl && (!this.onGround || k > swimHeight)) {
               this.swimUpward(class_3486.field_15517);
            } else if (this.isInLava() && (!this.onGround || k > swimHeight)) {
               this.swimUpward(class_3486.field_15518);
            } else if ((this.onGround || bl && k <= swimHeight) && this.jumpingCooldown == 0) {
               this.jump();
               this.jumpingCooldown = 10;
            }
         }

         k = (double)this.input.method_3128().field_1343 * 0.98D;
         double forwardSpeed = (double)this.input.method_3128().field_1342 * 0.98D;
         double upwardsSpeed = 0.0D;
         if (this.hasStatusEffect(class_1294.field_5906) || this.hasStatusEffect(class_1294.field_5902)) {
            this.onLanding();
         }

         this.travel(new class_243(k, upwardsSpeed, forwardSpeed));
         ++this.simulatedTicks;
      }
   }

   private void travel(class_243 movementInput) {
      double beforeTravelVelocityY;
      double d;
      if (this.isSwimming && !this.player.method_5765()) {
         beforeTravelVelocityY = this.getRotationVector().field_1351;
         d = beforeTravelVelocityY < -0.2D ? 0.085D : 0.06D;
         if (beforeTravelVelocityY <= 0.0D || this.input.field_54155.comp_3163() || !this.player.method_73183().method_8320(class_2338.method_49637(this.pos.field_1352, this.pos.field_1351 + 1.0D - 0.1D, this.pos.field_1350)).method_26227().method_15769()) {
            this.deltaMovement = this.deltaMovement.method_1031(0.0D, (beforeTravelVelocityY - this.deltaMovement.field_1351) * d, 0.0D);
         }
      }

      beforeTravelVelocityY = this.deltaMovement.field_1351;
      d = 0.08D;
      boolean bl = this.deltaMovement.field_1351 <= 0.0D;
      if (this.deltaMovement.field_1351 <= 0.0D && this.hasStatusEffect(class_1294.field_5906)) {
         d = 0.01D;
         this.onLanding();
      }

      double k;
      float f;
      if (this.isTouchingWater() && this.player.method_29920()) {
         k = this.pos.field_1351;
         f = this.isSprinting ? 0.9F : 0.8F;
         float g = 0.02F;
         f = (float)this.getAttributeValue(class_5134.field_51578);
         if (!this.onGround) {
            f *= 0.5F;
         }

         if (f > 0.0F) {
            f += (0.54600006F - f) * f / 3.0F;
            g += (this.getMovementSpeed() - g) * f / 3.0F;
         }

         if (this.hasStatusEffect(class_1294.field_5900)) {
            f = 0.96F;
         }

         this.updateVelocity(g, movementInput);
         this.move(this.deltaMovement);
         class_243 vec3d = this.deltaMovement;
         if (this.horizontalCollision && this.isClimbing()) {
            vec3d = new class_243(vec3d.field_1352, 0.2D, vec3d.field_1350);
         }

         this.deltaMovement = vec3d.method_18805((double)f, 0.8D, (double)f);
         class_243 vec3d2 = this.player.method_26317(d, bl, this.deltaMovement);
         this.deltaMovement = vec3d2;
         if (this.horizontalCollision && this.doesNotCollide(vec3d2.field_1352, vec3d2.field_1351 + 0.6D - this.pos.field_1351 + k, vec3d2.field_1350)) {
            this.deltaMovement = new class_243(vec3d2.field_1352, 0.3D, vec3d2.field_1350);
         }
      } else if (this.isInLava() && this.player.method_29920()) {
         k = this.pos.field_1351;
         this.updateVelocity(0.02F, movementInput);
         this.move(this.deltaMovement);
         if (this.getFluidHeight(class_3486.field_15518) <= this.getSwimHeight()) {
            this.deltaMovement = this.deltaMovement.method_18805(0.5D, 0.8D, 0.5D);
            this.deltaMovement = this.player.method_26317(d, bl, this.deltaMovement);
         } else {
            this.deltaMovement = this.deltaMovement.method_1021(0.5D);
         }

         if (!this.player.method_5740()) {
            this.deltaMovement = this.deltaMovement.method_1031(0.0D, -d / 4.0D, 0.0D);
         }

         if (this.horizontalCollision && this.doesNotCollide(this.deltaMovement.field_1352, this.deltaMovement.field_1351 + 0.6D - this.pos.field_1351 + k, this.deltaMovement.field_1350)) {
            this.deltaMovement = new class_243(this.deltaMovement.field_1352, 0.3D, this.deltaMovement.field_1350);
         }
      } else {
         class_243 vec3d3;
         if (this.isFallFlying) {
            class_243 e = this.deltaMovement;
            if (e.field_1351 > -0.5D) {
               this.fallDistance = 1.0D;
            }

            vec3d3 = this.getRotationVector();
            f = this.xRot * 0.017453292F;
            double g = Math.sqrt(vec3d3.field_1352 * vec3d3.field_1352 + vec3d3.field_1350 * vec3d3.field_1350);
            double vec3d = e.method_37267();
            double i = vec3d3.method_1033();
            float j = class_3532.method_15362((double)f);
            j = (float)((double)j * (double)j * Math.min(1.0D, i / 0.4D));
            e = this.deltaMovement.method_1031(0.0D, d * (-1.0D + (double)j * 0.75D), 0.0D);
            if (e.field_1351 < 0.0D && g > 0.0D) {
               k = e.field_1351 * -0.1D * (double)j;
               e = e.method_1031(vec3d3.field_1352 * k / g, k, vec3d3.field_1350 * k / g);
            }

            if (f < 0.0F && g > 0.0D) {
               k = vec3d * (double)(-class_3532.method_15374((double)f)) * 0.04D;
               e = e.method_1031(-vec3d3.field_1352 * k / g, k * 3.2D, -vec3d3.field_1350 * k / g);
            }

            if (g > 0.0D) {
               e = e.method_1031((vec3d3.field_1352 / g * vec3d - e.field_1352) * 0.1D, 0.0D, (vec3d3.field_1350 / g * vec3d - e.field_1350) * 0.1D);
            }

            this.deltaMovement = e.method_18805(0.99D, 0.98D, 0.99D);
            this.move(this.deltaMovement);
         } else {
            class_2338 blockPos = this.getVelocityAffectingPos();
            float p = this.player.method_73183().method_8320(blockPos).method_26204().method_9499();
            f = this.onGround ? p * 0.91F : 0.91F;
            vec3d3 = this.applyMovementInput(movementInput, p);
            double q = vec3d3.field_1351;
            if (this.hasStatusEffect(class_1294.field_5902)) {
               q += (0.05D * (double)(this.getStatusEffect(class_1294.field_5902).method_5578() + 1) - vec3d3.field_1351) * 0.2D;
            } else if (this.player.method_73183().method_8608() && !this.player.method_73183().method_22340(blockPos)) {
               q = this.pos.field_1351 > (double)this.player.method_73183().method_31607() ? -0.1D : 0.0D;
            } else if (!this.player.method_5740()) {
               q -= d;
            }

            if (this.player.method_35053()) {
               this.deltaMovement = new class_243(vec3d3.field_1352, q, vec3d3.field_1350);
            } else {
               this.deltaMovement = new class_243(vec3d3.field_1352 * (double)f, q * 0.9800000190734863D, vec3d3.field_1350 * (double)f);
            }
         }
      }

      if (this.player.method_31549().field_7479 && !this.player.method_5765()) {
         this.deltaMovement = new class_243(this.deltaMovement.field_1352, beforeTravelVelocityY * 0.6D, this.deltaMovement.field_1350);
         this.onLanding();
      }

   }

   private class_243 applyMovementInput(class_243 movementInput, float slipperiness) {
      this.updateVelocity(this.getMovementSpeed(slipperiness), movementInput);
      this.deltaMovement = this.applyClimbingSpeed(this.deltaMovement);
      this.deltaMovement = this.applyWebSpeed(this.deltaMovement);
      this.move(this.deltaMovement);
      class_243 vec3d = this.deltaMovement;
      if ((this.horizontalCollision || this.isJumping) && (this.isClimbing() || this.getBlockState(new class_2338((int)this.pos.field_1352, (int)this.pos.field_1351, (int)this.pos.field_1350)).method_27852(class_2246.field_27879) && class_5635.method_32355(this.player))) {
         vec3d = new class_243(vec3d.field_1352, 0.2D, vec3d.field_1350);
      }

      return vec3d;
   }

   private void updateVelocity(float speed, class_243 movementInput) {
      class_243 vec3d = ((lol.ethane.mixin.accessor.EntityAccessor)this).invokeGetMovementDirection(movementInput, speed, this.yRot);
      this.deltaMovement = this.deltaMovement.method_1019(vec3d);
   }

   private float getMovementSpeed(float slipperiness) {
      return this.onGround ? this.getMovementSpeed() * (0.21600002F / (slipperiness * slipperiness * slipperiness)) : this.getAirStrafingSpeed();
   }

   private float getAirStrafingSpeed() {
      float speed = 0.02F;
      return this.input.sprinting ? (float)((double)speed + 0.005999999865889549D) : speed;
   }

   private float getMovementSpeed() {
      return 0.1F;
   }

   private void move(class_243 input) {
      class_243 movement = this.adjustMovementForSneaking(input);
      class_243 adjustedMovement = this.adjustMovementForCollisions(movement);
      if (adjustedMovement.method_1027() > 1.0E-7D) {
         this.pos = this.pos.method_1019(adjustedMovement);
         this.boundingBox = this.player.method_18377(this.player.method_18376()).method_30757(this.pos);
      }

      boolean xCollision = !class_3532.method_20390(movement.field_1352, adjustedMovement.field_1352);
      boolean zCollision = !class_3532.method_20390(movement.field_1350, adjustedMovement.field_1350);
      this.horizontalCollision = xCollision || zCollision;
      this.verticalCollision = movement.field_1351 != adjustedMovement.field_1351;
      this.onGround = this.verticalCollision && movement.field_1351 < 0.0D;
      if (!this.isTouchingWater()) {
         this.checkWaterState();
      }

      if (this.onGround) {
         this.onLanding();
      } else if (movement.field_1351 < 0.0D) {
         this.fallDistance -= movement.field_1351;
      }

      class_243 vec3d2 = this.deltaMovement;
      if (this.horizontalCollision || this.verticalCollision) {
         this.deltaMovement = new class_243(xCollision ? 0.0D : vec3d2.field_1352, this.onGround ? 0.0D : vec3d2.field_1351, zCollision ? 0.0D : vec3d2.field_1350);
      }

   }

   private class_243 adjustMovementForCollisions(class_243 movement) {
      class_238 box = (new class_238(-0.3D, 0.0D, -0.3D, 0.3D, 1.8D, 0.3D)).method_997(this.pos);
      List<class_265> entityCollisionList = Collections.emptyList();
      class_243 vec3d = movement.method_1027() == 0.0D ? movement : class_1297.method_20736(this.player, movement, box, this.player.method_73183(), entityCollisionList);
      boolean bl = movement.field_1352 != vec3d.field_1352;
      boolean bl2 = movement.field_1351 != vec3d.field_1351;
      boolean bl3 = movement.field_1350 != vec3d.field_1350;
      boolean bl4 = this.onGround || bl2 && movement.field_1351 < 0.0D;
      if (this.player.method_49476() > 0.0F && bl4 && (bl || bl3)) {
         class_243 vec3d2 = class_1297.method_20736(this.player, new class_243(movement.field_1352, (double)this.player.method_49476(), movement.field_1350), box, this.player.method_73183(), entityCollisionList);
         class_243 vec3d3 = class_1297.method_20736(this.player, new class_243(0.0D, (double)this.player.method_49476(), 0.0D), box.method_1012(movement.field_1352, 0.0D, movement.field_1350), this.player.method_73183(), entityCollisionList);
         class_243 asdf = class_1297.method_20736(this.player, new class_243(movement.field_1352, 0.0D, movement.field_1350), box.method_997(vec3d3), this.player.method_73183(), entityCollisionList).method_1019(vec3d3);
         if (vec3d3.field_1351 < (double)this.player.method_49476() && asdf.method_37268() > vec3d2.method_37268()) {
            vec3d2 = asdf;
         }

         if (vec3d2.method_37268() > vec3d.method_37268()) {
            return vec3d2.method_1019(class_1297.method_20736(this.player, new class_243(0.0D, -vec3d2.field_1351 + movement.field_1351, 0.0D), box.method_997(vec3d2), this.player.method_73183(), entityCollisionList));
         }
      }

      return vec3d;
   }

   private void onLanding() {
      this.fallDistance = 0.0D;
   }

   public void jump() {
      this.deltaMovement = this.deltaMovement.method_1031(0.0D, (double)this.getJumpVelocity() - this.deltaMovement.field_1351, 0.0D);
      if (this.isSprinting) {
         float f = this.yRot * 0.017453292F;
         this.deltaMovement = this.deltaMovement.method_1031((double)(-class_3532.method_15374((double)f) * 0.2F), 0.0D, (double)(class_3532.method_15362((double)f) * 0.2F));
      }

   }

   private class_243 applyClimbingSpeed(class_243 motion) {
      if (!this.isClimbing()) {
         return motion;
      } else {
         this.onLanding();
         double d = class_3532.method_15350(motion.field_1352, -0.15000000596046448D, 0.15000000596046448D);
         double e = class_3532.method_15350(motion.field_1350, -0.15000000596046448D, 0.15000000596046448D);
         double g = Math.max(motion.field_1351, -0.15000000596046448D);
         if (g < 0.0D && !this.getBlockState(class_2338.method_49637(this.pos.field_1352, this.pos.field_1351, this.pos.field_1350)).method_27852(class_2246.field_16492) && this.player.method_21754()) {
            g = 0.0D;
         }

         return new class_243(d, g, e);
      }
   }

   private class_243 applyWebSpeed(class_243 motion) {
      class_2680 blockState = class_310.method_1551().field_1687.method_8320(class_2338.method_49637(this.pos.field_1352, this.pos.field_1351, this.pos.field_1350));
      if (blockState.method_26204() != class_2246.field_10343) {
         return motion;
      } else {
         class_243 multiplier = this.hasStatusEffect(class_1294.field_50119) ? new class_243(0.5D, 0.25D, 0.5D) : new class_243(0.25D, 0.05D, 0.25D);
         return motion.method_18805(multiplier.field_1352, multiplier.field_1351, multiplier.field_1350);
      }
   }

   private boolean isClimbing() {
      class_2338 blockPos = class_2338.method_49637(this.pos.field_1352, this.pos.field_1351, this.pos.field_1350);
      class_2680 blockState = this.getBlockState(blockPos);
      if (blockState.method_26164(class_3481.field_22414)) {
         return true;
      } else {
         return blockState.method_26204() instanceof class_2533 && this.canEnterTrapdoor(blockPos, blockState);
      }
   }

   private boolean canEnterTrapdoor(class_2338 pos, class_2680 state) {
      if (!(Boolean)state.method_11654(class_2533.field_11631)) {
         return false;
      } else {
         class_2680 blockState = this.player.method_73183().method_8320(pos.method_10074());
         return blockState.method_27852(class_2246.field_9983) && blockState.method_11654(class_2399.field_11253) == state.method_11654(class_2533.field_11177);
      }
   }

   private class_243 adjustMovementForSneaking(class_243 movement) {
      if (movement.field_1351 <= 0.0D && this.isAboveGround()) {
         double d = movement.field_1352;
         double e = movement.field_1350;

         while(true) {
            while(d != 0.0D && class_310.method_1551().field_1687.method_8587(this.player, this.boundingBox.method_989(d, -0.5D, 0.0D))) {
               if (d < 0.05D && d >= -0.05D) {
                  d = 0.0D;
               } else if (d > 0.0D) {
                  d -= 0.05D;
               } else {
                  d += 0.05D;
               }
            }

            while(true) {
               while(e != 0.0D && class_310.method_1551().field_1687.method_8587(this.player, this.boundingBox.method_989(0.0D, -0.5D, e))) {
                  if (e < 0.05D && e >= -0.05D) {
                     e = 0.0D;
                  } else if (e > 0.0D) {
                     e -= 0.05D;
                  } else {
                     e += 0.05D;
                  }
               }

               while(true) {
                  while(d != 0.0D && e != 0.0D && class_310.method_1551().field_1687.method_8587(this.player, this.boundingBox.method_989(d, -0.5D, e))) {
                     if (d < 0.05D && d >= -0.05D) {
                        d = 0.0D;
                     } else if (d > 0.0D) {
                        d -= 0.05D;
                     } else {
                        d += 0.05D;
                     }

                     if (e < 0.05D && e >= -0.05D) {
                        e = 0.0D;
                     } else if (e > 0.0D) {
                        e -= 0.05D;
                     } else {
                        e += 0.05D;
                     }
                  }

                  if (movement.field_1352 != d || movement.field_1350 != e) {
                     this.clipLedged = true;
                  }

                  if (this.shouldClipAtLedge()) {
                     movement = new class_243(d, movement.field_1351, e);
                  }

                  return movement;
               }
            }
         }
      } else {
         return movement;
      }
   }

   private boolean shouldClipAtLedge() {
      return !this.input.ignoreClippingAtLedge && (this.input.field_54155.comp_3164() || this.input.forceSafeWalk);
   }

   private boolean isAboveGround() {
      return this.onGround || this.fallDistance < 0.5D && !class_310.method_1551().field_1687.method_8587(this.player, this.boundingBox.method_989(0.0D, this.fallDistance - 0.5D, 0.0D));
   }

   private float getJumpVelocity() {
      return 0.42F * this.getJumpVelocityMultiplier() + this.getJumpBoostVelocityModifier();
   }

   private float getJumpBoostVelocityModifier() {
      return this.hasStatusEffect(class_1294.field_5913) ? 0.1F * (float)(this.getStatusEffect(class_1294.field_5913).method_5578() + 1) : 0.0F;
   }

   private float getJumpVelocityMultiplier() {
      float f = this.getBlockState(class_2338.method_49637(this.pos.field_1352, this.pos.field_1351, this.pos.field_1350)).method_26204().method_23350();
      float g = this.getBlockState(this.getVelocityAffectingPos()).method_26204().method_23350();
      return (double)f == 1.0D ? g : f;
   }

   private boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
      return this.doesNotCollide(this.boundingBox.method_989(offsetX, offsetY, offsetZ));
   }

   private boolean doesNotCollide(class_238 box) {
      return this.player.method_73183().method_8587(this.player, box) && !this.player.method_73183().method_22345(box);
   }

   private void swimUpward(class_6862<class_3611> fluid) {
      this.deltaMovement = this.deltaMovement.method_1031(0.0D, fluid == class_3486.field_15517 ? 0.03999999910593033D : 0.005999999865889549D, 0.0D);
   }

   private class_2338 getVelocityAffectingPos() {
      return class_2338.method_49637(this.pos.field_1352, this.boundingBox.field_1322 - 0.5000001D, this.pos.field_1350);
   }

   private double getSwimHeight() {
      return (double)this.player.method_5751() < 0.4D ? 0.0D : 0.4D;
   }

   private boolean isTouchingWater() {
      return this.wasTouchingWater;
   }

   private boolean isInLava() {
      return this.getFluidHeight(class_3486.field_15518) > 0.0D;
   }

   private void checkWaterState() {
      class_1297 var2 = this.player.method_5854();
      if (var2 instanceof class_1690) {
         class_1690 boat = (class_1690)var2;
         if (!boat.method_5869()) {
            this.wasTouchingWater = false;
            return;
         }
      }

      if (this.updateMovementInFluid(class_3486.field_15517, 0.014D)) {
         this.onLanding();
         this.wasTouchingWater = true;
      } else {
         this.wasTouchingWater = false;
      }

   }

   private void updateSwimming() {
      if (this.isSwimming) {
         this.isSwimming = this.isSprinting && this.isTouchingWater() && !this.player.method_5765();
      } else {
         this.isSwimming = this.isSprinting && this.isSubmergedInWater() && !this.player.method_5765() && this.player.method_73183().method_8316(class_2338.method_49637(this.pos.field_1352, this.pos.field_1351, this.pos.field_1350)).method_15767(class_3486.field_15517);
      }

   }

   private void updateSubmergedInWaterState() {
      this.wasUnderwater = this.fluidOnEyes.contains(class_3486.field_15517);
      this.fluidOnEyes.clear();
      double d = this.getEyeY() - 0.1111111119389534D;
      class_1297 entity = this.player.method_5854();
      if (entity instanceof class_1690) {
         class_1690 boat = (class_1690)entity;
         if (!boat.method_5869() && boat.method_5829().field_1325 >= d && boat.method_5829().field_1322 <= d) {
            return;
         }
      }

      class_2338 blockPos = class_2338.method_49637(this.pos.field_1352, d, this.pos.field_1350);
      class_3610 fluidState = this.player.method_73183().method_8316(blockPos);
      double e = (double)((float)blockPos.method_10264() + fluidState.method_15763(this.player.method_73183(), blockPos));
      if (e > d) {
         Stream var10000 = fluidState.method_40181();
         Set var10001 = this.fluidOnEyes;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::add);
      }

   }

   private double getEyeY() {
      return this.pos.field_1351 + (double)this.player.method_5751();
   }

   private boolean isSubmergedInWater() {
      return this.wasUnderwater && this.isTouchingWater();
   }

   private double getFluidHeight(class_6862<class_3611> tags) {
      return this.fluidHeight.getDouble(tags);
   }

   private boolean updateMovementInFluid(class_6862<class_3611> tag, double speed) {
      if (this.isRegionUnloaded()) {
         return false;
      } else {
         class_238 box = this.boundingBox.method_1011(0.001D);
         int i = class_3532.method_15357(box.field_1323);
         int j = class_3532.method_15384(box.field_1320);
         int k = class_3532.method_15357(box.field_1322);
         int l = class_3532.method_15384(box.field_1325);
         int m = class_3532.method_15357(box.field_1321);
         int n = class_3532.method_15384(box.field_1324);
         double d = 0.0D;
         boolean bl2 = false;
         class_243 vec3d = class_243.field_1353;
         int o = 0;
         class_2339 mutable = new class_2339();

         for(int p = i; p < j; ++p) {
            for(int q = k; q < l; ++q) {
               for(int r = m; r < n; ++r) {
                  mutable.method_10103(p, q, r);
                  class_3610 fluidState = this.player.method_73183().method_8316(mutable);
                  if (fluidState.method_15767(tag)) {
                     double e = (double)((float)q + fluidState.method_15763(this.player.method_73183(), mutable));
                     if (e >= box.field_1322) {
                        bl2 = true;
                        d = Math.max(e - box.field_1322, d);
                        class_243 vec3d2 = fluidState.method_15758(this.player.method_73183(), mutable);
                        if (d < 0.4D) {
                           vec3d2 = vec3d2.method_1021(d);
                        }

                        vec3d = vec3d.method_1019(vec3d2);
                        ++o;
                     }
                  }
               }
            }
         }

         if (vec3d.method_1033() > 0.0D) {
            if (o > 0) {
               vec3d = vec3d.method_1021(1.0D / (double)o);
            }

            class_243 vec3d3 = this.deltaMovement;
            vec3d = vec3d.method_1021(speed);
            if (Math.abs(vec3d3.field_1352) < 0.003D && Math.abs(vec3d3.field_1350) < 0.003D && vec3d.method_1033() < 0.0045000000000000005D) {
               vec3d = vec3d.method_1029().method_1021(0.0045000000000000005D);
            }

            this.deltaMovement = this.deltaMovement.method_1019(vec3d);
         }

         this.fluidHeight.put(tag, d);
         return bl2;
      }
   }

   private boolean isRegionUnloaded() {
      class_238 box = this.boundingBox.method_1014(1.0D);
      int i = class_3532.method_15357(box.field_1323);
      int j = class_3532.method_15384(box.field_1320);
      int k = class_3532.method_15357(box.field_1321);
      int l = class_3532.method_15384(box.field_1324);
      return !this.player.method_73183().method_33597(i, k, j, l);
   }

   private class_243 getRotationVector() {
      return this.getRotationVector(this.xRot, this.yRot);
   }

   private class_243 getRotationVector(float pitch, float yaw) {
      float f = pitch * 0.017453292F;
      float g = -yaw * 0.017453292F;
      float h = class_3532.method_15362((double)g);
      float i = class_3532.method_15374((double)g);
      float j = class_3532.method_15362((double)f);
      float k = class_3532.method_15374((double)f);
      return new class_243((double)(i * j), (double)(-k), (double)(h * j));
   }

   private boolean hasStatusEffect(class_6880<class_1291> effect) {
      class_1293 instance = this.player.method_6112(effect);
      if (instance == null) {
         return false;
      } else {
         return instance.method_5584() >= this.simulatedTicks;
      }
   }

   private class_1293 getStatusEffect(class_6880<class_1291> effect) {
      class_1293 instance = this.player.method_6112(effect);
      if (instance == null) {
         return null;
      } else {
         return instance.method_5584() < this.simulatedTicks ? null : instance;
      }
   }

   public double getAttributeValue(class_6880<class_1320> attribute) {
      return this.player.method_6127().method_26852(attribute);
   }

   public class_2680 getBlockState(class_2338 pos) {
      return this.player.method_73183().method_8320(pos);
   }

   public SimulatedPlayer copy() {
      return new SimulatedPlayer(this.player, this.input, this.pos, this.deltaMovement, this.boundingBox, this.yRot, this.xRot, this.isSprinting, this.fallDistance, this.jumpingCooldown, this.isJumping, this.isFallFlying, this.onGround, this.horizontalCollision, this.verticalCollision, this.wasTouchingWater, this.isSwimming, this.wasUnderwater, new Object2DoubleArrayMap(this.fluidHeight), new HashSet(this.fluidOnEyes));
   }

   public static class SimulatedPlayerInput extends class_744 {
      public final DirectionalInput directionalInput;
      public boolean sprinting;
      public boolean ignoreClippingAtLedge = false;
      public boolean forceSafeWalk = false;

      public SimulatedPlayerInput(DirectionalInput directionalInput, boolean jumping, boolean sprinting, boolean sneaking) {
         this.directionalInput = directionalInput;
         this.sprinting = sprinting;
         this.field_54155 = new class_10185(directionalInput.forwards(), directionalInput.backwards(), directionalInput.left(), directionalInput.right(), jumping, sneaking, sprinting);
      }

      public void update() {
         float forward = 0.0F;
         if (this.field_54155.comp_3159() != this.field_54155.comp_3160()) {
            forward = this.field_54155.comp_3159() ? 1.0F : -1.0F;
         }

         float sideways = 0.0F;
         if (this.field_54155.comp_3161() != this.field_54155.comp_3162()) {
            sideways = this.field_54155.comp_3161() ? 1.0F : -1.0F;
         }

         if (this.field_54155.comp_3164()) {
            sideways *= 0.3F;
            forward *= 0.3F;
         }

         this.field_55868 = new class_241(sideways, forward);
      }

      public static SimulatedPlayer.SimulatedPlayerInput fromClientPlayer(DirectionalInput directionalInput) {
         class_746 player = class_310.method_1551().field_1724;

         assert player != null;

         return fromClientPlayer(directionalInput, player.field_3913.field_54155.comp_3163(), player.method_5624(), player.method_5715());
      }

      public static SimulatedPlayer.SimulatedPlayerInput fromClientPlayer(DirectionalInput directionalInput, boolean jump, boolean sprinting, boolean sneaking) {
         SimulatedPlayer.SimulatedPlayerInput input = new SimulatedPlayer.SimulatedPlayerInput(directionalInput, jump, sprinting, sneaking);
         PlayerSafeWalkEvent safeWalkEvent = new PlayerSafeWalkEvent();
         EventDispatcher.dispatch(safeWalkEvent);
         if (safeWalkEvent.isSafeWalk()) {
            input.forceSafeWalk = true;
         }

         return input;
      }

      public static SimulatedPlayer.SimulatedPlayerInput guessInput(class_1657 entity) {
         class_243 velocity = entity.method_73189().method_1023(entity.field_6014, entity.field_6036, entity.field_5969);
         double horizontalVelocitySqr = velocity.method_37268();
         boolean sprinting = horizontalVelocitySqr >= 0.014641D;
         DirectionalInput input = DirectionalInput.NONE;
         if (horizontalVelocitySqr > 0.0025000000000000005D) {
            float velocityAngle = DirectionalInput.getDegreesRelativeToView(velocity, entity.method_36454());
            input = DirectionalInput.getDirectionalInputForDegrees(DirectionalInput.NONE, class_3532.method_15393(velocityAngle));
         }

         boolean jumping = !entity.method_24828();
         return new SimulatedPlayer.SimulatedPlayerInput(input, jumping, sprinting, entity.method_5715());
      }
   }
}
