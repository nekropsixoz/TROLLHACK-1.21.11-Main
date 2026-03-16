package ru.noxium.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import ru.noxium.event.EventInit;
import ru.noxium.event.EventType;
import ru.noxium.event.input.KeyInputEvent;
import ru.noxium.event.input.MouseButtonEvent;
import ru.noxium.event.lifecycle.ClientTickEvent;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.BindSettings;
import ru.noxium.module.api.setting.impl.BooleanSetting;
import ru.noxium.module.api.setting.impl.ModeSetting;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@IModule(
   name = "TNTMinecart",
   description = "Automatically places and ignites TNT minecarts",
   category = Category.Combat,
   bind = -1
)
public class TNTMinecart extends Module {

   final BindSettings activateKey = new BindSettings("ActivateKey", -1);
   final ModeSetting modeSetting = new ModeSetting("Mode", "Pre-Rail", "Pre-Rail", "Insta-Cart");
   final BooleanSetting silentRotation = new BooleanSetting("SilentRotation", false);
   final BooleanSetting autoSwitch = new BooleanSetting("AutoSwitch", true);

   enum State {
      IDLE,
      PLACING_RAIL,
      DRAWING_BOW,
      SHOOTING,
      PLACING_MINECART,
      INSTA_DRAWING_BOW,
      INSTA_SHOOTING,
      INSTA_PLACING_RAIL,
      INSTA_PLACING_MINECART,
      DONE
   }

   State currentState = State.IDLE;
   int actionTimer = 0;
   int originalSlot = 0;
   int railSlot = -1;
   int bowSlot = -1;
   int tntMinecartSlot = -1;
   BlockHitResult targetHit = null;
   boolean bowStarted = false;
   float originalPitch = 0;
   float originalYaw = 0;

   public TNTMinecart() {
      super();
      this.addSettings(new Setting[]{activateKey, modeSetting, silentRotation, autoSwitch});
   }

   @Override
   public void onDisable() {
      reset();
   }

   @EventInit(value = EventType.PRE)
   public void onKey(KeyInputEvent e) {
      if (mc.player == null || mc.world == null) return;
      if (mc.currentScreen != null) return;
      if (currentState != State.IDLE) return;
      if (e.action() != 1) return;

      int bindKey = activateKey.key;

      if (bindKey == GLFW.GLFW_KEY_UNKNOWN || bindKey == -1) return;

      boolean matches = e.key() == bindKey;

      if (matches) {
         execute();
      }
   }

   @EventInit(value = EventType.PRE)
   public void onMouse(MouseButtonEvent e) {
      if (mc.player == null || mc.world == null) return;
      if (mc.currentScreen != null) return;
      if (currentState != State.IDLE) return;
      if (e.action() != 1) return;

      int bindKey = activateKey.key;

      if (bindKey == GLFW.GLFW_KEY_UNKNOWN || bindKey == -1) return;

      boolean matches = e.button() == bindKey;

      if (matches) {
         execute();
      }
   }

   @EventInit(value = EventType.PRE)
   public void onTick(ClientTickEvent e) {
      if (mc.player == null || mc.world == null) return;

      if (currentState != State.IDLE) {
         processTick();
      }
   }

   private void execute() {
      PlayerInventory inventory = mc.player.getInventory();

      tntMinecartSlot = findTNTMinecart(inventory);
      railSlot = findRail(inventory);
      bowSlot = findBow(inventory);

      if (tntMinecartSlot == -1 || railSlot == -1 || bowSlot == -1) {
         return;
      }

      HitResult hit = mc.crosshairTarget;
      if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
         return;
      }

      targetHit = (BlockHitResult) hit;
      originalSlot = inventory.getSelectedSlot();
      originalPitch = mc.player.getPitch();
      originalYaw = mc.player.getYaw();
      actionTimer = 0;
      bowStarted = false;

