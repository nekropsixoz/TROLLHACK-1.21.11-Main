package ru.noxium.module.impl.combat;

import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PlayerHeadItem;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import ru.noxium.event.EventInit;
import ru.noxium.event.lifecycle.ClientTickEvent;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.MultiBooleanSetting;
import ru.noxium.module.api.setting.impl.SliderSetting;
import ru.noxium.util.other.StopWatchP;
import ru.noxium.util.player.MoveUtil;
import ru.noxium.util.player.MovementManager;

@IModule(name = "Auto Totem", description = "Жозки авто свин", category = Category.Combat, bind = -1)
@Environment(EnvType.CLIENT)
public class AutoTotem extends Module {
   private final MultiBooleanSetting options = new MultiBooleanSetting(
         "Настройки",
         new BooleanSetting("Здоровье с элитрами", true),
         new BooleanSetting("Динамит", true),
         new BooleanSetting("Падение", false),
         new BooleanSetting("Эндер-кристалл", false));
   private final SliderSetting health = new SliderSetting("Здоровье", 4.0F, 1.0F, 20.0F, 0.5F, false);
   private final SliderSetting elytraHealth = new SliderSetting("Здоровье на элитре", 9.0F, 0.0F, 20.0F, 0.5F, false)
         .hidden(() -> !this.options.get("Здоровье с элитрами"));
   private final SliderSetting crystalDistance = new SliderSetting("Дистанция до кристалла", 4.0F, 1.0F, 10.0F, 1.0F,
         false)
         .hidden(() -> !this.options.get("Эндер-кристалл"));
   private final SliderSetting tntDistance = new SliderSetting("Дистанция до динамита", 30.0F, 3.0F, 50.0F, 1.0F, false)
         .hidden(() -> !this.options.get("Динамит"));
   private final BooleanSetting noBall = new BooleanSetting("Не свапать если шар", false);
   private int oldSlot = -1;
   private ItemStack oldOffhandItem = ItemStack.EMPTY;
   int nonEnchantedTotems;
   private final StopWatchP swapWatch = new StopWatchP();
   private boolean lockHeld;

   public AutoTotem() {
      this.addSettings(new Setting[] { this.options, this.health, this.elytraHealth, this.crystalDistance,
            this.tntDistance, this.noBall });
   }

   @EventInit
   public void update(ClientTickEvent eventUpdate) {
      if (mc.player != null && mc.player.isAlive() && mc.world != null) {
         this.nonEnchantedTotems = (int) IntStream.range(0, 36)
               .mapToObj(i -> mc.player.getInventory().getStack(i))
               .filter(s -> s.getItem() == Items.TOTEM_OF_UNDYING && !s.hasEnchantments())
               .count();
         this.bypass();
      } else {
         if (this.lockHeld) {
            MovementManager.getInstance().unlockMovement("AutoTotem");
            this.lockHeld = false;
            this.resetSwapBack();
         }
      }
   }

