package ru.noxium.module.impl.visuals;

import org.lwjgl.glfw.GLFW;
import ru.noxium.event.EventInit;
import ru.noxium.event.EventType;
import ru.noxium.event.input.MouseButtonEvent;
import ru.noxium.module.api.Category;
import ru.noxium.module.api.IModule;
import ru.noxium.module.api.Module;
import ru.noxium.module.api.setting.Setting;
import ru.noxium.module.api.setting.impl.ModeSetting;

@IModule(
   name = "ClickGUI",
   description = "Opens click GUI menu",
   category = Category.Visuals,
   bind = GLFW.GLFW_KEY_RIGHT_SHIFT
)
public class ClickGUI extends Module {

   private boolean guiOpen = false;
   public static ModeSetting guiStyle = new ModeSetting("Стиль GUI", "Современный", "Современный", "Классический");

   public ClickGUI() {
      this.addSettings(new Setting[] { guiStyle });
   }

   @EventInit(value = EventType.PRE)
   public void onMouseInput(MouseButtonEvent event) {
      // Check for right mouse button click
      if (event.button() == 1 && event.isPress()) { // Right click
         if (mc.currentScreen != null) return; // Don't open GUI when in another screen
         
         if (guiOpen) {
            closeGui();
         } else {
            openGui();
         }
      }
   }

   private boolean isGuiOpen() {
      return guiOpen;
   }

   private void openGui() {
      guiOpen = true;
      // The actual GUI would need to be implemented
      // This is a placeholder for the basic structure
   }

   private void closeGui() {
      guiOpen = false;
   }

   @Override
   public void onEnable() {
      // Module enabled logic
   }

   @Override
   public void onDisable() {
      // Close GUI when module is disabled
      if (guiOpen) {
         closeGui();
      }
   }
}
