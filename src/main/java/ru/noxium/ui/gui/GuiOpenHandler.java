package ru.noxium.ui.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.event.EventInit;
import ru.noxium.event.input.KeyInputEvent;

@Environment(EnvType.CLIENT)
public class GuiOpenHandler {
   @EventInit
   public void onKeyInput(KeyInputEvent event) {
   }
}
