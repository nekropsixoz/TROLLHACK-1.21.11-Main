package ru.noxium.module.impl.movement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Hand;
import ru.noxium.event.EventInit;
import ru.noxium.event.lifecycle.ClientTickEvent;
import ru.noxium.event.player.SlowWalkingEvent;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.ModeSetting;

@IModule(
   name = "No Slow",
   description = "",
   category = Category.Movement,
   bind = -1
)
@Environment(EnvType.CLIENT)
public class NoSlow extends Module {
   public static ModeSetting mode = new ModeSetting("Режим", "Grim", "Grim", "Grim Tick", "Grim 2.3.73-101473b");
   private float ticks = 0.0F;

   public NoSlow() {
      this.addSettings(new Setting[]{mode});
   }

   @EventInit
   public void onTick(ClientTickEvent event) {
      if (mc.player != null) {
         if (mode.is("Grim Tick") || mode.is("Grim 2.3.73-101473b")) {
            if (mc.player.isUsingItem()) {
               this.ticks++;
            } else {
               this.ticks = 0.0F;
            }
         }
      }
   }

   @EventInit
   public void onSlowWalking(SlowWalkingEvent event) {
      Hand first = mc.player.getActiveHand();
      Hand second = first.equals(Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
      if (mode.is("Grim")) {
         if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
         } else {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
         }

         event.cancel();
      }

      if (mode.is("Grim 2.3.73-101473b") && mc.player != null && mc.player.isUsingItem() && !mc.player.hasVehicle() && this.ticks >= 1.3F) {
         event.cancel();
         this.ticks = 0.26F;
      }

      if (mode.is("Grim Tick") && mc.player != null && mc.player.isUsingItem() && !mc.player.hasVehicle() && this.ticks >= 1.2F) {
         event.cancel();
         this.ticks = 0.0F;
      }
   }
}
