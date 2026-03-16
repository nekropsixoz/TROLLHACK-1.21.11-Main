package ru.noxium.module.impl.combat.auraProcess;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Hand;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import ru.noxium.module.impl.combat.HitAura;
import ru.noxium.module.impl.combat.auraProcess.auraUtil.AuraUtil;
import ru.noxium.module.impl.combat.auraProcess.auraUtil.RayTraceUtil;
import ru.noxium.util.other.IMinecraft;
import ru.noxium.util.other.StopWatch;

@Environment(EnvType.CLIENT)
public final class Attack implements IMinecraft {
   private static final SecureRandom secureRandom = new SecureRandom();
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   public static long hitCounterCPSBypass;
   public static int randomInt1PosibleOrNotOnHit = -1;
   private static final StopWatch cooldownTimer = new StopWatch();
   private static boolean missDetected;
   private static int counterTo0PostMissHits;

   public static void hitCounterCPSBypassNext() {
      hitCounterCPSBypass++;
   }

   public static void hitCounterCPSBypassReset() {
      hitCounterCPSBypass = 0L;
   }

   public static boolean cpsBypassTrigger() {
      return hitCounterCPSBypass % 7L == 3L;
   }

   public static PlayerEntity getSelf() {
      return mc.player;
   }

   public static World getWorld() {
      return mc.world;
   }

   public static float applyGaussianJitter(float rotation) {
      float strength = 0.2F;
      return (float)(rotation + (secureRandom.nextGaussian() * 0.2F * 2.0 - 0.2F));
   }

   public static boolean randomBoolean(int chance010) {
      return secureRandom.nextInt(chance010 + 1) >= 1.0F * (1.0F / Math.max((float)chance010, 1.0F));
   }

   public static boolean randomBoolean() {
      return secureRandom.nextInt(2) == 1;
   }

   public static float randomFloat(float min, float max) {
      return secureRandom.nextFloat(min, max);
   }

   public static float randomFloat() {
      return randomFloat(0.0F, 1.0F);
   }

   public static int randomInt1PosibleOrNot() {
      return randomBoolean() ? 1 : -1;
   }

   public static int getAxeSlot() {
      if (mc.player == null) {
         return -1;
      } else {
         for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() instanceof AxeItem) {
               return i;
            }
         }

