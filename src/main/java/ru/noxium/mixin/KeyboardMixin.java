package ru.noxium.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.noxium.Noxium;
import ru.noxium.event.EventManager;
import ru.noxium.event.input.KeyInputEvent;
import ru.noxium.module.impl.client.MenuSettingsModule;
import ru.noxium.ui.gui.GuiClient;

@Environment(EnvType.CLIENT)
@Mixin({Keyboard.class})
public class KeyboardMixin {
   @Inject(
      method = {"onKey"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void handleMenuKeyEvent(long window, int action, KeyInput input, CallbackInfo ci) {
      if (Noxium.isModInitialized() && Noxium.get != null) {
         MinecraftClient client = MinecraftClient.getInstance();
         if (client != null && client.getWindow() != null && client.currentScreen == null) {
            KeyInputEvent event = new KeyInputEvent(window, input.key(), input.scancode(), action, input.modifiers());
            EventManager.call(event);
            if (event.action() == 1 && client.currentScreen == null) {
               MenuSettingsModule module = MenuSettingsModule.getInstanceIfAvailable();
               int menuKey = module != null && module.bind != -1 ? module.bind : 344;
               if (menuKey != -1 && event.key() == menuKey) {
                  GuiClient gui = Noxium.get.getGuiClient();
                  if (gui != null) {
                     client.setScreen(gui);
                     if (client.mouse != null) {
                        client.mouse.unlockCursor();
                     }

                     event.cancel();
                  }
               }
            }

            if (event.isCancelled()) {
               ci.cancel();
            }
         }
      }
   }
}