      if (modeSetting.is("Pre-Rail")) {
         currentState = State.PLACING_RAIL;
      } else {
         currentState = State.INSTA_DRAWING_BOW;
      }
   }

   private void processTick() {
      if (mc.player == null || mc.interactionManager == null || mc.world == null) {
         reset();
         return;
      }

      actionTimer++;

      try {
         if (modeSetting.is("Pre-Rail")) {
            processPreRailMode();
         } else {
            processInstaCartMode();
         }
      } catch (Exception ex) {
         reset();
      }
   }

   private void processPreRailMode() {
      switch (currentState) {
         case PLACING_RAIL:
            if (actionTimer == 1) {
               if (autoSwitch.get()) {
                  mc.player.getInventory().setSelectedSlot(railSlot);
               }
               mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, targetHit);
               currentState = State.DRAWING_BOW;
               actionTimer = 0;
            }
            break;

         case DRAWING_BOW:
            if (actionTimer == 1) {
               if (autoSwitch.get()) {
                  mc.player.getInventory().setSelectedSlot(bowSlot);
               }
               mc.options.useKey.setPressed(true);
               bowStarted = true;
               currentState = State.SHOOTING;
               actionTimer = 0;
            }
            break;

         case SHOOTING:
            if (actionTimer == 5) {
               if (!silentRotation.get()) {
                  mc.player.setPitch(mc.player.getPitch() - 12.0f);
               }
            } else if (actionTimer == 6) {
               if (bowStarted) {
                  mc.options.useKey.setPressed(false);
                  bowStarted = false;
               }
            } else if (actionTimer >= 7) {
               if (!silentRotation.get()) {
                  mc.player.setPitch(originalPitch);
                  mc.player.setYaw(originalYaw);
               }
               currentState = State.PLACING_MINECART;
               actionTimer = 0;
            }
            break;

         case PLACING_MINECART:
            if (actionTimer == 1) {
               if (autoSwitch.get()) {
                  mc.player.getInventory().setSelectedSlot(tntMinecartSlot);
               }
               mc.options.useKey.setPressed(true);
            } else if (actionTimer == 2) {
               mc.options.useKey.setPressed(false);
               currentState = State.DONE;
               actionTimer = 0;
            }
            break;

         case DONE:
            if (actionTimer >= 1) {
               if (autoSwitch.get()) {
                  mc.player.getInventory().setSelectedSlot(originalSlot);
               }
               reset();
            }
            break;
      }
   }

   private void processInstaCartMode() {
      switch (currentState) {
         case INSTA_DRAWING_BOW:
            if (actionTimer == 1) {
               if (autoSwitch.get()) {
                  mc.player.getInventory().setSelectedSlot(bowSlot);
               }
               mc.options.useKey.setPressed(true);
               bowStarted = true;
               currentState = State.INSTA_SHOOTING;
               actionTimer = 0;
            }
            break;

         case INSTA_SHOOTING:
            if (actionTimer == 5) {
               if (!silentRotation.get()) {
                  mc.player.setPitch(mc.player.getPitch() - 12.0f);
               }
            } else if (actionTimer == 6) {
               if (bowStarted) {
                  mc.options.useKey.setPressed(false);
                  bowStarted = false;
               }
            } else if (actionTimer >= 7) {
               if (!silentRotation.get()) {
                  mc.player.setPitch(originalPitch);
                  mc.player.setYaw(originalYaw);
               }
               currentState = State.INSTA_PLACING_RAIL;
               actionTimer = 0;
            }
            break;

         case INSTA_PLACING_RAIL:
            if (actionTimer == 1) {
               if (autoSwitch.get()) {
                  mc.player.getInventory().setSelectedSlot(railSlot);
               }
               mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, targetHit);
               currentState = State.INSTA_PLACING_MINECART;
               actionTimer = 0;
            }
            break;

         case INSTA_PLACING_MINECART:
            if (actionTimer == 1) {
               if (autoSwitch.get()) {
                  mc.player.getInventory().setSelectedSlot(tntMinecartSlot);
               }
               mc.options.useKey.setPressed(true);
            } else if (actionTimer == 2) {
               mc.options.useKey.setPressed(false);
               currentState = State.DONE;
               actionTimer = 0;
            }
            break;

         case DONE:
            if (actionTimer >= 1) {
               if (autoSwitch.get()) {
                  mc.player.getInventory().setSelectedSlot(originalSlot);
               }
               reset();
            }
            break;
      }
   }

   private void reset() {
      if (bowStarted && mc.options != null) {
         mc.options.useKey.setPressed(false);
      }
      currentState = State.IDLE;
      actionTimer = 0;
      bowStarted = false;
      targetHit = null;
      originalPitch = 0;
      originalYaw = 0;
   }

   private int findTNTMinecart(PlayerInventory inventory) {
      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.getItem() == Items.TNT_MINECART) {
            return i;
         }
      }
      return -1;
   }

   private int findRail(PlayerInventory inventory) {
      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.getItem() == Items.RAIL || 
             stack.getItem() == Items.POWERED_RAIL ||
             stack.getItem() == Items.DETECTOR_RAIL ||
             stack.getItem() == Items.ACTIVATOR_RAIL) {
            return i;
         }
      }
      return -1;
   }

   private int findBow(PlayerInventory inventory) {
      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.getItem() == Items.BOW) {
            return i;
         }
      }
      return -1;
   }
}
