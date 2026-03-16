package ru.noxium.ui.gui.component.mouse;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.noxium.ui.gui.GuiScreen;
import ru.noxium.Noxium;
import ru.noxium.module.impl.visuals.ClickGUI;
import ru.noxium.ui.gui.component.mouse.category.GuiMouseClickedCategory;
import ru.noxium.ui.gui.component.mouse.colorpicker.GuiMouseClickedColorPicker;
import ru.noxium.ui.gui.component.mouse.module.GuiMouseClickedModule;
import ru.noxium.ui.gui.component.render.GuiRenderMain;

import ru.noxium.ui.gui.theme.ThemeScreen;
import ru.noxium.util.render.core.Renderer2D;
import ru.noxium.util.render.math.ScaleHelper;

@Environment(EnvType.CLIENT)
public class GuiMouseClicked extends GuiScreen {
   public static boolean mouseClicked(Renderer2D renderer2D, double pMouseX, double pMouseY, int pButton) {
      int mouseX = (int) ScaleHelper.calc((float) pMouseX, (float) pMouseY)[0];
      int mouseY = (int) ScaleHelper.calc((float) pMouseX, (float) pMouseY)[1];

      // Если выбран классический стиль, используем его обработчик
      if (ClickGUI.guiStyle.is("Классический")) {
         return GuiMouseClickedClassic.mouseClickedClassic(renderer2D, mouseX, mouseY, pButton);
      }

      if (!GuiScreen.exit) {
         float searchX = GuiScreen.x + 111.885F;
         float searchY = GuiScreen.y + 6.185F;
         float searchWidth = 124.04F;
         float searchHeight = 21.325F;
         if (pButton == 0 && GuiRenderMain.isHovered(mouseX, mouseY, searchX, searchY, searchWidth, searchHeight)) {
            GuiScreen.activeSearch = true;
            return true;
         }

         GuiMouseClickedCategory.mouseClickedCategory(mouseX, mouseY);
         if (GuiMouseClickedColorPicker.mouseClickedColorPicker(mouseX, mouseY, pButton)) {
            return true;
         }

         if (GuiMouseClickedModule.mouseClickedModule(renderer2D, mouseX, mouseY, pButton)) {
            return true;
         }

         ThemeScreen.mouseClickedTheme(pMouseX, pMouseY, pButton);
      }

      if (GuiScreen.activeBindSetting != null && pButton >= 0 && pButton <= 10) {
         int mouseKey = -100 - pButton;
         GuiScreen.activeBindSetting.key = mouseKey;
         GuiScreen.activeBindSetting.active = false;
         GuiScreen.activeBindSetting = null;
         if (Noxium.get.configManager != null) {
            Noxium.get.configManager.autoSave();
         }
         return true;
      } else if (GuiScreen.activeModuleBind != null && pButton >= 0 && pButton <= 10) {
         int mouseKey = -100 - pButton;
         GuiScreen.activeModuleBind.bind = mouseKey;
         GuiScreen.activeModuleBind.binding = false;
         GuiScreen.activeModuleBind = null;
         if (Noxium.get.configManager != null) {
            Noxium.get.configManager.autoSave();
         }
         return true;
      } else {
         return false;
      }
   }
}