         return -1;
      }
   }

   public static Runnable[] hitShieldBreakTaskForUse(LivingEntity livingIn, boolean enabled) {
      Runnable[] pre$post = new Runnable[]{() -> {}, () -> {}};
      if (enabled && mc.player != null) {
         if (livingIn instanceof PlayerEntity player) {
            if (!player.isBlocking()) {
               return pre$post;
            }

            ItemStack main = player.getMainHandStack();
            ItemStack off = player.getOffHandStack();
            Item mainItem = main.isEmpty() ? null : main.getItem();
            Item offItem = off.isEmpty() ? null : off.getItem();
            if (mainItem == Items.SHIELD || offItem == Items.SHIELD) {
               int handSlot = mc.player.getInventory().getSelectedSlot();
               int slot;
               if ((slot = getAxeSlot()) != -1 && slot != handSlot) {
                  pre$post[0] = () -> {
                     if (mc.getNetworkHandler() != null) {
                        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                     }
                  };
                  pre$post[1] = () -> {
                     if (mc.getNetworkHandler() != null) {
                        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(handSlot));
                     }
                  };
               }
            }
         }

         return pre$post;
      } else {
         return pre$post;
      }
   }

   public static Runnable[] resetShieldSilentTaskForUse(boolean enabled) {
      Runnable[] pre$post = new Runnable[]{() -> {}, () -> {}};
      if (enabled && mc.player != null) {
         if (mc.player.isBlocking()) {
            Hand active = mc.player.getActiveHand();
            if (active == null) {
               return pre$post;
            }

            pre$post[0] = () -> mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
            pre$post[1] = () -> mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(active, 0, mc.player.getYaw(), mc.player.getPitch()));
         }

         return pre$post;
      } else {
         return pre$post;
      }
   }

   public static Runnable[] skipSilentSprintingTaskForUse(boolean enabled) {
      Runnable[] pre$post = new Runnable[]{() -> {}, () -> {}};
      if (enabled && mc.player != null) {
         if (mc.player.isSprinting() && !mc.player.isOnGround() && !mc.player.isSubmergedIn(FluidTags.WATER)) {
            pre$post[0] = () -> {
               mc.player.setSprinting(false);
               mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
            };
            pre$post[1] = () -> {
               mc.player.setSprinting(true);
               mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_SPRINTING));
            };
         }

         return pre$post;
      } else {
         return pre$post;
      }
   }

   public static double getYCapacityOnPlayerPos(int rangeY) {
      if (mc.world != null && mc.player != null) {
         Vec3d eyePos = mc.player.getEyePos();
         double minDst = rangeY * 2.0;
         double maxY = 320.0;
         double minY = -64.0;
         float selfWD2 = mc.player.getWidth() / 2.0F - 0.01F;

         for (Vec3d vec : Arrays.asList(
            eyePos.add(-selfWD2, 0.0, -selfWD2),
            eyePos.add(selfWD2, 0.0, selfWD2),
            eyePos.add(selfWD2, 0.0, -selfWD2),
            eyePos.add(-selfWD2, 0.0, selfWD2)
         )) {
            HitResult first = mc.world
               .raycast(new RaycastContext(vec, vec.add(0.0, -rangeY, 0.0), ShapeType.VISUAL, FluidHandling.ANY, mc.player));
            HitResult second = mc.world
               .raycast(new RaycastContext(vec, vec.add(0.0, rangeY, 0.0), ShapeType.VISUAL, FluidHandling.ANY, mc.player));
            if (maxY > second.getPos().y) {
               maxY = second.getPos().y;
            }

            if (minY < first.getPos().y) {
               minY = first.getPos().y;
            }

            double dst = maxY - minY;
            if (minDst > dst) {
               minDst = dst;
            }
         }

         return minDst - mc.player.getHeight();
      } else {
         return 1.0;
      }
   }

   public static double convenientFallOffset() {
      if (mc.player == null) {
         return 0.0;
      } else {
         double fallOffset = mc.player.fallDistance;
         if (mc.world != null && !mc.player.isOnGround() && mc.player.getVelocity().y < -0.0784000015258789) {
            boolean posLiquid = !mc.world.getFluidState(mc.player.getBlockPos()).isEmpty();
            boolean posUpLiquid = !mc.world.getFluidState(mc.player.getBlockPos().up()).isEmpty();
            if (!posLiquid && !posUpLiquid && mc.player.fallDistance < -mc.player.getVelocity().y && mc.player.age > 6) {
               fallOffset = -mc.player.getVelocity().y;
            }
         }

         return fallOffset;
      }
   }

   public static boolean isBestMomentToHit(boolean fallCheck) {
      // В режиме 1.8.9 игнорируем проверку падения для закликивания
      if (HitAura.clickMode.is("1.8.9")) {
         return true;
      }
      
      if (fallCheck && mc.player != null) {
         float adaptiveFallValue = 0.0F;
         float maxFallOff = 0.2F;
         if (cpsBypassTrigger() && getYCapacityOnPlayerPos(2) > 0.20000005F) {
            adaptiveFallValue = maxFallOff;
         }

         boolean hasFall = convenientFallOffset() > adaptiveFallValue || getYCapacityOnPlayerPos(2) < 0.1F;
         if (hasFall) {
            return true;
         } else {
            boolean isInWeb = mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.COBWEB);
            boolean badLiquidMoment = !mc.player.isJumping() && (mc.player.isTouchingWater() || mc.player.isInLava())
               || mc.player.isSubmergedIn(FluidTags.WATER)
               || mc.player.isSubmergedIn(FluidTags.LAVA)
               || isInWeb;
            return badLiquidMoment
               || !mc.player.isJumping() && mc.player.age > 6 && HitAura.extraSettings.get("Умные криты")
               || mc.player.isClimbing()
               || mc.player.hasVehicle()
               || mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
               || mc.player.hasStatusEffect(StatusEffects.LEVITATION)
               || mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
               || mc.player.getAbilities().flying;
         }
      } else {
         return true;
      }
   }

   public static boolean useEntity(LivingEntity livingIn, Runnable preHit, Runnable postHit, Hand hand, boolean cpsBypass) {
      if (preHit != null) {
         preHit.run();
      }

      if (livingIn != null && mc.interactionManager != null && mc.player != null) {
         mc.interactionManager.attackEntity(mc.player, livingIn);
         if (hand != null) {
            mc.player.swingHand(hand);
         }

         if (cpsBypass) {
            hitCounterCPSBypassNext();
         } else {
            hitCounterCPSBypassReset();
         }

         cooldownTimer.reset();
      }

      if (postHit != null) {
         postHit.run();
      }

      return livingIn != null;
   }

   public static long getMsCooldown() {
      if (mc.player == null) {
         return 500L;
      } else {
         // Режим 1.8.9 - закликивание с заданным CPS
         if (HitAura.clickMode.is("1.8.9")) {
            float cpsValue = HitAura.cps.get();
            long baseCooldown = (long)(1000.0F / cpsValue);
            // Добавляем небольшую рандомизацию для естественности
            long randomOffset = ThreadLocalRandom.current().nextLong(-20L, 20L);
            return Math.max(50L, baseCooldown + randomOffset);
         }
         
         // Режим 1.9 - стандартная логика с cooldown
         double attackSpeed = mc.player.getAttributeValue(EntityAttributes.ATTACK_SPEED);
         float maxDeviation = 0.2F;
         long msCooldown2 = (long)(1.0 / attackSpeed * 1000.0 * (1.0F - Math.min(maxDeviation, 1.0F)));
         long msCooldown;
         if (HitAura.attackDelay.is("Динамичный")) {
            msCooldown = msCooldown2 + ThreadLocalRandom.current().nextLong(10L, 50L);
         } else {
            msCooldown = msCooldown2 + 40L;
         }

         return msCooldown;
      }
   }

   public static boolean msCooldownReached(long msOffset) {
      return cooldownTimer.finished(getMsCooldown() + msOffset);
   }

   public static boolean msCooldownReached() {
      return msCooldownReached(0L);
   }

   public static boolean msCooldownHasMs(long ms) {
      return cooldownTimer.finished(ms);
   }

   public static float msCooldownPC01() {
      return Math.min((float)cooldownTimer.elapsedTime() / (float)getMsCooldown(), 1.0F);
   }

   public static float msCooldownReach() {
      return (float)cooldownTimer.elapsedTime();
   }

   public static boolean anyEntityOnRay(LivingEntity livingIn, double range) {
      return livingIn != null
         && RayTraceUtil.rayTraceEntity(MathHelper.wrapDegrees(mc.player.getYaw()), mc.player.getPitch(), (float)range, livingIn);
   }

   public static boolean shouldAttack(LivingEntity livingTarget, boolean rayCast, boolean distanceCheck, boolean fallCheck, long cooldownMSOffset, float[] ranges) {
      if (distanceCheck && livingTarget != null && !AuraUtil.validDistance(livingTarget, ranges[0], true)) {
         return false;
      } else if (!msCooldownReached(cooldownMSOffset)) {
         return false;
      } else {
         boolean validNext = isBestMomentToHit(fallCheck);
         if (validNext && rayCast && !anyEntityOnRay(livingTarget, ranges[0])) {
            validNext = false;
         }

         return validNext;
      }
   }

   public static boolean shouldAttack(LivingEntity livingTarget, boolean rayCast, boolean fallCheck, long cooldownMSOffset, float[] ranges) {
      return shouldAttack(livingTarget, rayCast, true, fallCheck, cooldownMSOffset, ranges);
   }

   public static boolean resetSprintTickP(LivingEntity targetIn, float[] ranges) {
      return targetIn != null
         && shouldAttack(targetIn, false, false, -80L, ranges)
         && !mc.player.isOnGround()
         && !mc.player.isSubmergedIn(FluidTags.WATER)
         && mc.player.getVelocity().y <= 0.0030162615090425808;
   }

   public static boolean resetSprintTick(LivingEntity targetIn, float[] ranges) {
      return targetIn != null
         && shouldAttack(targetIn, false, false, -60L, ranges)
         && !mc.player.isOnGround()
         && !mc.player.isSubmergedIn(FluidTags.WATER)
         && mc.player.getVelocity().y <= 0.16477328182606651;
   }

   private static int maxHitsCountOnMiss() {
      return 3;
   }

   public static void antiMissesHittingReset() {
      missDetected = false;
      counterTo0PostMissHits = 0;
   }

   public static void antiMissesHittingUpdate(LivingEntity targetIn, boolean cpsBypass, boolean rayCastCheck, boolean enabled) {
      if (targetIn == null || counterTo0PostMissHits == 0 || !enabled || targetIn.hurtTime != 0) {
         antiMissesHittingReset();
      }

      if (enabled && targetIn != null && msCooldownHasMs(cpsBypassTrigger() ? 250L : 150L) && mc.player.handSwinging) {
         if (!missDetected && counterTo0PostMissHits == 0 && targetIn.hurtTime == 0) {
            missDetected = true;
            counterTo0PostMissHits = maxHitsCountOnMiss();
         }

         if (missDetected
            && counterTo0PostMissHits > 0
            && targetIn != null
            && (!rayCastCheck || anyEntityOnRay(targetIn, 6.0))
            && useEntity(targetIn, () -> {}, () -> {}, Hand.MAIN_HAND, cpsBypass)) {
            counterTo0PostMissHits--;
         }
      }
   }

   @Generated
   private Attack() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   @Generated
   public static StopWatch getCooldownTimer() {
      return cooldownTimer;
   }
}