   private void bypass() {
      int slot = this.findNonEnchantedTotemSlot();
      boolean totemInHand = this.isTotemInHands();
      if (this.canSwap()) {
         if (slot >= 0 && !totemInHand) {
            if (!this.lockHeld && mc.currentScreen == null) {
               mc.options.forwardKey.setPressed(false);
               mc.player.setSprinting(false);
               mc.options.sprintKey.setPressed(false);
               if (!MoveUtil.isMoving()) {
                  if (this.oldOffhandItem.isEmpty() && !mc.player.getOffHandStack().isEmpty()) {
                     this.oldOffhandItem = mc.player.getOffHandStack().copy();
                     this.oldSlot = slot;
                  }

                  mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, slot, 40, SlotActionType.SWAP,
                        mc.player);
                  mc.player.networkHandler
                        .sendPacket(new CloseHandledScreenC2SPacket(mc.player.playerScreenHandler.syncId));
                  this.lockHeld = true;
                  this.swapWatch.reset();
               }
            }

            if (this.lockHeld && this.swapWatch.isReached(50L)) {
               MovementManager.getInstance().unlockMovement("AutoTotem");
               this.lockHeld = false;
               return;
            }
         }
      } else if (this.oldSlot != -1 && !this.oldOffhandItem.isEmpty()) {
         if (mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
            if (!this.lockHeld && mc.currentScreen == null) {
               mc.options.forwardKey.setPressed(false);
               mc.player.setSprinting(false);
               mc.options.sprintKey.setPressed(false);
               mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, this.oldSlot, 40,
                     SlotActionType.SWAP, mc.player);
               mc.player.networkHandler
                     .sendPacket(new CloseHandledScreenC2SPacket(mc.player.playerScreenHandler.syncId));
               this.lockHeld = true;
               this.swapWatch.reset();
            }

            if (this.lockHeld && this.swapWatch.isReached(50L)) {
               MovementManager.getInstance().unlockMovement("AutoTotem");
               this.lockHeld = false;
               this.resetSwapBack();
               return;
            }
         } else {
            this.resetSwapBack();
         }
      } else if (this.lockHeld) {
         MovementManager.getInstance().unlockMovement("AutoTotem");
         this.lockHeld = false;
         this.resetSwapBack();
      }
   }

   private void resetSwapBack() {
      this.oldOffhandItem = ItemStack.EMPTY;
      this.oldSlot = -1;
   }

   private int findNonEnchantedTotemSlot() {
      for (int i = 0; i < 36; i++) {
         ItemStack stack = mc.player.getInventory().getStack(i);
         if (stack.getItem() == Items.TOTEM_OF_UNDYING && !stack.hasEnchantments()) {
            return i < 9 ? i + 36 : i;
         }
      }

      for (int ix = 0; ix < 36; ix++) {
         ItemStack stack = mc.player.getInventory().getStack(ix);
         if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
            return ix < 9 ? ix + 36 : ix;
         }
      }

      return -1;
   }

   public boolean isTotemInHands() {
      ItemStack mainHand = mc.player.getMainHandStack();
      ItemStack offHand = mc.player.getOffHandStack();
      if (mainHand.getItem() == Items.TOTEM_OF_UNDYING) {
         return !mainHand.hasEnchantments() || this.nonEnchantedTotems <= 0;
      } else {
         return offHand.getItem() != Items.TOTEM_OF_UNDYING ? false
               : !offHand.hasEnchantments() || this.nonEnchantedTotems <= 0;
      }
   }

   private boolean canSwap() {
      boolean flag1 = this.elytraCheck();
      boolean flag2 = this.checkCrystal();
      boolean flag3 = this.checkTnt();
      boolean flag4 = this.checkFall();
      boolean flag6 = mc.player.getHealth() + this.getAbsorption() <= this.health.get();
      return flag1 || flag2 || flag3 || flag4 || flag6;
   }

   private boolean elytraCheck() {
      ItemStack chestStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
      boolean elytra = chestStack.getItem() == Items.ELYTRA && this.options.get("Здоровье с элитрами");
      return elytra && this.checkHealth();
   }

   private boolean checkFall() {
      return !this.options.get("Падение") ? false : mc.player.fallDistance > 2.0;
   }

   private boolean checkHealth() {
      return mc.player.getHealth() + this.getAbsorption() <= this.elytraHealth.get();
   }

   private boolean checkCrystal() {
      if (!this.options.get("Эндер-кристалл")) {
         return false;
      } else {
         for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity && mc.player.distanceTo(entity) <= this.crystalDistance.get()) {
               return !(mc.player.getOffHandStack().getItem() instanceof PlayerHeadItem) || !this.noBall.get();
            }
         }

         return false;
      }
   }

   private boolean checkTnt() {
      if (!this.options.get("Динамит")) {
         return false;
      } else {
         for (Entity entity : mc.world.getEntities()) {
            float distance = mc.player.distanceTo(entity);
            if ((entity instanceof TntEntity || entity instanceof TntMinecartEntity)
                  && distance <= this.tntDistance.get()) {
               return true;
            }
         }

         return false;
      }
   }

   private float getAbsorption() {
      return mc.player.getAbsorptionAmount();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.lockHeld) {
         MovementManager.getInstance().unlockMovement("AutoTotem");
         this.lockHeld = false;
      }

      this.resetSwapBack();
   }
}
