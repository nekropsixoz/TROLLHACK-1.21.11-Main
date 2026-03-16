package ru.noxium.ui.gui.component.main;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.Noxium;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.util.render.math.animation.anim.util.Easings;

@Environment(EnvType.CLIENT)
public class GuiKeyPressed extends GuiScreen {
   public static boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (GuiScreen.activeModuleBind != null) {
         if (keyCode == 256) {
            GuiScreen.activeModuleBind.binding = false;
            GuiScreen.activeModuleBind = null;
         } else if (keyCode == 261) {
            GuiScreen.activeModuleBind.bind = -1;
            GuiScreen.activeModuleBind.binding = false;
            GuiScreen.getModuleBindAnimation(GuiScreen.activeModuleBind).run(0.0, 0.2F, Easings.SINE_OUT);
            GuiScreen.activeModuleBind = null;
            if (Noxium.get.configManager != null) {
               Noxium.get.configManager.autoSave();
            }
         } else {
            GuiScreen.activeModuleBind.bind = keyCode;
            GuiScreen.activeModuleBind.binding = false;
            GuiScreen.getModuleBindAnimation(GuiScreen.activeModuleBind).run(1.0, 0.2F, Easings.SINE_OUT);
            GuiScreen.activeModuleBind = null;
            if (Noxium.get.configManager != null) {
               Noxium.get.configManager.autoSave();
            }
         }

         return true;
      } else if (GuiScreen.activeBindSetting != null) {
         if (keyCode == 256) {
            GuiScreen.activeBindSetting.active = false;
            GuiScreen.activeBindSetting = null;
         } else if (keyCode == 261) {
            GuiScreen.activeBindSetting.key = -1;
            GuiScreen.activeBindSetting.active = false;
            GuiScreen.activeBindSetting = null;
            if (Noxium.get.configManager != null) {
               Noxium.get.configManager.autoSave();
            }
         } else {
            GuiScreen.activeBindSetting.key = keyCode;
            GuiScreen.activeBindSetting.active = false;
            GuiScreen.activeBindSetting = null;
            if (Noxium.get.configManager != null) {
               Noxium.get.configManager.autoSave();
            }
         }

         return true;
      } else {
         if (GuiScreen.activeStringSetting != null) {
            if (keyCode == 256) {
               GuiScreen.activeStringSetting.active = false;
               GuiScreen.activeStringSetting = null;
               if (Noxium.get.configManager != null) {
                  Noxium.get.configManager.autoSave();
               }

               return true;
            }

            if (keyCode == 259) {
               if (!GuiScreen.activeStringSetting.input.isEmpty()) {
                  GuiScreen.activeStringSetting.input = GuiScreen.activeStringSetting.input.substring(0, GuiScreen.activeStringSetting.input.length() - 1);
                  if (Noxium.get.configManager != null) {
                     Noxium.get.configManager.autoSave();
                  }
               }

               return true;
            }
         }

         if (GuiScreen.activeSearch) {
            if (keyCode == 256) {
               GuiScreen.activeSearch = false;
               GuiScreen.searchText = "";
               return true;
            }

            if (keyCode == 259) {
               return true;
            }
         }

         return false;
      }
   }
}
