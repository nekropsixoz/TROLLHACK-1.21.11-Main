package ru.noxium.module.impl.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import ru.noxium.event.EventInit;
import ru.noxium.event.impl.EventUpdate;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;

@IModule(
   name = "SP Joiner",
   category = Category.Player,
   description = "Автоматический джойнер для SP",
   bind = -1
)
@Environment(EnvType.CLIENT)
public class SPJoiner extends Module {
   @EventInit
   public void onEvent(EventUpdate event) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null) {
         if (mc.currentScreen instanceof HandledScreen<?> handledScreen) {
            ScreenHandler handler = handledScreen.getScreenHandler();

            for (int i = 0; i < handler.slots.size(); i++) {
               Slot slot = (Slot)handler.slots.get(i);
               ItemStack stack = slot.getStack();
               if (stack.isOf(Items.STICKY_PISTON)) {
                  mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                  return;
               }
            }
         } else {
            selectCompass();
         }
      }
   }

   public static void selectCompass() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null) {
         PlayerInventory inv = mc.player.getInventory();
         int slot = -1;

         for (int i = 0; i < 9; i++) {
            if (inv.getStack(i).isOf(Items.COMPASS)) {
               slot = i;
               break;
            }
         }

         if (slot != -1) {
            inv.setSelectedSlot(slot);
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
         }
      }
   }
}
